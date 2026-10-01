package org.example.productos;

import org.example.excepciones.DatoInvalidoException;
import org.example.interfaces.Facturable;
import org.example.personas.Proveedor;

public abstract class ProductoServicio implements Facturable {
    private int id;
    private String nombre;
    private int precioBase;
    private Proveedor proveedor;

    public ProductoServicio(int id, String nombre, int precioBase, Proveedor proveedor) {
        if (precioBase < 0) {
            throw new DatoInvalidoException("El precio base no puede ser negativo: " + precioBase);
        }
        this.id = id;
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.proveedor = proveedor;
    }

    // Métodos abstractos: cada subclase los implementa a su manera
    public abstract String getTipo();
    public abstract int calcularPrecioFinal();

    // Contrato Facturable: el monto que aporta a una factura es el precio final
    @Override
    public int getMontoFacturable() {
        return calcularPrecioFinal();
    }

    // Método concreto compartido
    public String getDescripcion() {
        return getTipo() + ": " + nombre + " | $" + calcularPrecioFinal();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(int precioBase) {
        if (precioBase < 0) {
            throw new DatoInvalidoException("El precio base no puede ser negativo: " + precioBase);
        }
        this.precioBase = precioBase;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
}
