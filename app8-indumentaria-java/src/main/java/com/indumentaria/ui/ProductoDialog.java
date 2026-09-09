package com.indumentaria.ui;

import com.indumentaria.db.ConexionDB;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Ventana de alta/edición de producto. Si se pasa un id en el constructor
 * se abre en modo edición y se cargan los datos actuales del producto;
 * si es null, se abre en modo alta.
 *
 * En modo edición los datos se leen una sola vez al abrir la ventana.
 */
public class ProductoDialog extends JDialog {

    private final Integer productoId;
    private boolean guardado = false;

    private JTextField campoNombre;
    private JTextField campoPrecio;
    private JTextField campoStock;
    private JComboBox<String> comboCategoria;

    public ProductoDialog(Frame padre, Integer productoId) {
        super(padre, productoId == null ? "Nuevo producto" : "Editar producto", true);
        this.productoId = productoId;
        armarUI();
        if (productoId != null) {
            cargarDatosProducto();
        }
        setSize(360, 280);
        setLocationRelativeTo(padre);
        setResizable(false);
    }

    public boolean isGuardado() {
        return guardado;
    }

    private void armarUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        campoNombre = new JTextField(15);
        panel.add(campoNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1;
        campoPrecio = new JTextField(15);
        panel.add(campoPrecio, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1;
        campoStock = new JTextField(15);
        panel.add(campoStock, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 1;
        comboCategoria = new JComboBox<>(new String[] {"ropa", "calzado", "accesorios", "accesorios electronicos"});
        comboCategoria.setEditable(true);
        panel.add(comboCategoria, gbc);

        JButton botonGuardar = new JButton("Guardar");
        botonGuardar.addActionListener(e -> guardar());
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(botonGuardar, gbc);

        add(panel);
    }

    private void cargarDatosProducto() {
        String sql = "SELECT nombre, precio, stock, categoria FROM productos WHERE id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    campoNombre.setText(rs.getString("nombre"));
                    campoPrecio.setText(rs.getString("precio"));
                    campoStock.setText(String.valueOf(rs.getInt("stock")));
                    comboCategoria.setSelectedItem(rs.getString("categoria"));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardar() {
        String nombre = campoNombre.getText().trim();
        String precioTexto = campoPrecio.getText().trim();
        String stockTexto = campoStock.getText().trim();
        String categoria = (String) comboCategoria.getEditor().getItem();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (precioTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El precio es obligatorio.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioTexto.replace(",", "."));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int stock = 0;
        if (!stockTexto.isEmpty()) {
            try {
                stock = Integer.parseInt(stockTexto);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El stock debe ser un número entero.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (productoId == null) {
            insertar(nombre, precio, stock, categoria);
        } else {
            actualizar(nombre, precio, stock, categoria);
        }
    }

    private void insertar(String nombre, double precio, int stock, String categoria) {
        String sql = "INSERT INTO productos (nombre, precio, stock, categoria) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, String.valueOf(precio));
            ps.setInt(3, stock);
            ps.setString(4, categoria);
            ps.executeUpdate();
            guardado = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar(String nombre, double precio, int stock, String categoria) {
        String sql = "UPDATE productos SET nombre = ?, precio = ?, stock = ?, categoria = ? WHERE id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, String.valueOf(precio));
            ps.setInt(3, stock);
            ps.setString(4, categoria);
            ps.setInt(5, productoId);
            ps.executeUpdate();
            guardado = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
