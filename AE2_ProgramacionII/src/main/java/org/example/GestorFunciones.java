package org.example;
import org.example.Enums.Categoria;
import org.example.Enums.Estado;
import org.example.Enums.FormaPago;
import org.example.Enums.Puesto;
import org.example.Exceptions.CreditoInsuficienteException;
import org.example.Exceptions.FacturaSinItemsException;
import org.example.Interfaces.ComparadorPorTotal;
import org.example.ObjetosClave.*;
import org.example.Persistencia.FacturaRepository;
import org.example.Personas.Cliente;
import org.example.Personas.Empleado;
import org.example.Personas.Proveedor;

import java.io.IOException;
import java.util.Collections;


import java.util.*;

public class GestorFunciones {
    private List<Departamento> listaDepartamentos;
    private List<Empleado> listaEmpleados;
    private List<Cliente> listaClientes;
    private List<Producto> listaProductos;
    private List<Servicio> listaServicios;
    private List<Factura> listaFacturas;
    private FacturaRepository facturaRepository = new FacturaRepository();

    public GestorFunciones() {
        this.listaDepartamentos = new ArrayList<>();
        this.listaEmpleados = new ArrayList<>();
        this.listaClientes = new ArrayList<>();
        this.listaProductos = new ArrayList<>();
        this.listaServicios = new ArrayList<>();
        this.listaFacturas = new ArrayList<>();
        try {
            this.listaFacturas = facturaRepository.obtenerTodas();
        } catch (IOException e) {
            System.out.println("No se pudo leer facturas, se arranca vacio: " + e.getMessage());
            this.listaFacturas = new ArrayList<>();
        }
    }

    Scanner entrada = new Scanner(System.in);
    int contador = 0;
    Random random = new Random();
    int limiteCredito = 30000 + (random.nextInt(20001));

    public void registrarEmpleado() {
        System.out.println("=== Registro de Empleados ===");
        System.out.println("== Informacion personal ==");
        System.out.println("Ingrese el nombre: ");
        String nombre = entrada.nextLine();
        System.out.println("Ingrese el domicilio: ");
        String domicilio = entrada.nextLine();
        System.out.println("Ingrese el dni: ");
        int dni = entrada.nextInt();
        System.out.println("Ingrese el numero de telefono: ");
        String telefono = entrada.nextLine();
        entrada.nextLine();
        System.out.println("== Informacion de empleo ==");
        System.out.println("Puesto (ADMINISTRATIVO / TECNICO / GERENTE): ");
        String puestoTexto = entrada.nextLine().trim().toUpperCase();
        Puesto puesto = Puesto.valueOf(puestoTexto);
        System.out.println("Ingrese el salario: ");
        double salario = entrada.nextDouble();
        entrada.nextLine();
        System.out.println("Ingrese la fecha de ingreso: ");
        String fechaIngreso = entrada.nextLine();
        System.out.println("Departamentos disponibles: ");
        for (Departamento t : listaDepartamentos) {
            System.out.println("-" + t.getNombre());
        }
        System.out.println("Ingrese el departamento a ingresar: ");
        String departamentoElegido = entrada.nextLine();
        Departamento departamento = buscarDepartamentoPorNombre(departamentoElegido);
        if (departamento == null) {
            System.out.println("Ese departamento no existe. No se puede registrar el empleado");
            return;
        }
        Empleado nuevoEmpleado = new Empleado(nombre, domicilio, dni, telefono, salario, puesto, fechaIngreso, departamento);
        departamento.agregarEmpleado(nuevoEmpleado);
        listaEmpleados.add(nuevoEmpleado);
        System.out.println("--- Empleado " + nuevoEmpleado.getNombre() + " registrado ---");
    }

    public void agregarDepartamento(Departamento d) {
        listaDepartamentos.add(d);
    }

    private Departamento buscarDepartamentoPorNombre(String nombreBuscado) {
        for (Departamento t : listaDepartamentos) {
            if (t.getNombre().equalsIgnoreCase(nombreBuscado)) {
                return t;
            }

        }
        return null;
    }

    public void mostrarEmpleados() {
        if (listaEmpleados.isEmpty()) {
            System.out.println("No hay empleados registrados");
        }
        System.out.println("");
        System.out.println("=== Empleados ===");
        for (Empleado e : listaEmpleados) {
            contador++;
            System.out.println("Empleado " + "(" + contador + ")");
            System.out.println("Nombre: " + e.getNombre());
            System.out.println("Domicilio " + e.getDomicilio());
            System.out.println("DNI: " + e.getDni());
            System.out.println("Telefono: " + e.getTelefono());
            System.out.println("Puesto: " + e.getPuesto());
            System.out.println("Salario: " + e.getSalario());
            System.out.println("Fecha de ingreso: " + e.getFechaIngreso());
            System.out.println("Departamento: " + e.getDepartamento().getNombre());
            System.out.println("===================================");
        }
    }

    public void asignarResponsable() {
        if (listaEmpleados.isEmpty()) {
            System.out.println("No hay empleados para asignar responsable");
            return;
        }
        List<Departamento> departamentosFaltantes = new ArrayList<>();
        for (Departamento t : listaDepartamentos) {
            if (t.getResponsable() == null) {
                departamentosFaltantes.add(t);
            }
        }
        if (departamentosFaltantes.isEmpty()) {
            System.out.println("No hay departamentos sin responsable por el momento");
            return;
        }
        System.out.println("Departamentos sin responsable asignado: ");
        for (Departamento r : departamentosFaltantes) {
            System.out.println("Nombre: " + r.getNombre());
            System.out.println("Presupuesto: " + r.getPresupuesto());
            System.out.println("Empleados: ");
            for (Empleado y : r.getListaEmpleados()) {
                System.out.println(y.getNombre() + " | " + y.getPuesto());
                System.out.println("--------------------------------------------");
            }
        }
        System.out.println("Ingrese el nombre del departamento a asignar responsable: ");
        boolean departamentoEncontrado = false;
        String departamentoElegido = entrada.nextLine().trim();
        for (Departamento r : departamentosFaltantes) {
            if (departamentoElegido.equalsIgnoreCase(r.getNombre())) {
                departamentoEncontrado = true;
                System.out.println("A continuacion ingrese el nombre del empleado responsable: ");
                String empleadoElegido = entrada.nextLine().trim();
                boolean empleadoEncontrado = false;
                for (Empleado t : r.getListaEmpleados()) {
                    if (empleadoElegido.equalsIgnoreCase(t.getNombre())) {
                        empleadoEncontrado = true;
                        r.setResponsable(t);
                        System.out.println("Responsable " + t.getNombre() + " asignado al departamento " + r.getNombre());
                        break;
                    }
                }
                if (!empleadoEncontrado) {
                    System.out.println("Ese empleado no pertenece a ese departamento");
                }
                break;
            }
        }
        if (!departamentoEncontrado) {
            System.out.println("Ese departamento no existe o ya tiene responsable");
        }

    }

    public void mostrarDepartamentos() {
        System.out.println("");
        System.out.println("==== Departamentos ===");
        for (Departamento t : listaDepartamentos) {
            System.out.println("Nombre: " + t.getNombre());
            System.out.println("Presupuesto: " + t.getPresupuesto());
            if (t.getResponsable() == null) {
                System.out.println("Responsable: nadie");
            }{
                System.out.println("Responsable: " + t.getResponsable());
            }
            if (t.getListaEmpleados().isEmpty()) {
                System.out.println("No hay empleados en este departamento");
                System.out.println("==================================");
            } else {
                System.out.println("Empleados: ");
                for (Empleado y : t.getListaEmpleados()) {
                    System.out.println(y.getNombre() + " | " + y.getPuesto());
                    System.out.println("==================================");
                }
            }
        }
    }

        public Cliente buscarCliente (String nombre,int dni){
            for (Cliente c : listaClientes) {
                if (c.getNombre().equalsIgnoreCase(nombre) && c.getDni() == dni) {
                    return c;
                }
            }
            return null;
        }
        private int asignarCreditoPorCategoria (Categoria categoria){
            switch (categoria) {
                case PREMIUM:
                    return 50000;
                case CORPORATIVO:
                    return 100000;
                default:
                    return 30000;
            }

        }
        public void agregarProducto (Producto p){
            this.listaProductos.add(p);
        }

        public void agregarServicio (Servicio s){
            this.listaServicios.add(s);
        }

        public String generarCodigoAleatorio () {
            String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            StringBuilder codigo = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                int indice = random.nextInt(letras.length());
                codigo.append(letras.charAt(indice));
            }
            return codigo.toString();
        }

        public Producto crearProductoAleatorio (String nombre, Proveedor proveedor){
            String codigo = generarCodigoAleatorio();
            int precio = 6000 + random.nextInt(70001);
            int stock = 2 + random.nextInt(11);
            Producto Nuevoproducto = new Producto(codigo, nombre, precio, stock, proveedor);
            listaProductos.add(Nuevoproducto);
            return Nuevoproducto;
        }

        public Servicio crearServicioAleatorio (String nombre, Proveedor proveedor ){
            String codigo = generarCodigoAleatorio();
            int precio = 6000 + random.nextInt(70001);
            Servicio nuevoServicio = new Servicio(codigo, nombre, precio, proveedor);
            listaServicios.add(nuevoServicio);
            return nuevoServicio;
        }
    public void registrarPago() throws FacturaSinItemsException, CreditoInsuficienteException {
        System.out.println("==== Registro de compra ====");
        System.out.println("Esta el cliente registrado en el sistema? (s/n): ");
        String respuesta = entrada.nextLine().trim().toLowerCase();

        Cliente cliente = null;

        if (respuesta.equals("s") || respuesta.equals("si")) {
            boolean seguirBuscando = true;
            while (seguirBuscando && cliente == null) {
                System.out.println("Ingrese el nombre del cliente: ");
                String nombreIngresado = entrada.nextLine();
                System.out.println("Ingrese el dni del cliente: ");
                int dniIngresado = entrada.nextInt();
                entrada.nextLine();

                cliente = buscarCliente(nombreIngresado, dniIngresado);

                if (cliente == null) {
                    System.out.println("No se encontro ningun cliente con esos datos. Desea intentar de nuevo? (s/n): ");
                    String reintentar = entrada.nextLine().trim().toLowerCase();
                    seguirBuscando = reintentar.equals("s") || reintentar.equals("si");
                }
            }
        }

        if (cliente == null) {
            System.out.println("--- Registro de nuevo cliente ---");
            System.out.println("Ingrese el nombre del cliente: ");
            String nombreNuevo = entrada.nextLine();
            System.out.println("Ingrese el dni del cliente: ");
            int dniNuevo = entrada.nextInt();
            entrada.nextLine();

            Categoria categoria = Categoria.REGULAR;
            int limiteCredito = asignarCreditoPorCategoria(categoria);
            cliente = new Cliente(nombreNuevo, "Sin especificar", dniNuevo, "0000-0000", limiteCredito, categoria);
            listaClientes.add(cliente);
            System.out.println("Cliente registrado con categoria REGULAR y credito de $" + limiteCredito);
        }

        System.out.println("¡Hola " + cliente.getNombre() + "!");
        List<ItemComercial> carrito = new ArrayList<>();
        boolean seguirComprando = true;

        while (seguirComprando) {
            System.out.println("===== Productos =====");
            for (Producto p : listaProductos) {
                System.out.println("Codigo: "+p.getCodigo() + " | " + p.getNombre() + " | $" + p.getPrecio() + " | Stock: " + p.getStock());
            }
            System.out.println("===== Servicios =====");
            for (Servicio s : listaServicios) {
                System.out.println(s.getCodigo() + " | " + s.getNombre() + " | $" + s.getPrecio());
            }

            System.out.println("Desea comprar producto o servicio? (p/s): ");
            String rts = entrada.nextLine().trim().toLowerCase();

            if (rts.equals("p")) {
                System.out.println("Ingrese el codigo del producto: ");
                String codigoProducto = entrada.nextLine().trim();

                Producto encontrado = null;
                for (Producto p : listaProductos) {
                    if (codigoProducto.equalsIgnoreCase(p.getCodigo())) {
                        encontrado = p;
                        break;
                    }
                }

                if (encontrado == null) {
                    System.out.println("Producto no encontrado");
                } else if (encontrado.getStock() <= 0) {
                    System.out.println("Sin stock disponible");
                } else {
                    carrito.add(encontrado);
                    encontrado.setStockResta(1);
                    System.out.println("Producto agregado al carrito");
                }

            } else if (rts.equals("s")) {
                System.out.println("Ingrese el codigo del servicio: ");
                String codigo = entrada.nextLine().trim();

                Servicio encontrado = null;
                for (Servicio s : listaServicios) {
                    if (codigo.equalsIgnoreCase(s.getCodigo())) {
                        encontrado = s;
                        break;
                    }
                }

                if (encontrado == null) {
                    System.out.println("Servicio no encontrado");
                } else {
                    carrito.add(encontrado);
                    System.out.println("Servicio agregado al carrito");
                }

            } else {
                System.out.println("Opcion invalida");
            }

            System.out.println("Desea agregar algo mas? (s/n): ");
            seguirComprando = entrada.nextLine().trim().toLowerCase().startsWith("s");
        }

        if (carrito.isEmpty()) {
            throw new FacturaSinItemsException("No se puede generar una factura sin items");
        }

        double total = 0;
        for (ItemComercial item : carrito) {
            total += item.getPrecio();
        }
        System.out.println("Total a pagar: $" + total);

        System.out.println("Forma de pago (EFECTIVO/TARJETA/TRANSFERENCIA): ");
        FormaPago formaPago = FormaPago.valueOf(entrada.nextLine().trim().toUpperCase());

        System.out.println("Ingrese el monto que abona el cliente: ");
        double montoPagado = entrada.nextDouble();
        entrada.nextLine();

        Estado estado;
        if (montoPagado <= 0) {
            estado = Estado.PENDIENTE;
        } else if (montoPagado < total) {
            estado = Estado.PARCIAL;
        } else {
            estado = Estado.CANCELADO;
        }

        Pago pago = new Pago(montoPagado, java.time.LocalDate.now().toString(), formaPago, estado);
        String numeroFactura = generarCodigoAleatorio();
        Factura factura = new Factura(numeroFactura, java.time.LocalDate.now().toString(), cliente, carrito, formaPago, pago);

        cliente.getHistorialCompras().add(factura);
        listaFacturas.add(factura);
        try {
            facturaRepository.guardarTodas(listaFacturas);
        } catch (IOException e) {
            System.out.println("No se pudo guardar la factura: " + e.getMessage());
        }
        if (estado != Estado.CANCELADO) {
            double deudaActual = cliente.getLimiteCredito();
            if (total - montoPagado > cliente.getLimiteCredito()) {
                throw new CreditoInsuficienteException(cliente.getNombre() + " supera su limite de credito permitido");
            }
        }
        System.out.println("===== Factura generada =====");
        System.out.println(factura);
        System.out.println("Estado del pago: " + estado);
    }


    public void mostrarFacturasOrdenadasPorNumero() {
        Collections.sort(listaFacturas); // usa el compareTo (Comparable) de Factura
        for (Factura f : listaFacturas) {
            System.out.println(f.getNumero() + " - $" + f.getTotal());
        }
    }

    public void mostrarFacturasOrdenadasPorTotal() {
        Collections.sort(listaFacturas, new ComparadorPorTotal()); // usa el Comparator
        for (Factura f : listaFacturas) {
            System.out.println(f.getNumero() + " - $" + f.getTotal());
        }
    }
    }




