package com.indumentaria.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Maneja la conexion a la base SQLite del proyecto.
 * El archivo indumentaria.db se crea en el directorio desde donde se ejecuta el jar.
 */
public class ConexionDB {

    private static final String URL = "jdbc:sqlite:indumentaria.db";

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Crea las tablas si todavia no existen, leyendo schema.sql del classpath.
     */
    public static void inicializarEsquema() {
        try (Connection con = obtenerConexion();
             Statement st = con.createStatement()) {

            String sql = leerSchemaSql();
            for (String sentencia : sql.split(";")) {
                String limpio = sentencia.trim();
                if (!limpio.isEmpty()) {
                    st.execute(limpio);
                }
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error");
        }
    }

    private static String leerSchemaSql() throws Exception {
        InputStream is = ConexionDB.class.getClassLoader().getResourceAsStream("schema.sql");
        if (is == null) {
            throw new IllegalStateException("No se encontro schema.sql en el classpath");
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                sb.append(linea).append("\n");
            }
        }
        return sb.toString();
    }
}
