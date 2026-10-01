package org.example.excepciones;

// Excepción no verificada (RuntimeException): se lanza cuando un objeto
// del dominio recibe datos inválidos en su construcción o en un setter.
public class DatoInvalidoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
