package org.example.excepciones;

// Excepción verificada: el pago registrado supera el total de la factura.
public class PagoExcedeDeudaException extends Exception {
    private static final long serialVersionUID = 1L;

    private final int numeroFactura;
    private final int montoPago;
    private final int totalFactura;

    public PagoExcedeDeudaException(int numeroFactura, int montoPago, int totalFactura) {
        super("En la factura N° " + numeroFactura + " el pago ($" + montoPago
                + ") excede el total adeudado ($" + totalFactura + ").");
        this.numeroFactura = numeroFactura;
        this.montoPago = montoPago;
        this.totalFactura = totalFactura;
    }

    public int getNumeroFactura() { return numeroFactura; }
    public int getMontoPago() { return montoPago; }
    public int getTotalFactura() { return totalFactura; }
}
