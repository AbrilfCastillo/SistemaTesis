package com.indumentaria.modelo;

/**
 * Representa un producto del inventario.
 *
 * El precio se maneja como String porque asi esta guardado en la columna
 * de la base (ver schema.sql). Donde hace falta operar con el valor se
 * convierte con Double.parseDouble en el momento.
 */
public class Producto {

    private int id;
    private String nombre;
    private String precio;
    private int stock;
    private String categoria;

    public Producto(int id, String nombre, String precio, int stock, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public String getCategoria() {
        return categoria;
    }

    @Override
    public String toString() {
        return nombre + " - $" + precio + " (stock: " + stock + ")";
    }
}
