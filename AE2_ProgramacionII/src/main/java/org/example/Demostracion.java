package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.example.excepciones.DatoInvalidoException;
import org.example.excepciones.FacturaSinItemsException;
import org.example.excepciones.LimiteCreditoExcedidoException;
import org.example.excepciones.PagoExcedeDeudaException;
import org.example.excepciones.PersistenciaException;
import org.example.facturacion.ComparadorFacturaPorFecha;
import org.example.facturacion.Estado;
import org.example.facturacion.Factura;
import org.example.facturacion.FormaPago;
import org.example.facturacion.Pago;
import org.example.interfaces.Facturable;
import org.example.interfaces.Guardable;
import org.example.persistencia.RepositorioArchivo;
import org.example.personas.Categoria;
import org.example.personas.Cliente;
import org.example.personas.Departamento;
import org.example.personas.Empleado;
import org.example.personas.Proveedor;
import org.example.personas.Puesto;
import org.example.productos.Producto;
import org.example.productos.ProductoServicio;
import org.example.productos.Servicio;

public class Demostracion {
    // Demostración original del AE2 (orden, interfaces, persistencia y excepciones)
    public static void ejecutar() {

        try {
            // 1. Proveedor
            Proveedor proveedor1 = new Proveedor("Distribuidora Sur", "Ruta 12 km 5", 20111222, 376411111,
                    "Distribuidora Sur SRL", "30-12345678-9");

            // 2. Productos y Servicios
            Producto producto1 = new Producto(1, "Mouse inalambrico", 15000, proveedor1);
            Servicio servicio1 = new Servicio(2, "Instalacion de software", 8000, proveedor1);

            // 3. Departamento y Empleado
            Departamento sistemas = new Departamento("Sistemas", 500000, null);
            Empleado empleado1 = new Empleado("Juan", "Calle 123", 30111222, 376422222,
                    350000, Puesto.TECNICO, "01/03/2023", sistemas);

            sistemas.setResponsable(empleado1);
            sistemas.agregarEmpleado(empleado1);

            // 4. Cliente
            Cliente cliente1 = new Cliente("Maria", "Av. Siempre Viva 742", 28111333, 376433333,
                    100000, Categoria.PREMIUM);

            // --- DEMOSTRACIÓN DE ROLES ---
            System.out.println("===== DEMOSTRACIÓN DE ROLES (Persona) =====");
            System.out.println(cliente1.getRol());
            System.out.println(empleado1.getRol());
            System.out.println(proveedor1.getRol());
            System.out.println("===========================================\n");

            // 5. Creación de Facturas

            // Factura 1: Producto + Servicio
            List<ProductoServicio> items1 = new ArrayList<>();
            items1.add(producto1);
            items1.add(servicio1);
            Pago pago1 = new Pago(23000, "15/03/2024", FormaPago.TARJETA, Estado.PARCIAL);
            Factura factura1 = new Factura(1, "15/03/2024", cliente1, empleado1, items1,
                    FormaPago.TARJETA, pago1);

            // Factura 2: Solo Servicio (fecha más antigua, monto menor)
            List<ProductoServicio> items2 = new ArrayList<>();
            items2.add(servicio1);
            Pago pago2 = new Pago(8000, "10/01/2024", FormaPago.EFECTIVO, Estado.CANCELADO);
            Factura factura2 = new Factura(2, "10/01/2024", cliente1, empleado1, items2,
                    FormaPago.EFECTIVO, pago2);

            // Factura 3: Solo Producto (fecha más reciente)
            List<ProductoServicio> items3 = new ArrayList<>();
            items3.add(producto1);
            Pago pago3 = new Pago(18150, "20/05/2024", FormaPago.TRANSFERENCIA, Estado.CANCELADO);
            Factura factura3 = new Factura(3, "20/05/2024", cliente1, empleado1, items3,
                    FormaPago.TRANSFERENCIA, pago3);

            // 6. Se registran las compras en el historial del cliente (valida el límite de crédito)
            cliente1.registrarCompra(factura1);
            cliente1.registrarCompra(factura2);
            cliente1.registrarCompra(factura3);
            System.out.println("Compras registradas en el historial de " + cliente1.getNombre()
                    + ": " + cliente1.getHistorialCompras().size() + "\n");

            // 7. Lista de Facturas
            List<Factura> facturas = new ArrayList<>();
            facturas.add(factura1);
            facturas.add(factura2);
            facturas.add(factura3);

            // --- ORDENAMIENTO DE FACTURAS ---
            System.out.println("===== ORDEN NATURAL (Comparable: por monto) =====");
            Collections.sort(facturas);
            for (Factura f : facturas) {
                System.out.println("N° " + f.getNumero() + " | Monto: $" + f.getMontoFacturable()
                        + " | Fecha: " + f.getFecha());
            }

            System.out.println("\n===== ORDEN ALTERNATIVO (Comparator: por fecha) =====");
            Collections.sort(facturas, new ComparadorFacturaPorFecha());
            for (Factura f : facturas) {
                System.out.println("N° " + f.getNumero() + " | Fecha: " + f.getFecha()
                        + " | Monto: $" + f.getMontoFacturable());
            }

            // --- DEMOSTRACIÓN DE INTERFACES ---
            System.out.println("\n===== DEMOSTRACIÓN DE INTERFAZ FACTURABLE =====");
            Facturable[] cosas = { producto1, servicio1, factura1 };
            for (Facturable f : cosas) {
                System.out.println("Monto facturable: $" + f.getMontoFacturable());
            }

            System.out.println("\n===== DEMOSTRACIÓN DE INTERFAZ GUARDABLE =====");
            Guardable[] elementosGuardables = { factura1, cliente1 };
            for (Guardable g : elementosGuardables) {
                System.out.println("Línea lista para archivo: " + g.aLineaArchivo());
            }

            // --- PERSISTENCIA EN ARCHIVO ---
            System.out.println("\n===== PERSISTENCIA EN ARCHIVO =====");
            RepositorioArchivo repoFacturas = new RepositorioArchivo("facturas.txt");
            repoFacturas.guardar(facturas);
            System.out.println("Facturas guardadas en: " + repoFacturas.getRuta().toAbsolutePath());
            System.out.println("Contenido leído del archivo:");
            for (String linea : repoFacturas.leer()) {
                mostrarFacturaLeida(linea);
            }

            List<Cliente> clientes = new ArrayList<>();
            clientes.add(cliente1);
            RepositorioArchivo repoClientes = new RepositorioArchivo("clientes.txt");
            repoClientes.guardar(clientes);
            System.out.println("\nClientes guardados y leídos:");
            for (String linea : repoClientes.leer()) {
                mostrarClienteLeido(linea);
            }

            // --- DEMOSTRACIÓN DE EXCEPCIONES DEL DOMINIO ---
            System.out.println("\n===== DEMOSTRACIÓN DE EXCEPCIONES =====");

            // a) Factura sin ítems
            try {
                new Factura(10, "01/06/2024", cliente1, empleado1, new ArrayList<>(),
                        FormaPago.EFECTIVO, pago2);
            } catch (FacturaSinItemsException e) {
                System.out.println("[FacturaSinItems]");
                System.out.println("  Factura N°: " + e.getNumeroFactura());
                System.out.println("  Motivo:     no tiene ningún ítem comprado");
                System.out.println();
            } catch (PagoExcedeDeudaException e) {
                System.out.println("[Error inesperado] " + e.getMessage());
            }

            // b) Pago que excede el total de la factura
            try {
                Pago pagoExcesivo = new Pago(99999, "01/06/2024", FormaPago.EFECTIVO, Estado.CANCELADO);
                new Factura(11, "01/06/2024", cliente1, empleado1, items2,
                        FormaPago.EFECTIVO, pagoExcesivo);
            } catch (PagoExcedeDeudaException e) {
                System.out.println("[PagoExcedeDeuda]");
                System.out.println("  Factura N°:      " + e.getNumeroFactura());
                System.out.println("  Pago ingresado:  $" + e.getMontoPago());
                System.out.println("  Total adeudado:  $" + e.getTotalFactura());
                System.out.println("  Exceso:          $" + (e.getMontoPago() - e.getTotalFactura()));
                System.out.println();
            } catch (FacturaSinItemsException e) {
                System.out.println("[Error inesperado] " + e.getMessage());
            }

            // c) Cliente con límite de crédito insuficiente
            try {
                Cliente clienteChico = new Cliente("Pedro", "Calle 9", 25111444, 376400000,
                        20000, Categoria.REGULAR);
                clienteChico.registrarCompra(factura1);
            } catch (LimiteCreditoExcedidoException e) {
                System.out.println("[LimiteCredito]");
                System.out.println("  Cliente:              " + e.getCliente());
                System.out.println("  Límite de crédito:    $" + e.getLimite());
                System.out.println("  Total con la compra:  $" + e.getTotalConCompra());
                System.out.println("  Excede por:           $" + (e.getTotalConCompra() - e.getLimite()));
                System.out.println();
            }

            // d) Datos inválidos (excepción no verificada)
            try {
                new Pago(-500, "01/06/2024", FormaPago.EFECTIVO, Estado.PENDIENTE);
            } catch (DatoInvalidoException e) {
                System.out.println("[DatoInvalido]");
                System.out.println("  Campo:   Pago.monto");
                System.out.println("  Detalle: " + e.getMessage());
                System.out.println();
            }
            try {
                new Cliente("", "Calle 1", 12345678, 376400001, 1000, Categoria.REGULAR);
            } catch (DatoInvalidoException e) {
                System.out.println("[DatoInvalido]");
                System.out.println("  Campo:   Cliente.nombre");
                System.out.println("  Detalle: " + e.getMessage());
                System.out.println();
            }

            // e) Error de persistencia (archivo inexistente)
            try {
                new RepositorioArchivo("no_existe.txt").leer();
            } catch (PersistenciaException e) {
                System.out.println("[Persistencia]");
                System.out.println("  Archivo: no_existe.txt");
                System.out.println("  Acción:  lectura");
                System.out.println("  Causa:   el archivo no existe ("
                        + (e.getCause() != null ? e.getCause().getClass().getSimpleName() : "desconocida") + ")");
            }

        } catch (FacturaSinItemsException | PagoExcedeDeudaException
                 | LimiteCreditoExcedidoException | PersistenciaException e) {
            System.out.println("Error en el flujo principal: " + e.getMessage());
        }
    }

    // Convierte una línea "nro;fecha;dniCliente;dniEmpleado;monto;formaPago" en campos legibles
    private static void mostrarFacturaLeida(String linea) {
        String[] c = linea.split(";");
        System.out.println("  Factura N° " + c[0]);
        System.out.println("    Fecha:          " + c[1]);
        System.out.println("    DNI Cliente:    " + c[2]);
        System.out.println("    DNI Empleado:   " + c[3]);
        System.out.println("    Monto:          $" + c[4]);
        System.out.println("    Forma de pago:  " + c[5]);
        System.out.println();
    }

    // Convierte una línea "dni;nombre;limiteCredito;categoria" en campos legibles
    private static void mostrarClienteLeido(String linea) {
        String[] c = linea.split(";");
        System.out.println("  DNI:               " + c[0]);
        System.out.println("  Nombre:            " + c[1]);
        System.out.println("  Límite de crédito: $" + c[2]);
        System.out.println("  Categoría:         " + c[3]);
        System.out.println();
    }
}
