package org.example.Exceptions;

public class FacturaSinItemsException extends Exception{
    public FacturaSinItemsException(String mensaje) {
        super(mensaje);
    }
}
