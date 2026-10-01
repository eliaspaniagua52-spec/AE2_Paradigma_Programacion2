package org.example.Exceptions;

public class PagoExcedeDeudaException extends Exception {
    public PagoExcedeDeudaException(String mensaje) {
        super(mensaje);
    }
}
