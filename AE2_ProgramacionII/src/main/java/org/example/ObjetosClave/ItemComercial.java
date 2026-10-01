
package org.example.ObjetosClave;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.example.Personas.Proveedor;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipoItem")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Producto.class, name = "producto"),
        @JsonSubTypes.Type(value = Servicio.class, name = "servicio")
})
@JsonIgnoreProperties({"tipo"})
public abstract class ItemComercial {
    private String codigo;
    private String nombre;
    private int precio;
    private Proveedor proveedor;

    public ItemComercial(String codigo, String nombre, int precio, Proveedor proveedor){
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.proveedor = proveedor;
    }


    public String mostrarResumen(){
        return getTipo() + ": " + nombre + " | $" + precio;
    }
    public ItemComercial() {}


    public abstract String getTipo();


    public String getCodigo(){
        return codigo;
    }
    public void setCodigo(String codigo){
        this.codigo = codigo;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
}
