package com.indumentaria;

import com.indumentaria.db.ConexionDB;
import com.indumentaria.ui.LoginFrame;
import com.indumentaria.util.SeedData;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        ConexionDB.inicializarEsquema();

        boolean seed = false;
        for (String arg : args) {
            if ("--seed".equals(arg)) {
                seed = true;
            }
        }
        if (seed) {
            SeedData.cargarDatosDeEjemplo();
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Ocurrió un error");
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
