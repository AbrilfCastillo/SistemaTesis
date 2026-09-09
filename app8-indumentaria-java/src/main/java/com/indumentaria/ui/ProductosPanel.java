package com.indumentaria.ui;

import com.indumentaria.db.ConexionDB;
import com.indumentaria.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Panel de ABM de productos. Las consultas SQL se hacen directamente
 * desde acá, junto con el manejo de los botones y la tabla.
 */
public class ProductosPanel extends JPanel {

    private final Usuario usuarioActual;
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextField campoBusqueda;

    public ProductosPanel(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        armarUI();
        cargarProductos(null);
    }

    private void armarUI() {
        setLayout(new BorderLayout(6, 6));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        campoBusqueda = new JTextField(20);
        JButton botonBuscar = new JButton("Buscar");
        botonBuscar.addActionListener(e -> cargarProductos(campoBusqueda.getText().trim()));
        JButton botonRefrescar = new JButton("Refrescar");
        botonRefrescar.addActionListener(e -> {
            campoBusqueda.setText("");
            cargarProductos(null);
        });
        panelBusqueda.add(new JLabel("Buscar por nombre:"));
        panelBusqueda.add(campoBusqueda);
        panelBusqueda.add(botonBuscar);
        panelBusqueda.add(botonRefrescar);

        modeloTabla = new DefaultTableModel(new Object[] {"ID", "Nombre", "Precio", "Stock", "Categoría"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botonNuevo = new JButton("Nuevo producto");
        botonNuevo.addActionListener(e -> abrirNuevo());
        JButton botonEditar = new JButton("Editar producto");
        botonEditar.addActionListener(e -> abrirEditar());
        JButton botonEliminar = new JButton("Eliminar producto");
        botonEliminar.addActionListener(e -> eliminarSeleccionado());
        panelBotones.add(botonNuevo);
        panelBotones.add(botonEditar);
        panelBotones.add(botonEliminar);

        add(panelBusqueda, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarProductos(String filtroNombre) {
        modeloTabla.setRowCount(0);

        String sql = "SELECT id, nombre, precio, stock, categoria FROM productos";
        if (filtroNombre != null && !filtroNombre.isEmpty()) {
            sql += " WHERE nombre LIKE ?";
        }
        sql += " ORDER BY nombre";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (filtroNombre != null && !filtroNombre.isEmpty()) {
                ps.setString(1, "%" + filtroNombre + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modeloTabla.addRow(new Object[] {
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("precio"),
                            rs.getInt("stock"),
                            rs.getString("categoria")
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirNuevo() {
        Window ventana = SwingUtilities.getWindowAncestor(this);
        ProductoDialog dialog = new ProductoDialog((Frame) ventana, null);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            cargarProductos(null);
        }
    }

    private void abrirEditar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Window ventana = SwingUtilities.getWindowAncestor(this);
        ProductoDialog dialog = new ProductoDialog((Frame) ventana, id);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            cargarProductos(null);
        }
    }

    private void eliminarSeleccionado() {
        if (!usuarioActual.esAdmin()) {
            JOptionPane.showMessageDialog(this, "No tiene permisos para esta acción.", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        String nombre = (String) modeloTabla.getValueAt(fila, 1);
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el producto \"" + nombre + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            cargarProductos(null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
