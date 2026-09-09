package com.indumentaria.ui;

import com.indumentaria.db.ConexionDB;
import com.indumentaria.util.PasswordUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Ventana para registrar un usuario nuevo (admin o vendedor).
 */
public class RegistroDialog extends JDialog {

    private JTextField campoUsuario;
    private JPasswordField campoPassword;
    private JComboBox<String> comboRol;

    public RegistroDialog(JFrame padre) {
        super(padre, "Registro de usuario", true);
        armarUI();
        setSize(340, 240);
        setLocationRelativeTo(padre);
        setResizable(false);
    }

    private void armarUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        campoUsuario = new JTextField(15);
        panel.add(campoUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        campoPassword = new JPasswordField(15);
        panel.add(campoPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1;
        comboRol = new JComboBox<>(new String[] {"vendedor", "admin"});
        panel.add(comboRol, gbc);

        JButton botonGuardar = new JButton("Registrar");
        botonGuardar.addActionListener(e -> registrar());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(botonGuardar, gbc);

        add(panel);
    }

    private void registrar() {
        String usuario = campoUsuario.getText().trim();
        String password = new String(campoPassword.getPassword());
        String rol = (String) comboRol.getSelectedItem();

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete usuario y contraseña.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO usuarios (username, password_hash, rol) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, PasswordUtil.hashear(password));
            ps.setString(3, rol);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.");
            dispose();
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                JOptionPane.showMessageDialog(this, "Ese nombre de usuario ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Ocurrió un error", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
