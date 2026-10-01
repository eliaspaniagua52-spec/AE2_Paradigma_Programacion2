package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // 1. Creo los proveedores primero (porque los productos/servicios los necesitan)
        Proveedor proveedor1 = new Proveedor("Distribuidora Sur", "Ruta 12 km 5", 20111222, 376411111,
                "Distribuidora Sur SRL", "30-12345678-9");

        // 2. Creo productos y servicios (necesitan un proveedor ya creado)
        Producto producto1 = new Producto(1, "Mouse inalambrico", 15000, proveedor1);
        Servicio servicio1 = new Servicio(2, "Instalacion de software", 8000, proveedor1);

        // 3. Creo un departamento SIN responsable todavia (porque el responsable es un Empleado, y todavia no cree ninguno)
        Departamento sistemas = new Departamento("Sistemas", 500000, null);

        // 4. Creo el empleado, ahora si asignandole el departamento que ya existe
        Empleado empleado1 = new Empleado("Juan", "Calle 123", 30111222, 376422222,
                350000, Puesto.TECNICO, "01/03/2023", sistemas);

        // 5. Ahora que el empleado existe, se lo asigno como responsable del departamento
        sistemas.setResponsable(empleado1);
        sistemas.agregarEmpleado(empleado1);

        // 6. Creo un cliente
        Cliente cliente1 = new Cliente("Maria", "Av. Siempre Viva 742", 28111333, 376433333,
                100000, Categoria.PREMIUM);

        // 7. Armo la lista de items que va a tener la factura
        List<ItemComercial> itemsFactura = new ArrayList<>();
        itemsFactura.add(producto1);
        itemsFactura.add(servicio1);

        // 8. Creo el pago
        Pago pago1 = new Pago(23000, "15/03/2024", FormaPago.TARJETA, Estado.PARCIAL);

        // 9. Creo la factura, uniendo todo lo anterior
        Factura factura1 = new Factura(1, "15/03/2024", cliente1, empleado1, itemsFactura,
                FormaPago.TARJETA, pago1);

        // 10. Muestro el detalle de la factura en pantalla
        System.out.println("===== DETALLE DE FACTURA =====");
        System.out.println("Numero: " + factura1.getNumero());
        System.out.println("Fecha: " + factura1.getFecha());
        System.out.println("Cliente: " + factura1.getCliente().getNombre());
        System.out.println("Empleado que atendio: " + factura1.getEmpleado().getNombre());
        System.out.println("Forma de pago: " + factura1.getFormapago());
        System.out.println("--- Items comprados ---");
        for (ItemComercial item : factura1.getItemsComprados()) {
            System.out.println("- " + item.getTipo() + ": " + item.getNombre() + " | $" + item.getPrecio());
        }
        System.out.println("Estado del pago: " + factura1.getPago().getEstado());
    }
}
