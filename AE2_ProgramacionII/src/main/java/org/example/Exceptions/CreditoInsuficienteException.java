package org.example.Exceptions;

public class CreditoInsuficienteException extends Exception{
    public CreditoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
