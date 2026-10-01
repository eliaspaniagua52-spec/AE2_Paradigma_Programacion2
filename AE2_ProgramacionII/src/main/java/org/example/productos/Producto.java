package org.example.productos;

import org.example.personas.Proveedor;

public class Producto extends ProductoServicio {

    public Producto(int id, String nombre, int precioBase, Proveedor proveedor) {
        super(id, nombre, precioBase, proveedor);
    }

    @Override
    public String getTipo() {
        return "Producto";
    }

    @Override
    public int calcularPrecioFinal() {
        return (int) Math.round(getPrecioBase() * 1.21);
    }
}
