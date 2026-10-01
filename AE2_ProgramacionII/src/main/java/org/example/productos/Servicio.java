package org.example.productos;

import org.example.personas.Proveedor;

public class Servicio extends ProductoServicio {

    public Servicio(int id, String nombre, int precioBase, Proveedor proveedor) {
        super(id, nombre, precioBase, proveedor);
    }

    @Override
    public String getTipo() {
        return "Servicio";
    }

    // El servicio mantiene su precio base sin IVA
    @Override
    public int calcularPrecioFinal() {
        return getPrecioBase();
    }
}
