package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.example.excepciones.DatoInvalidoException;
import org.example.excepciones.FacturaSinItemsException;
import org.example.excepciones.LimiteCreditoExcedidoException;
import org.example.excepciones.PagoExcedeDeudaException;
import org.example.excepciones.PersistenciaException;
import org.example.facturacion.Estado;
import org.example.facturacion.Factura;
import org.example.facturacion.FormaPago;
import org.example.facturacion.Pago;
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

public class Menu {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final Scanner scanner = new Scanner(System.in);

    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Empleado> empleados = new ArrayList<>();
    private final List<Proveedor> proveedores = new ArrayList<>();
    private final List<ProductoServicio> productos = new ArrayList<>();
    private final List<Departamento> departamentos = new ArrayList<>();
    private final List<Factura> facturas = new ArrayList<>();

    // ------------------------------------------------------------------
    // Menú principal
    // ------------------------------------------------------------------

    public void iniciar() {
        cargarDatosIniciales();
        try {
            int opcion;
            do {
                System.out.println("\n========== SISTEMA DE GESTIÓN EMPRESARIAL ==========");
                System.out.println("1. Cliente");
                System.out.println("2. Empleados");
                System.out.println("3. Proveedores");
                System.out.println("4. Productos y servicios");
                System.out.println("5. Facturas");
                System.out.println("6. Guardar datos en archivos (clientes.txt / facturas.txt)");
                System.out.println("7. Ejecutar demostración (ordenamiento, interfaces, excepciones)");
                System.out.println("0. Salir");
                opcion = leerEntero("Opción", 0, 7);
                switch (opcion) {
                    case 1: menuClientes(); break;
                    case 2: menuEmpleados(); break;
                    case 3: menuProveedores(); break;
                    case 4: menuProductos(); break;
                    case 5: menuFacturas(); break;
                    case 6: guardarDatos(); break;
                    case 7: ejecutarDemostracion(); break;
                    default: break;
                }
            } while (opcion != 0);
        } catch (NoSuchElementException e) {
            System.out.println("\nSe cerró la entrada de datos.");
        }
        System.out.println("¡Hasta luego!");
    }

    // Datos de ejemplo para poder facturar sin cargar todo desde cero
    private void cargarDatosIniciales() {
        Proveedor proveedor = new Proveedor("Distribuidora Sur", "Ruta 12 km 5", 20111222, 376411111,
                "Distribuidora Sur SRL", "30-12345678-9");
        proveedores.add(proveedor);

        Producto mouse = new Producto(1, "Mouse inalambrico", 15000, proveedor);
        Servicio instalacion = new Servicio(2, "Instalacion de software", 8000, proveedor);
        proveedor.getListaProductos().add(mouse);
        productos.add(mouse);
        productos.add(instalacion);

        Departamento sistemas = new Departamento("Sistemas", 500000, null);
        Empleado juan = new Empleado("Juan", "Calle 123", 30111222, 376422222,
                350000, Puesto.TECNICO, "01/03/2023", sistemas);
        sistemas.setResponsable(juan);
        sistemas.agregarEmpleado(juan);
        departamentos.add(sistemas);
        empleados.add(juan);

        clientes.add(new Cliente("Maria", "Av. Siempre Viva 742", 28111333, 376433333,
                100000, Categoria.PREMIUM));
    }

    private void ejecutarDemostracion() {
        System.out.println("\nLa demostración usa sus propios datos y sobrescribe clientes.txt y facturas.txt.");
        if (!confirmar("¿Ejecutarla igual?")) {
            return;
        }
        Demostracion.ejecutar();
    }

    private void guardarDatos() {
        try {
            RepositorioArchivo repoClientes = new RepositorioArchivo("clientes.txt");
            repoClientes.guardar(clientes);
            RepositorioArchivo repoFacturas = new RepositorioArchivo("facturas.txt");
            repoFacturas.guardar(facturas);
            System.out.println("Clientes guardados en: " + repoClientes.getRuta().toAbsolutePath());
            System.out.println("Facturas guardadas en: " + repoFacturas.getRuta().toAbsolutePath());
        } catch (PersistenciaException e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }

    // Muestra el submenú estándar y devuelve la opción elegida
    private int submenu(String titulo) {
        System.out.println("\n----- " + titulo + " -----");
        System.out.println("1. Listar");
        System.out.println("2. Agregar");
        System.out.println("3. Modificar");
        System.out.println("4. Eliminar");
        System.out.println("0. Volver");
        return leerEntero("Opción", 0, 4);
    }

    // ------------------------------------------------------------------
    // CLIENTES
    // ------------------------------------------------------------------

    private void menuClientes() {
        int opcion;
        do {
            opcion = submenu("CLIENTES");
            switch (opcion) {
                case 1: listarClientes(); break;
                case 2: agregarCliente(); break;
                case 3: modificarCliente(); break;
                case 4: eliminarCliente(); break;
                default: break;
            }
        } while (opcion != 0);
    }

    private void listarClientes() {
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes cargados.");
            return;
        }
        for (Cliente c : clientes) {
            System.out.printf("DNI %d | %s | %s | Tel: %d | Límite: $%d | %s | Compras: %d%n",
                    c.getDni(), c.getNombre(), c.getDomicilio(), c.getTelefono(),
                    c.getLimiteCredito(), c.getCategoria(), c.getHistorialCompras().size());
        }
    }

    private void agregarCliente() {
        System.out.println("\n--- Agregar cliente ---");
        int dni = leerEntero("DNI", 1, Integer.MAX_VALUE);
        if (dniEnUso(dni)) {
            System.out.println("Ya existe una persona cargada con ese DNI.");
            return;
        }
        String nombre = leerTexto("Nombre");
        String domicilio = leerTexto("Domicilio");
        int telefono = leerEntero("Teléfono", 0, Integer.MAX_VALUE);
        int limite = leerEntero("Límite de crédito", 0, Integer.MAX_VALUE);
        Categoria categoria = elegirEnum("Categoría", Categoria.values(), null);
        try {
            clientes.add(new Cliente(nombre, domicilio, dni, telefono, limite, categoria));
            System.out.println("Cliente agregado.");
        } catch (DatoInvalidoException e) {
            System.out.println("Datos inválidos: " + e.getMessage());
        }
    }

    private void modificarCliente() {
        Cliente c = seleccionarCliente();
        if (c == null) {
            return;
        }
        System.out.println("(Enter = mantener el valor actual. El DNI no se modifica.)");
        try {
            c.setNombre(leerTextoOpcional("Nombre", c.getNombre()));
            c.setDomicilio(leerTextoOpcional("Domicilio", c.getDomicilio()));
            c.setTelefono(leerEnteroOpcional("Teléfono", c.getTelefono(), 0));

            int nuevoLimite = leerEnteroOpcional("Límite de crédito", c.getLimiteCredito(), 0);
            int acumulado = totalCompras(c);
            if (nuevoLimite < acumulado) {
                System.out.println("No se cambió el límite: el cliente ya acumula $" + acumulado + " en compras.");
            } else {
                c.setLimiteCredito(nuevoLimite);
            }

            c.setCategoria(elegirEnum("Categoría", Categoria.values(), c.getCategoria()));
            System.out.println("Cliente modificado.");
        } catch (DatoInvalidoException e) {
            System.out.println("Datos inválidos: " + e.getMessage());
        }
    }

    private void eliminarCliente() {
        Cliente c = seleccionarCliente();
        if (c == null) {
            return;
        }
        int cantidad = 0;
        for (Factura f : facturas) {
            if (f.getCliente() == c) {
                cantidad++;
            }
        }
        if (cantidad > 0) {
            System.out.println("No se puede eliminar: el cliente tiene " + cantidad
                    + " factura(s). Eliminá primero esas facturas.");
            return;
        }
        if (confirmar("¿Eliminar al cliente " + c.getNombre() + "?")) {
            clientes.remove(c);
            System.out.println("Cliente eliminado.");
        }
    }

    private int totalCompras(Cliente c) {
        int total = 0;
        for (Factura f : c.getHistorialCompras()) {
            total += f.getMontoFacturable();
        }
        return total;
    }

    private Cliente seleccionarCliente() {
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes cargados.");
            return null;
        }
        listarClientes();
        int dni = leerEntero("DNI del cliente (0 para cancelar)", 0, Integer.MAX_VALUE);
        if (dni == 0) {
            return null;
        }
        for (Cliente c : clientes) {
            if (c.getDni() == dni) {
                return c;
            }
        }
        System.out.println("No existe un cliente con ese DNI.");
        return null;
    }

    // ------------------------------------------------------------------
    // EMPLEADOS
    // ------------------------------------------------------------------

    private void menuEmpleados() {
        int opcion;
        do {
            opcion = submenu("EMPLEADOS");
            switch (opcion) {
                case 1: listarEmpleados(); break;
                case 2: agregarEmpleado(); break;
                case 3: modificarEmpleado(); break;
                case 4: eliminarEmpleado(); break;
                default: break;
            }
        } while (opcion != 0);
    }

    private void listarEmpleados() {
        if (empleados.isEmpty()) {
            System.out.println("No hay empleados cargados.");
            return;
        }
        for (Empleado e : empleados) {
            System.out.printf("DNI %d | %s | %s | Tel: %d | Sueldo: $%d | %s | Ingreso: %s | Depto: %s%n",
                    e.getDni(), e.getNombre(), e.getDomicilio(), e.getTelefono(), e.getSalario(),
                    e.getPuesto(), e.getFechaIngreso(), e.getDepartamento().getNombre());
        }
    }

    private void agregarEmpleado() {
        System.out.println("\n--- Agregar empleado ---");
        int dni = leerEntero("DNI", 1, Integer.MAX_VALUE);
        if (dniEnUso(dni)) {
            System.out.println("Ya existe una persona cargada con ese DNI.");
            return;
        }
        String nombre = leerTexto("Nombre");
        String domicilio = leerTexto("Domicilio");
        int telefono = leerEntero("Teléfono", 0, Integer.MAX_VALUE);
        int salario = leerEntero("Salario", 0, Integer.MAX_VALUE);
        Puesto puesto = elegirEnum("Puesto", Puesto.values(), null);
        String fechaIngreso = leerFecha("Fecha de ingreso (dd/MM/yyyy, Enter = hoy)", hoy());
        Departamento departamento = elegirDepartamento(null);
        try {
            Empleado e = new Empleado(nombre, domicilio, dni, telefono, salario, puesto, fechaIngreso, departamento);
            departamento.agregarEmpleado(e);
            empleados.add(e);
            System.out.println("Empleado agregado.");
        } catch (DatoInvalidoException ex) {
            System.out.println("Datos inválidos: " + ex.getMessage());
        }
    }

    private void modificarEmpleado() {
        Empleado e = seleccionarEmpleado();
        if (e == null) {
            return;
        }
        System.out.println("(Enter = mantener el valor actual. El DNI no se modifica.)");
        try {
            e.setNombre(leerTextoOpcional("Nombre", e.getNombre()));
            e.setDomicilio(leerTextoOpcional("Domicilio", e.getDomicilio()));
            e.setTelefono(leerEnteroOpcional("Teléfono", e.getTelefono(), 0));
            e.setSalario(leerEnteroOpcional("Salario", e.getSalario(), 0));
            e.setPuesto(elegirEnum("Puesto", Puesto.values(), e.getPuesto()));
            e.setFechaIngreso(leerFecha("Fecha de ingreso [" + e.getFechaIngreso() + "]", e.getFechaIngreso()));

            Departamento anterior = e.getDepartamento();
            Departamento nuevo = elegirDepartamento(anterior);
            if (nuevo != anterior) {
                anterior.quitarEmpleado(e);
                if (anterior.getResponsable() == e) {
                    anterior.setResponsable(null);
                }
                nuevo.agregarEmpleado(e);
                e.setDepartamento(nuevo);
            }
            System.out.println("Empleado modificado.");
        } catch (DatoInvalidoException ex) {
            System.out.println("Datos inválidos: " + ex.getMessage());
        }
    }

    private void eliminarEmpleado() {
        Empleado e = seleccionarEmpleado();
        if (e == null) {
            return;
        }
        int cantidad = 0;
        for (Factura f : facturas) {
            if (f.getEmpleado() == e) {
                cantidad++;
            }
        }
        if (cantidad > 0) {
            System.out.println("No se puede eliminar: el empleado figura en " + cantidad
                    + " factura(s). Eliminá primero esas facturas.");
            return;
        }
        if (confirmar("¿Eliminar al empleado " + e.getNombre() + "?")) {
            Departamento d = e.getDepartamento();
            d.quitarEmpleado(e);
            if (d.getResponsable() == e) {
                d.setResponsable(null);
            }
            empleados.remove(e);
            System.out.println("Empleado eliminado.");
        }
    }

    private Empleado seleccionarEmpleado() {
        if (empleados.isEmpty()) {
            System.out.println("No hay empleados cargados.");
            return null;
        }
        listarEmpleados();
        int dni = leerEntero("DNI del empleado (0 para cancelar)", 0, Integer.MAX_VALUE);
        if (dni == 0) {
            return null;
        }
        for (Empleado e : empleados) {
            if (e.getDni() == dni) {
                return e;
            }
        }
        System.out.println("No existe un empleado con ese DNI.");
        return null;
    }

    // Permite elegir un departamento existente o crear uno nuevo.
    // Si "actual" no es null, Enter conserva el actual.
    private Departamento elegirDepartamento(Departamento actual) {
        while (true) {
            System.out.println("Departamentos:");
            for (int i = 0; i < departamentos.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + departamentos.get(i).getNombre());
            }
            System.out.println("  0. Crear un departamento nuevo");
            String sufijo = actual != null ? " [" + actual.getNombre() + "]" : "";
            System.out.print("Elegí un departamento" + sufijo + ": ");
            String texto = scanner.nextLine().trim();
            if (texto.isEmpty() && actual != null) {
                return actual;
            }
            try {
                int n = Integer.parseInt(texto);
                if (n == 0) {
                    String nombre = leerTexto("Nombre del nuevo departamento");
                    int presupuesto = leerEntero("Presupuesto", 0, Integer.MAX_VALUE);
                    Departamento nuevo = new Departamento(nombre, presupuesto, null);
                    departamentos.add(nuevo);
                    return nuevo;
                }
                if (n >= 1 && n <= departamentos.size()) {
                    return departamentos.get(n - 1);
                }
            } catch (NumberFormatException ex) {
                // se muestra el mensaje de opción inválida más abajo
            }
            System.out.println("  Opción inválida.");
        }
    }

    // ------------------------------------------------------------------
    // PROVEEDORES
    // ------------------------------------------------------------------

    private void menuProveedores() {
        int opcion;
        do {
            opcion = submenu("PROVEEDORES");
            switch (opcion) {
                case 1: listarProveedores(); break;
                case 2: agregarProveedor(); break;
                case 3: modificarProveedor(); break;
                case 4: eliminarProveedor(); break;
                default: break;
            }
        } while (opcion != 0);
    }

    private void listarProveedores() {
        if (proveedores.isEmpty()) {
            System.out.println("No hay proveedores cargados.");
            return;
        }
        for (Proveedor p : proveedores) {
            System.out.printf("DNI %d | %s | %s | Tel: %d | Razón social: %s | CUIT: %s%n",
                    p.getDni(), p.getNombre(), p.getDomicilio(), p.getTelefono(),
                    p.getRazonsocial(), p.getCuit());
        }
    }

    private void agregarProveedor() {
        System.out.println("\n--- Agregar proveedor ---");
        int dni = leerEntero("DNI", 1, Integer.MAX_VALUE);
        if (dniEnUso(dni)) {
            System.out.println("Ya existe una persona cargada con ese DNI.");
            return;
        }
        String nombre = leerTexto("Nombre");
        String domicilio = leerTexto("Domicilio");
        int telefono = leerEntero("Teléfono", 0, Integer.MAX_VALUE);
        String razonSocial = leerTexto("Razón social");
        String cuit = leerTexto("CUIT");
        try {
            proveedores.add(new Proveedor(nombre, domicilio, dni, telefono, razonSocial, cuit));
            System.out.println("Proveedor agregado.");
        } catch (DatoInvalidoException e) {
            System.out.println("Datos inválidos: " + e.getMessage());
        }
    }

    private void modificarProveedor() {
        Proveedor p = seleccionarProveedor();
        if (p == null) {
            return;
        }
        System.out.println("(Enter = mantener el valor actual. El DNI no se modifica.)");
        try {
            p.setNombre(leerTextoOpcional("Nombre", p.getNombre()));
            p.setDomicilio(leerTextoOpcional("Domicilio", p.getDomicilio()));
            p.setTelefono(leerEnteroOpcional("Teléfono", p.getTelefono(), 0));
            p.setRazonsocial(leerTextoOpcional("Razón social", p.getRazonsocial()));
            p.setCuit(leerTextoOpcional("CUIT", p.getCuit()));
            System.out.println("Proveedor modificado.");
        } catch (DatoInvalidoException e) {
            System.out.println("Datos inválidos: " + e.getMessage());
        }
    }

    private void eliminarProveedor() {
        Proveedor p = seleccionarProveedor();
        if (p == null) {
            return;
        }
        int cantidad = 0;
        for (ProductoServicio ps : productos) {
            if (ps.getProveedor() == p) {
                cantidad++;
            }
        }
        if (cantidad > 0) {
            System.out.println("No se puede eliminar: el proveedor tiene " + cantidad
                    + " producto(s)/servicio(s) asociado(s). Eliminalos o reasignalos primero.");
            return;
        }
        if (confirmar("¿Eliminar al proveedor " + p.getNombre() + "?")) {
            proveedores.remove(p);
            System.out.println("Proveedor eliminado.");
        }
    }

    private Proveedor seleccionarProveedor() {
        if (proveedores.isEmpty()) {
            System.out.println("No hay proveedores cargados.");
            return null;
        }
        listarProveedores();
        int dni = leerEntero("DNI del proveedor (0 para cancelar)", 0, Integer.MAX_VALUE);
        if (dni == 0) {
            return null;
        }
        for (Proveedor p : proveedores) {
            if (p.getDni() == dni) {
                return p;
            }
        }
        System.out.println("No existe un proveedor con ese DNI.");
        return null;
    }

    // ------------------------------------------------------------------
    // PRODUCTOS Y SERVICIOS
    // ------------------------------------------------------------------

    private void menuProductos() {
        int opcion;
        do {
            opcion = submenu("PRODUCTOS Y SERVICIOS");
            switch (opcion) {
                case 1: listarProductos(); break;
                case 2: agregarProducto(); break;
                case 3: modificarProducto(); break;
                case 4: eliminarProducto(); break;
                default: break;
            }
        } while (opcion != 0);
    }

    private void listarProductos() {
        if (productos.isEmpty()) {
            System.out.println("No hay productos ni servicios cargados.");
            return;
        }
        for (ProductoServicio ps : productos) {
            System.out.printf("ID %d | %s | %s | Base: $%d | Final: $%d | Proveedor: %s%n",
                    ps.getId(), ps.getTipo(), ps.getNombre(), ps.getPrecioBase(),
                    ps.calcularPrecioFinal(), ps.getProveedor().getNombre());
        }
    }

    private void agregarProducto() {
        System.out.println("\n--- Agregar producto o servicio ---");
        if (proveedores.isEmpty()) {
            System.out.println("Primero cargá al menos un proveedor.");
            return;
        }
        int tipo = leerEntero("Tipo (1 = Producto con IVA, 2 = Servicio)", 1, 2);
        String nombre = leerTexto("Nombre");
        int precioBase = leerEntero("Precio base", 0, Integer.MAX_VALUE);
        Proveedor proveedor = seleccionarProveedor();
        if (proveedor == null) {
            return;
        }
        int id = siguienteIdProducto();
        try {
            if (tipo == 1) {
                Producto producto = new Producto(id, nombre, precioBase, proveedor);
                proveedor.getListaProductos().add(producto);
                productos.add(producto);
            } else {
                productos.add(new Servicio(id, nombre, precioBase, proveedor));
            }
            System.out.println("Agregado con ID " + id + ".");
        } catch (DatoInvalidoException e) {
            System.out.println("Datos inválidos: " + e.getMessage());
        }
    }

    private void modificarProducto() {
        ProductoServicio ps = seleccionarProducto();
        if (ps == null) {
            return;
        }
        System.out.println("(Enter = mantener el valor actual. El tipo y el ID no se modifican.)");
        try {
            ps.setNombre(leerTextoOpcional("Nombre", ps.getNombre()));

            int nuevoPrecio = leerEnteroOpcional("Precio base", ps.getPrecioBase(), 0);
            if (nuevoPrecio != ps.getPrecioBase() && estaFacturado(ps)) {
                System.out.println("No se cambió el precio: este ítem ya figura en facturas emitidas.");
            } else {
                ps.setPrecioBase(nuevoPrecio);
            }

            System.out.println("Proveedor actual: " + ps.getProveedor().getNombre());
            if (confirmar("¿Cambiar de proveedor?")) {
                Proveedor nuevo = seleccionarProveedor();
                if (nuevo != null && nuevo != ps.getProveedor()) {
                    if (ps instanceof Producto) {
                        ps.getProveedor().getListaProductos().remove(ps);
                        nuevo.getListaProductos().add((Producto) ps);
                    }
                    ps.setProveedor(nuevo);
                }
            }
            System.out.println("Modificado correctamente.");
        } catch (DatoInvalidoException e) {
            System.out.println("Datos inválidos: " + e.getMessage());
        }
    }

    private void eliminarProducto() {
        ProductoServicio ps = seleccionarProducto();
        if (ps == null) {
            return;
        }
        if (estaFacturado(ps)) {
            System.out.println("No se puede eliminar: el ítem figura en facturas emitidas.");
            return;
        }
        if (confirmar("¿Eliminar \"" + ps.getNombre() + "\"?")) {
            if (ps instanceof Producto) {
                ps.getProveedor().getListaProductos().remove(ps);
            }
            productos.remove(ps);
            System.out.println("Eliminado.");
        }
    }

    private boolean estaFacturado(ProductoServicio ps) {
        for (Factura f : facturas) {
            if (f.getItemsComprados().contains(ps)) {
                return true;
            }
        }
        return false;
    }

    private int siguienteIdProducto() {
        int max = 0;
        for (ProductoServicio ps : productos) {
            max = Math.max(max, ps.getId());
        }
        return max + 1;
    }

    private ProductoServicio buscarProducto(int id) {
        for (ProductoServicio ps : productos) {
            if (ps.getId() == id) {
                return ps;
            }
        }
        return null;
    }

    private ProductoServicio seleccionarProducto() {
        if (productos.isEmpty()) {
            System.out.println("No hay productos ni servicios cargados.");
            return null;
        }
        listarProductos();
        int id = leerEntero("ID del ítem (0 para cancelar)", 0, Integer.MAX_VALUE);
        if (id == 0) {
            return null;
        }
        ProductoServicio ps = buscarProducto(id);
        if (ps == null) {
            System.out.println("No existe un ítem con ese ID.");
        }
        return ps;
    }

    // ------------------------------------------------------------------
    // FACTURAS
    // ------------------------------------------------------------------

    private void menuFacturas() {
        int opcion;
        do {
            opcion = submenu("FACTURAS");
            switch (opcion) {
                case 1: listarFacturas(); break;
                case 2: agregarFactura(); break;
                case 3: modificarFactura(); break;
                case 4: eliminarFactura(); break;
                default: break;
            }
        } while (opcion != 0);
    }

    private void listarFacturas() {
        if (facturas.isEmpty()) {
            System.out.println("No hay facturas emitidas.");
            return;
        }
        for (Factura f : facturas) {
            StringBuilder items = new StringBuilder();
            for (ProductoServicio ps : f.getItemsComprados()) {
                if (items.length() > 0) {
                    items.append(", ");
                }
                items.append(ps.getNombre());
            }
            String pago = f.getPago() == null
                    ? "sin pago"
                    : "$" + f.getPago().getMonto() + " (" + f.getPago().getEstado() + ")";
            System.out.printf("N° %d | %s | Cliente: %s | Vendedor: %s | Ítems: %s | Total: $%d | %s | Pago: %s%n",
                    f.getNumero(), f.getFecha(), f.getCliente().getNombre(), f.getEmpleado().getNombre(),
                    items, f.getMontoFacturable(), f.getFormapago(), pago);
        }
    }

    private void agregarFactura() {
        System.out.println("\n--- Emitir factura ---");
        if (clientes.isEmpty() || empleados.isEmpty() || productos.isEmpty()) {
            System.out.println("Para facturar necesitás al menos un cliente, un empleado y un producto/servicio.");
            return;
        }
        Cliente cliente = seleccionarCliente();
        if (cliente == null) {
            return;
        }
        Empleado empleado = seleccionarEmpleado();
        if (empleado == null) {
            return;
        }
        String fecha = leerFecha("Fecha (dd/MM/yyyy, Enter = hoy)", hoy());
        FormaPago formaPago = elegirEnum("Forma de pago", FormaPago.values(), null);

        listarProductos();
        System.out.println("Cargá los ítems por ID (Enter vacío para terminar).");
        List<ProductoServicio> items = new ArrayList<>();
        while (true) {
            System.out.print("ID del ítem: ");
            String texto = scanner.nextLine().trim();
            if (texto.isEmpty()) {
                break;
            }
            try {
                ProductoServicio ps = buscarProducto(Integer.parseInt(texto));
                if (ps == null) {
                    System.out.println("  No existe un ítem con ese ID.");
                } else {
                    items.add(ps);
                    System.out.println("  Agregado: " + ps.getDescripcion());
                }
            } catch (NumberFormatException e) {
                System.out.println("  Ingresá un número válido.");
            }
        }

        int monto = leerEntero("Monto pagado (0 = sin pago registrado)", 0, Integer.MAX_VALUE);
        int numero = siguienteNumeroFactura();
        try {
            Pago pago = null;
            if (monto > 0) {
                Estado estado = elegirEnum("Estado del pago", Estado.values(), null);
                pago = new Pago(monto, fecha, formaPago, estado);
            }
            Factura factura = new Factura(numero, fecha, cliente, empleado, items, formaPago, pago);
            cliente.registrarCompra(factura); // valida el límite de crédito
            facturas.add(factura);
            System.out.println("Factura N° " + numero + " emitida. Total: $" + factura.getMontoFacturable());
        } catch (FacturaSinItemsException | PagoExcedeDeudaException | LimiteCreditoExcedidoException e) {
            System.out.println("No se pudo emitir la factura: " + e.getMessage());
        } catch (DatoInvalidoException e) {
            System.out.println("Datos inválidos: " + e.getMessage());
        }
    }

    private void modificarFactura() {
        Factura f = seleccionarFactura();
        if (f == null) {
            return;
        }
        System.out.println("(Enter = mantener el valor actual. El número, cliente, vendedor e ítems no se modifican.)");
        String nuevaFecha = leerFecha("Fecha [" + f.getFecha() + "]", f.getFecha());
        FormaPago nuevaForma = elegirEnum("Forma de pago", FormaPago.values(), f.getFormapago());

        Pago nuevoPago = f.getPago();
        if (confirmar("¿Modificar el pago?")) {
            int monto = leerEntero("Monto pagado (0 = quitar el pago)", 0, Integer.MAX_VALUE);
            if (monto == 0) {
                nuevoPago = null;
            } else {
                Estado estadoActual = f.getPago() != null ? f.getPago().getEstado() : null;
                Estado estado = elegirEnum("Estado del pago", Estado.values(), estadoActual);
                nuevoPago = new Pago(monto, nuevaFecha, nuevaForma, estado);
            }
        }

        try {
            f.setPago(nuevoPago); // valida que no exceda el total (si falla, no se cambia nada)
            f.setFecha(nuevaFecha);
            f.setFormapago(nuevaForma);
            System.out.println("Factura modificada.");
        } catch (PagoExcedeDeudaException e) {
            System.out.println("No se modificó la factura: " + e.getMessage());
        } catch (DatoInvalidoException e) {
            System.out.println("Datos inválidos: " + e.getMessage());
        }
    }

    private void eliminarFactura() {
        Factura f = seleccionarFactura();
        if (f == null) {
            return;
        }
        if (confirmar("¿Eliminar la factura N° " + f.getNumero() + "?")) {
            facturas.remove(f);
            f.getCliente().getHistorialCompras().remove(f);
            System.out.println("Factura eliminada.");
        }
    }

    private int siguienteNumeroFactura() {
        int max = 0;
        for (Factura f : facturas) {
            max = Math.max(max, f.getNumero());
        }
        return max + 1;
    }

    private Factura seleccionarFactura() {
        if (facturas.isEmpty()) {
            System.out.println("No hay facturas emitidas.");
            return null;
        }
        listarFacturas();
        int numero = leerEntero("N° de factura (0 para cancelar)", 0, Integer.MAX_VALUE);
        if (numero == 0) {
            return null;
        }
        for (Factura f : facturas) {
            if (f.getNumero() == numero) {
                return f;
            }
        }
        System.out.println("No existe una factura con ese número.");
        return null;
    }

    // ------------------------------------------------------------------
    // Utilidades compartidas
    // ------------------------------------------------------------------

    // El DNI identifica a cualquier persona del sistema (cliente, empleado o proveedor)
    private boolean dniEnUso(int dni) {
        for (Cliente c : clientes) {
            if (c.getDni() == dni) {
                return true;
            }
        }
        for (Empleado e : empleados) {
            if (e.getDni() == dni) {
                return true;
            }
        }
        for (Proveedor p : proveedores) {
            if (p.getDni() == dni) {
                return true;
            }
        }
        return false;
    }

    private String hoy() {
        return LocalDate.now().format(FORMATO_FECHA);
    }

    private boolean confirmar(String pregunta) {
        System.out.print(pregunta + " (s/n): ");
        String r = scanner.nextLine().trim().toLowerCase();
        return r.equals("s") || r.equals("si") || r.equals("sí");
    }

    private int leerEntero(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + ": ");
            String texto = scanner.nextLine().trim();
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.println("  El valor tiene que ser mayor o igual a " + min
                        + (max != Integer.MAX_VALUE ? " y menor o igual a " + max : "") + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Ingresá un número válido.");
            }
        }
    }

    private int leerEnteroOpcional(String prompt, int actual, int min) {
        while (true) {
            System.out.print(prompt + " [" + actual + "]: ");
            String texto = scanner.nextLine().trim();
            if (texto.isEmpty()) {
                return actual;
            }
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= min) {
                    return valor;
                }
                System.out.println("  El valor tiene que ser mayor o igual a " + min + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Ingresá un número válido.");
            }
        }
    }

    private String leerTexto(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("  No puede quedar vacío.");
        }
    }

    private String leerTextoOpcional(String prompt, String actual) {
        System.out.print(prompt + " [" + actual + "]: ");
        String texto = scanner.nextLine().trim();
        return texto.isEmpty() ? actual : texto;
    }

    // Lee una fecha dd/MM/yyyy; Enter devuelve el valor por defecto
    private String leerFecha(String prompt, String porDefecto) {
        while (true) {
            System.out.print(prompt + ": ");
            String texto = scanner.nextLine().trim();
            if (texto.isEmpty()) {
                return porDefecto;
            }
            try {
                LocalDate.parse(texto, FORMATO_FECHA);
                return texto;
            } catch (DateTimeParseException e) {
                System.out.println("  Fecha inválida. Usá el formato dd/MM/yyyy (ej: 15/03/2024).");
            }
        }
    }

    // Muestra los valores de un enum numerados. Si "actual" no es null, Enter lo conserva.
    private <E extends Enum<E>> E elegirEnum(String titulo, E[] valores, E actual) {
        System.out.println(titulo + ":");
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + ". " + valores[i]);
        }
        String sufijo = actual != null ? " [" + actual + "]" : "";
        while (true) {
            System.out.print("Elegí una opción" + sufijo + ": ");
            String texto = scanner.nextLine().trim();
            if (texto.isEmpty() && actual != null) {
                return actual;
            }
            try {
                int n = Integer.parseInt(texto);
                if (n >= 1 && n <= valores.length) {
                    return valores[n - 1];
                }
            } catch (NumberFormatException e) {
                // se muestra el mensaje de opción inválida más abajo
            }
            System.out.println("  Opción inválida.");
        }
    }
}
