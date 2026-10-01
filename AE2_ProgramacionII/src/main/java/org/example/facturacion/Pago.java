<<<<<<<< HEAD:src/main/java/org/example/facturacion/Pago.java
package org.example.facturacion;

import org.example.excepciones.DatoInvalidoException;
========
package org.example.ObjetosClave;

import org.example.Enums.Estado;
import org.example.Enums.FormaPago;
>>>>>>>> de3e3cc60a7da5493cf7eab6bb96f73e86fd5abb:src/main/java/org/example/ObjetosClave/Pago.java

public class Pago {
    private double monto;
    private String fecha;
    private FormaPago formapago;
    private Estado estado;

    public Pago(double monto, String fecha, FormaPago formapago,
                Estado estado){
        if (monto <= 0) {
            throw new DatoInvalidoException("El monto del pago debe ser mayor a cero: " + monto);
        }
        this.monto = monto;
        this.fecha = fecha;
        this.formapago = formapago;
        this.estado = estado;
    }
    public double getMonto(){
        return monto;
    }
    public Pago() {}
    public String getFecha(){
        return fecha;
    }
    public FormaPago getFormapago(){
        return formapago;
    }
    public Estado getEstado(){
        return estado;
    }
}
