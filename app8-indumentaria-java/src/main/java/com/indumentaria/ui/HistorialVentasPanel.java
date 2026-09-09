package com.indumentaria.ui;

import com.indumentaria.db.ConexionDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Panel de historial de ventas. Al seleccionar una venta se puede ver
 * el detalle de productos vendidos en esa venta.
 */
public class HistorialVentasPanel extends JPanel {

    private DefaultTableModel modeloVentas;
    private JTable tablaVentas;

    private DefaultTableModel modeloDetalle;
    private JTable tablaDetalle;

    public HistorialVentasPanel() {
        armarUI();
        cargarVentas();
    }

    private void armarUI() {
        setLayout(new BorderLayout(6, 6));

        modeloVentas = new DefaultTableModel(new Object[] {"ID", "Usuario", "Fecha", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaVentas = new JTable(modeloVentas);
        tablaVentas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalleVentaSeleccionada();
            }
        });
        JScrollPane scrollVentas = new JScrollPane(tablaVentas);
        scrollVentas.setBorder(BorderFactory.createTitledBorder("Ventas"));

        modeloDetalle = new DefaultTableModel(new Object[] {"Producto", "Cantidad", "Precio unitario", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDetalle = new JTable(modeloDetalle);
        JScrollPane scrollDetalle = new JScrollPane(tablaDetalle);
        scrollDetalle.setBorder(BorderFactory.createTitledBorder("Detalle de la venta seleccionada"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollVentas, scrollDetalle);
        splitPane.setResizeWeight(0.6);

        JButton botonRefrescar = new JButton("Refrescar");
        botonRefrescar.addActionListener(e -> cargarVentas());
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.add(botonRefrescar);

        add(panelBotones, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void cargarVentas() {
        modeloVentas.setRowCount(0);
        modeloDetalle.setRowCount(0);

        String sql = "SELECT v.id, u.username, v.fecha, v.total " +
                "FROM ventas v LEFT JOIN usuarios u ON u.id = v.usuario_id " +
                "ORDER BY v.id DESC";

        try (Connection con = ConexionDB.obtenerConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                modeloVentas.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("fecha"),
                        rs.getString("total")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDetalleVentaSeleccionada() {
        modeloDetalle.setRowCount(0);
        int fila = tablaVentas.getSelectedRow();
        if (fila == -1) {
            return;
        }
        int ventaId = (int) modeloVentas.getValueAt(fila, 0);

        String sql = "SELECT p.nombre, d.cantidad, d.precio_unitario, d.subtotal " +
                "FROM venta_detalle d LEFT JOIN productos p ON p.id = d.producto_id " +
                "WHERE d.venta_id = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ventaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modeloDetalle.addRow(new Object[] {
                            rs.getString("nombre"),
                            rs.getInt("cantidad"),
                            rs.getString("precio_unitario"),
                            rs.getString("subtotal")
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
