package org.example.facturacion;

import java.util.Comparator;

public class ComparadorFacturaPorFecha implements Comparator<Factura> {
    @Override
    public int compare(Factura a, Factura b) {
        return a.getFechaComoLocalDate().compareTo(b.getFechaComoLocalDate());
    }
}
