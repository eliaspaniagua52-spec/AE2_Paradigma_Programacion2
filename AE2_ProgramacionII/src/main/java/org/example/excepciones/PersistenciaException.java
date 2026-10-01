package org.example.excepciones;

// Excepción verificada del dominio que envuelve los errores de lectura/escritura de archivos.
public class PersistenciaException extends Exception {
    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
