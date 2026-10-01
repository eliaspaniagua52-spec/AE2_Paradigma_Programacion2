package org.example.facturacion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import org.example.excepciones.DatoInvalidoException;
import org.example.excepciones.FacturaSinItemsException;
import org.example.excepciones.PagoExcedeDeudaException;
import org.example.interfaces.Facturable;
import org.example.interfaces.Guardable;
import org.example.personas.Cliente;
import org.example.personas.Empleado;
import org.example.productos.ProductoServicio;

public class Factura implements Facturable, Guardable, Comparable<Factura> {
    private int numero;
    private String fecha;
    private Cliente cliente;
    private Empleado empleado;
    private List<ProductoServicio> itemsComprados;
    private FormaPago formapago;
    private Pago pago;

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    public Factura(int numero, String fecha, Cliente cliente, Empleado empleado,
                   List<ProductoServicio> itemsComprados, FormaPago formapago, Pago pago)
            throws FacturaSinItemsException, PagoExcedeDeudaException {
        if (itemsComprados == null || itemsComprados.isEmpty()) {
            throw new FacturaSinItemsException(numero);
        }
        this.numero = numero;
        this.fecha = fecha;
        this.cliente = cliente;
        this.empleado = empleado;
        this.itemsComprados = itemsComprados;
        this.formapago = formapago;
        this.pago = pago;

        int total = 0;
        for (ProductoServicio item : itemsComprados) {
            total += item.getMontoFacturable();
        }
        if (pago != null && pago.getMonto() > total) {
            throw new PagoExcedeDeudaException(numero, pago.getMonto(), total);
        }
    }

    public LocalDate getFechaComoLocalDate() {
        return LocalDate.parse(fecha, FORMATO);
    }

    @Override
    public int compareTo(Factura otra) {
        return Integer.compare(this.getMontoFacturable(), otra.getMontoFacturable());
    }

    @Override
    public int getMontoFacturable() {
        int total = 0;
        for (ProductoServicio item : itemsComprados) {
            total += item.getMontoFacturable();
        }
        return total;
    }

    @Override
    public String aLineaArchivo() {
        return numero + ";" + fecha + ";" + cliente.getDni() + ";"
                + empleado.getDni() + ";" + getMontoFacturable() + ";" + formapago;
    }

    public int getNumero() {
        return numero;
    }

    public String getFecha() {
        return fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public List<ProductoServicio> getItemsComprados() {
        return itemsComprados;
    }

    public FormaPago getFormapago() {
        return formapago;
    }

    public Pago getPago() {
        return pago;
    }

    public void setFecha(String fecha) {
        try {
            LocalDate.parse(fecha, FORMATO);
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException("La fecha debe tener formato dd/MM/yyyy: " + fecha);
        }
        this.fecha = fecha;
    }

    public void setFormapago(FormaPago formapago) {
        this.formapago = formapago;
    }

    // El pago puede ser null (factura todavía sin pago registrado)
    public void setPago(Pago pago) throws PagoExcedeDeudaException {
        int total = getMontoFacturable();
        if (pago != null && pago.getMonto() > total) {
            throw new PagoExcedeDeudaException(numero, pago.getMonto(), total);
        }
        this.pago = pago;
    }
}
