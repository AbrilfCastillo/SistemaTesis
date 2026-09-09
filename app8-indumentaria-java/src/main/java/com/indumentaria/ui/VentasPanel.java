package com.indumentaria.ui;

import com.indumentaria.db.ConexionDB;
import com.indumentaria.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel para registrar una venta. Permite armar un carrito con varios
 * productos y confirmar la venta, descontando stock y calculando el total
 * con IVA y el descuento mayorista.
 */
public class VentasPanel extends JPanel {

    private static final double IVA = 0.21;
    private static final double DESCUENTO_MAYORISTA = 0.10;
    private static final int UMBRAL_MAYORISTA = 10;

    private final Usuario usuarioActual;

    private JComboBox<ProductoCombo> comboProductos;
    private JTextField campoCantidad;
    private JLabel labelStockDisponible;

    private DefaultTableModel modeloCarrito;
    private JTable tablaCarrito;
    private JLabel labelTotal;

    // carrito: productoId -> {nombre, precioUnitario, cantidad}
    private final List<ItemCarrito> carrito = new ArrayList<>();

    public VentasPanel(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        armarUI();
        cargarProductosCombo();
    }

    private void armarUI() {
        setLayout(new BorderLayout(6, 6));

        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboProductos = new JComboBox<>();
        comboProductos.addActionListener(e -> actualizarStockDisponible());
        campoCantidad = new JTextField(5);
        labelStockDisponible = new JLabel("Stock disponible: -");
        JButton botonAgregar = new JButton("Agregar al carrito");
        botonAgregar.addActionListener(e -> agregarAlCarrito());

        panelSeleccion.add(new JLabel("Producto:"));
        panelSeleccion.add(comboProductos);
        panelSeleccion.add(new JLabel("Cantidad:"));
        panelSeleccion.add(campoCantidad);
        panelSeleccion.add(labelStockDisponible);
        panelSeleccion.add(botonAgregar);

        modeloCarrito = new DefaultTableModel(new Object[] {"Producto", "Cantidad", "Precio unitario", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCarrito = new JTable(modeloCarrito);
        JScrollPane scroll = new JScrollPane(tablaCarrito);

        JPanel panelInferior = new JPanel(new BorderLayout());
        JButton botonQuitar = new JButton("Quitar seleccionado");
        botonQuitar.addActionListener(e -> quitarDelCarrito());
        JButton botonConfirmar = new JButton("Registrar venta");
        botonConfirmar.addActionListener(e -> registrarVenta());

        JPanel panelBotonesCarrito = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotonesCarrito.add(botonQuitar);
        panelBotonesCarrito.add(botonConfirmar);

        labelTotal = new JLabel("Total: $0.00");
        labelTotal.setFont(labelTotal.getFont().deriveFont(Font.BOLD, 13f));
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.add(labelTotal);

        panelInferior.add(panelBotonesCarrito, BorderLayout.WEST);
        panelInferior.add(panelTotal, BorderLayout.EAST);

        add(panelSeleccion, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void cargarProductosCombo() {
        comboProductos.removeAllItems();
        String sql = "SELECT id, nombre, precio, stock FROM productos ORDER BY nombre";
        try (Connection con = ConexionDB.obtenerConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                comboProductos.addItem(new ProductoCombo(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        Double.parseDouble(rs.getString("precio")),
                        rs.getInt("stock")));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error", "Error", JOptionPane.ERROR_MESSAGE);
        }
        actualizarStockDisponible();
    }

    private void actualizarStockDisponible() {
        ProductoCombo seleccionado = (ProductoCombo) comboProductos.getSelectedItem();
        if (seleccionado == null) {
            labelStockDisponible.setText("Stock disponible: -");
        } else {
            labelStockDisponible.setText("Stock disponible: " + seleccionado.stock);
        }
    }

    private void agregarAlCarrito() {
        ProductoCombo seleccionado = (ProductoCombo) comboProductos.getSelectedItem();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "No hay productos cargados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cantidadTexto = campoCantidad.getText().trim();
        if (cantidadTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadTexto);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidadYaEnCarrito = 0;
        for (ItemCarrito item : carrito) {
            if (item.productoId == seleccionado.id) {
                cantidadYaEnCarrito = item.cantidad;
                break;
            }
        }

        if (cantidad + cantidadYaEnCarrito > seleccionado.stock) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente. Disponible: " + seleccionado.stock, "Stock insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean encontrado = false;
        for (ItemCarrito item : carrito) {
            if (item.productoId == seleccionado.id) {
                item.cantidad += cantidad;
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            carrito.add(new ItemCarrito(seleccionado.id, seleccionado.nombre, seleccionado.precio, cantidad));
        }

        campoCantidad.setText("");
        refrescarTablaCarrito();
    }

    private void quitarDelCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un ítem del carrito.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        carrito.remove(fila);
        refrescarTablaCarrito();
    }

    private void refrescarTablaCarrito() {
        modeloCarrito.setRowCount(0);
        double subtotalGeneral = 0;
        for (ItemCarrito item : carrito) {
            double subtotalItem = item.precioUnitario * item.cantidad;
            subtotalGeneral += subtotalItem;
            modeloCarrito.addRow(new Object[] {
                    item.nombre,
                    item.cantidad,
                    String.format("%.2f", item.precioUnitario),
                    String.format("%.2f", subtotalItem)
            });
        }
        labelTotal.setText("Total: $" + String.format("%.2f", calcularTotal(subtotalGeneral)));
    }

    private double calcularTotal(double subtotal) {
        int totalUnidades = 0;
        for (ItemCarrito item : carrito) {
            totalUnidades += item.cantidad;
        }
        double subtotalConDescuento = subtotal;
        if (totalUnidades >= UMBRAL_MAYORISTA) {
            subtotalConDescuento = subtotal * (1 - DESCUENTO_MAYORISTA);
        }
        return subtotalConDescuento * (1 + IVA);
    }

    private void registrarVenta() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un producto al carrito.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double subtotalGeneral = 0;
        for (ItemCarrito item : carrito) {
            subtotalGeneral += item.precioUnitario * item.cantidad;
        }
        double total = calcularTotal(subtotalGeneral);
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (Connection con = ConexionDB.obtenerConexion()) {
            int ventaId;
            try (PreparedStatement psVenta = con.prepareStatement(
                    "INSERT INTO ventas (usuario_id, fecha, total) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setInt(1, usuarioActual.getId());
                psVenta.setString(2, fecha);
                psVenta.setString(3, String.valueOf(total));
                psVenta.executeUpdate();
                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    rs.next();
                    ventaId = rs.getInt(1);
                }
            }

            try (PreparedStatement psDetalle = con.prepareStatement(
                    "INSERT INTO venta_detalle (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)");
                 PreparedStatement psStock = con.prepareStatement(
                    "UPDATE productos SET stock = stock - ? WHERE id = ?")) {

                for (ItemCarrito item : carrito) {
                    double subtotalItem = item.precioUnitario * item.cantidad;
                    psDetalle.setInt(1, ventaId);
                    psDetalle.setInt(2, item.productoId);
                    psDetalle.setInt(3, item.cantidad);
                    psDetalle.setString(4, String.valueOf(item.precioUnitario));
                    psDetalle.setString(5, String.valueOf(subtotalItem));
                    psDetalle.executeUpdate();

                    psStock.setInt(1, item.cantidad);
                    psStock.setInt(2, item.productoId);
                    psStock.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Venta registrada. Total: $" + String.format("%.2f", total));
            carrito.clear();
            refrescarTablaCarrito();
            cargarProductosCombo();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Item auxiliar para armar el carrito antes de confirmar la venta. */
    private static class ItemCarrito {
        int productoId;
        String nombre;
        double precioUnitario;
        int cantidad;

        ItemCarrito(int productoId, String nombre, double precioUnitario, int cantidad) {
            this.productoId = productoId;
            this.nombre = nombre;
            this.precioUnitario = precioUnitario;
            this.cantidad = cantidad;
        }
    }

    /** Envoltorio para mostrar productos en el combo con su nombre. */
    private static class ProductoCombo {
        int id;
        String nombre;
        double precio;
        int stock;

        ProductoCombo(int id, String nombre, double precio, int stock) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
            this.stock = stock;
        }

        @Override
        public String toString() {
            return nombre + " ($" + String.format("%.2f", precio) + ")";
        }
    }
}
