    package org.example.ObjetosClave;
    import org.example.Enums.FormaPago;
    import org.example.Personas.Cliente;
    import org.example.Personas.Empleado;
    import java.util.List;
    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    @JsonIgnoreProperties({"total"})
    public class Factura implements Comparable<Factura> {
        private String numero;
        private String fecha;
        @JsonIgnoreProperties("historialCompras")
        private Cliente cliente;
        private List<ItemComercial> itemsComprados;
        private FormaPago formapago;
        private Pago pago;

        public Factura(String numero, String fecha, Cliente cliente,
                        List<ItemComercial> itemsFactura, FormaPago formapago, Pago pago){
            this.numero = numero;
            this.fecha = fecha;
            this.cliente = cliente;
            this.formapago = formapago;
            this.pago = pago;
            this.itemsComprados = itemsFactura;
        }





        public Factura() {}

        public String getNumero() {
            return numero;
        }

        public Cliente getCliente() {
            return cliente;
        }

        public String getFecha() {
            return fecha;
        }


        public List<ItemComercial> getItemsComprados() {
            return itemsComprados;
        }

        public FormaPago getFormapago() {
            return formapago;
        }

        public Pago getPago() {
            return pago;
        }
        public double getTotal() {
            double total = 0;
            for (ItemComercial item : itemsComprados) {
                total += item.getPrecio();
            }
            return total;
        }


        @Override
        public int compareTo(Factura otra) {
            return this.numero.compareTo(otra.numero);
        }
        @Override
        public String toString() {
            return "Factura " + numero + " | " + fecha + " | Cliente: " + cliente.getNombre() + " | Total: $" + getTotal();
        }

    }
