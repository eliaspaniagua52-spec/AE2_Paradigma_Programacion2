package org.example.excepciones;

// Excepción verificada: una factura no puede emitirse sin ítems.
public class FacturaSinItemsException extends Exception {
    private static final long serialVersionUID = 1L;

    private final int numeroFactura;

    public FacturaSinItemsException(int numeroFactura) {
        super("La factura N° " + numeroFactura + " no tiene ningún ítem comprado.");
        this.numeroFactura = numeroFactura;
    }

    public int getNumeroFactura() { return numeroFactura; }
}
