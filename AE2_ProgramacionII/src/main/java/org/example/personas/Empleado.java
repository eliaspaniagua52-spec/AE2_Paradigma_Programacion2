<<<<<<<< HEAD:src/main/java/org/example/personas/Empleado.java
package org.example.personas;
========
package org.example.Personas;

import org.example.ObjetosClave.Departamento;
import org.example.Enums.Puesto;
>>>>>>>> de3e3cc60a7da5493cf7eab6bb96f73e86fd5abb:src/main/java/org/example/Personas/Empleado.java

public class Empleado extends Persona {
    private Puesto puesto;
    private double salario;
    private String fechaIngreso;
    private Departamento departamento;

    public Empleado(String nombre, String domicilio, int dni, String telefono, double salario, Puesto puesto, String fechaIngreso,
                    Departamento departamento) {
        super(nombre, domicilio, dni, telefono);
        this.salario = salario;
        this.puesto = puesto;
        this.fechaIngreso = fechaIngreso;
        this.departamento = departamento;
    }

<<<<<<<< HEAD:src/main/java/org/example/personas/Empleado.java
    // Implementación del método abstracto heredado de Persona
    @Override
    public String getRol() {
        return "Empleado - " + puesto + " en " + departamento.getNombre();
    }

    public int getSalario() {
        return salario;
    }

    public void setSalario(int salario) {
========
    public double getSalario(){
        return salario;
    }
    public void setSalario(double salario){
>>>>>>>> de3e3cc60a7da5493cf7eab6bb96f73e86fd5abb:src/main/java/org/example/Personas/Empleado.java
        this.salario = salario;
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public void setPuesto(Puesto puesto) {
        this.puesto = puesto;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

<<<<<<<< HEAD:src/main/java/org/example/personas/Empleado.java
    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
========
    @Override
    public String mostrarDatos(){
        return getNombre() + " | DNI: " + getDni() + " | Puesto: " + puesto + " | Salario: $" + salario;
    }
    @Override
    public String toString() {
        return getNombre() + " | DNI: " + getDni() + " | Puesto: " + puesto;
    }
    public Empleado() {}
>>>>>>>> de3e3cc60a7da5493cf7eab6bb96f73e86fd5abb:src/main/java/org/example/Personas/Empleado.java
}
