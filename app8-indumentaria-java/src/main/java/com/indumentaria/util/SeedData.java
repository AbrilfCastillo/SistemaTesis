package com.indumentaria.util;

import com.indumentaria.db.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Carga usuarios, productos y ventas de ejemplo para poder probar la aplicacion
 * sin tener que cargar todo a mano. Se puede disparar desde el boton de Login
 * o con el modo "java -jar indumentaria.jar --seed".
 */
public class SeedData {

    private static final double IVA = 0.21;
    private static final double DESCUENTO_MAYORISTA = 0.10;
    private static final int UMBRAL_MAYORISTA = 10;

    public static void cargarDatosDeEjemplo() {
        try (Connection con = ConexionDB.obtenerConexion()) {
            cargarUsuarios(con);
            cargarProductos(con);
            cargarVentas(con);
        } catch (Exception e) {
            System.out.println("Ocurrió un error");
        }
    }

    private static void cargarUsuarios(Connection con) throws Exception {
        if (contarFilas(con, "usuarios") > 0) {
            return;
        }
        String sql = "INSERT INTO usuarios (username, password_hash, rol) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            insertarUsuario(ps, "admin", "admin123", "admin");
            insertarUsuario(ps, "mrodriguez", "vend2024", "vendedor");
            insertarUsuario(ps, "jgonzalez", "gonzalez99", "vendedor");
        }
    }

    private static void insertarUsuario(PreparedStatement ps, String username, String password, String rol) throws Exception {
        ps.setString(1, username);
        ps.setString(2, PasswordUtil.hashear(password));
        ps.setString(3, rol);
        ps.executeUpdate();
    }

    private static void cargarProductos(Connection con) throws Exception {
        if (contarFilas(con, "productos") > 0) {
            return;
        }
        String sql = "INSERT INTO productos (nombre, precio, stock, categoria) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            insertarProducto(ps, "Remera básica algodón", "8500.00", 40, "ropa");
            insertarProducto(ps, "Pantalón jean", "21000.00", 25, "ropa");
            insertarProducto(ps, "Buzo canguro", "18500.00", 15, "ropa");
            insertarProducto(ps, "Zapatillas urbanas", "45000.00", 20, "calzado");
            insertarProducto(ps, "Ojotas", "9000.00", 30, "calzado");
            insertarProducto(ps, "Auriculares bluetooth", "32000.00", 12, "accesorios electronicos");
            insertarProducto(ps, "Cargador USB-C 20W", "15000.00", 18, "accesorios electronicos");
            insertarProducto(ps, "Mochila urbana", "27000.00", 10, "accesorios");
        }
    }

    private static void insertarProducto(PreparedStatement ps, String nombre, String precio, int stock, String categoria) throws Exception {
        ps.setString(1, nombre);
        ps.setString(2, precio);
        ps.setInt(3, stock);
        ps.setString(4, categoria);
        ps.executeUpdate();
    }

    private static void cargarVentas(Connection con) throws Exception {
        if (contarFilas(con, "ventas") > 0) {
            return;
        }
        int idAdmin = obtenerIdUsuario(con, "admin");
        int idVendedor = obtenerIdUsuario(con, "mrodriguez");

        // Venta chica: 2 remeras + 1 par de zapatillas, sin descuento mayorista
        registrarVentaEjemplo(con, idAdmin, new int[][] {
                {1, 2},
                {4, 1}
        });

        // Venta grande: 12 remeras, dispara el descuento mayorista (10 o más unidades)
        registrarVentaEjemplo(con, idVendedor, new int[][] {
                {1, 12}
        });
    }

    /**
     * items: pares {producto_id, cantidad} usando los ids de la carga de productos de arriba (1 a 8).
     */
    private static void registrarVentaEjemplo(Connection con, int usuarioId, int[][] items) throws Exception {
        double subtotal = 0;
        int totalUnidades = 0;
        double[] precios = new double[items.length];

        for (int i = 0; i < items.length; i++) {
            int productoId = items[i][0];
            int cantidad = items[i][1];
            double precio = obtenerPrecioProducto(con, productoId);
            precios[i] = precio;
            subtotal += precio * cantidad;
            totalUnidades += cantidad;
        }

        if (totalUnidades >= UMBRAL_MAYORISTA) {
            subtotal = subtotal * (1 - DESCUENTO_MAYORISTA);
        }
        double total = subtotal * (1 + IVA);

        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (PreparedStatement psVenta = con.prepareStatement(
                "INSERT INTO ventas (usuario_id, fecha, total) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            psVenta.setInt(1, usuarioId);
            psVenta.setString(2, fecha);
            psVenta.setString(3, String.valueOf(total));
            psVenta.executeUpdate();

            int ventaId;
            try (ResultSet rs = psVenta.getGeneratedKeys()) {
                rs.next();
                ventaId = rs.getInt(1);
            }

            try (PreparedStatement psDetalle = con.prepareStatement(
                    "INSERT INTO venta_detalle (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)")) {
                for (int i = 0; i < items.length; i++) {
                    int productoId = items[i][0];
                    int cantidad = items[i][1];
                    double subtotalItem = precios[i] * cantidad;
                    psDetalle.setInt(1, ventaId);
                    psDetalle.setInt(2, productoId);
                    psDetalle.setInt(3, cantidad);
                    psDetalle.setString(4, String.valueOf(precios[i]));
                    psDetalle.setString(5, String.valueOf(subtotalItem));
                    psDetalle.executeUpdate();
                }
            }
        }
    }

    private static double obtenerPrecioProducto(Connection con, int productoId) throws Exception {
        try (PreparedStatement ps = con.prepareStatement("SELECT precio FROM productos WHERE id = ?")) {
            ps.setInt(1, productoId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return Double.parseDouble(rs.getString("precio"));
            }
        }
    }

    private static int obtenerIdUsuario(Connection con, String username) throws Exception {
        try (PreparedStatement ps = con.prepareStatement("SELECT id FROM usuarios WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        }
    }

    private static int contarFilas(Connection con, String tabla) throws Exception {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
