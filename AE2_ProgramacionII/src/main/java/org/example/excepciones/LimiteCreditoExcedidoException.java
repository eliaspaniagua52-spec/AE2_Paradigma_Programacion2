package org.example.excepciones;

// Excepción verificada: la compra supera el límite de crédito del cliente.
public class LimiteCreditoExcedidoException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String cliente;
    private final int limite;
    private final int totalConCompra;

    public LimiteCreditoExcedidoException(String cliente, int limite, int totalConCompra) {
        super("El cliente " + cliente + " superaría su límite de crédito ($" + limite
                + "): total acumulado con esta compra = $" + totalConCompra + ".");
        this.cliente = cliente;
        this.limite = limite;
        this.totalConCompra = totalConCompra;
    }

    public String getCliente() { return cliente; }
    public int getLimite() { return limite; }
    public int getTotalConCompra() { return totalConCompra; }
}
