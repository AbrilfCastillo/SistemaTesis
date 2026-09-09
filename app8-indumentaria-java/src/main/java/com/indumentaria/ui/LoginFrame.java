package com.indumentaria.ui;

import com.indumentaria.db.ConexionDB;
import com.indumentaria.modelo.Usuario;
import com.indumentaria.util.PasswordUtil;
import com.indumentaria.util.SeedData;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Pantalla de login. Tambien permite ir al registro de usuario nuevo
 * y cargar los datos de ejemplo para poder probar la aplicacion.
 */
public class LoginFrame extends JFrame {

    private JTextField campoUsuario;
    private JPasswordField campoPassword;

    public LoginFrame() {
        super("Indumentaria - Ingreso");
        armarUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 260);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void armarUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Sistema de gestión Indumentaria");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        campoUsuario = new JTextField(15);
        panel.add(campoUsuario, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        campoPassword = new JPasswordField(15);
        panel.add(campoPassword, gbc);

        JButton botonIngresar = new JButton("Ingresar");
        botonIngresar.addActionListener(e -> intentarLogin());
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(botonIngresar, gbc);

        JButton botonRegistro = new JButton("Registrarse");
        botonRegistro.addActionListener(e -> new RegistroDialog(this).setVisible(true));
        gbc.gridy = 4;
        panel.add(botonRegistro, gbc);

        JButton botonSeed = new JButton("Cargar datos de ejemplo");
        botonSeed.addActionListener(e -> cargarDatosDeEjemplo());
        gbc.gridy = 5;
        panel.add(botonSeed, gbc);

        add(panel);
    }

    private void cargarDatosDeEjemplo() {
        SeedData.cargarDatosDeEjemplo();
        JOptionPane.showMessageDialog(this, "Datos de ejemplo cargados (si ya existían, no se duplican).");
    }

    private void intentarLogin() {
        String usuario = campoUsuario.getText().trim();
        String password = new String(campoPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete usuario y contraseña.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT id, username, password_hash, rol FROM usuarios WHERE username = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de ingreso", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String hashGuardado = rs.getString("password_hash");
                if (!PasswordUtil.coincide(password, hashGuardado)) {
                    JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de ingreso", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Usuario usuarioLogueado = new Usuario(rs.getInt("id"), rs.getString("username"), rs.getString("rol"));
                MainFrame main = new MainFrame(usuarioLogueado);
                main.setVisible(true);
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
