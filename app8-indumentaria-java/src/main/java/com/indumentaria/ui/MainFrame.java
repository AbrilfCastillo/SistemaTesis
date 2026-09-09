package com.indumentaria.ui;

import com.indumentaria.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal luego del login. Contiene las distintas secciones
 * en solapas: productos, ventas e historial.
 */
public class MainFrame extends JFrame {

    private final Usuario usuarioActual;

    public MainFrame(Usuario usuarioActual) {
        super("Indumentaria - Panel principal");
        this.usuarioActual = usuarioActual;
        armarUI();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void armarUI() {
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.add(new JLabel("Conectado como: " + usuarioActual.getUsername() + " (" + usuarioActual.getRol() + ")"));

        JButton botonSalir = new JButton("Cerrar sesión");
        botonSalir.addActionListener(e -> cerrarSesion());
        JPanel panelSuperiorDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSuperiorDerecha.add(botonSalir);

        JPanel panelBarra = new JPanel(new BorderLayout());
        panelBarra.add(panelSuperior, BorderLayout.WEST);
        panelBarra.add(panelSuperiorDerecha, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Productos", new ProductosPanel(usuarioActual));
        tabs.addTab("Registrar venta", new VentasPanel(usuarioActual));
        tabs.addTab("Historial de ventas", new HistorialVentasPanel());

        setLayout(new BorderLayout());
        add(panelBarra, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private void cerrarSesion() {
        dispose();
        LoginFrame login = new LoginFrame();
        login.setVisible(true);
    }
}
