package org.example.ObjetosClave;

import org.example.Interfaces.Identificable;
import org.example.Interfaces.Vencible;
import org.example.Personas.Proveedor;

public class Producto extends ItemComercial implements Identificable, Vencible {
    private String fechaVencimiento;
    private int stock;

    public Producto(String codigo, String nombre,
                    int precio, int stock, Proveedor proveedor) {
        super(codigo, nombre, precio, proveedor);
        this.stock = stock;
    }

    @Override
    public String getTipo() {
        return "Producto";
    }

    @Override
    public boolean estaVencido() {
        if (fechaVencimiento == null) return false;
        return java.time.LocalDate.parse(fechaVencimiento).isBefore(java.time.LocalDate.now());
    }
    public Producto() {}


    public int getStock() {
        return stock;
    }

    public void setStockSuma(int stock) {
        this.stock += stock;
    }

    public void setStockResta(int stock) {
        this.stock -= stock;
    }
    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}
