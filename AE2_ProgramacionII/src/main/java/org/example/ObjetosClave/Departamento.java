<<<<<<<< HEAD:src/main/java/org/example/personas/Departamento.java
package org.example.personas;

import java.util.ArrayList;
import java.util.List;
========
package org.example.ObjetosClave;
import org.example.Personas.Empleado;

import java.util.List;
import java.util.ArrayList;
>>>>>>>> de3e3cc60a7da5493cf7eab6bb96f73e86fd5abb:src/main/java/org/example/ObjetosClave/Departamento.java

public class Departamento {
    private String nombre;
    private int presupuesto;
    private Empleado responsable;
    private List<Empleado> listaEmpleados;

    public Departamento(String nombre, int presupuesto, Empleado responsable) {
        this.nombre = nombre;
        this.presupuesto = presupuesto;
        this.responsable = responsable;
        this.listaEmpleados = new ArrayList<>();
    }
    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public int getPresupuesto(){
        return presupuesto;
    }
    public void setPrepuesto(int presupuesto){
        this.presupuesto = presupuesto;
    }
    public Empleado getResponsable(){
        return responsable;
    }

    public void setResponsable(Empleado responsable){
        this.responsable = responsable;
    }
    public List<Empleado> getListaEmpleados(){
        return listaEmpleados;
    }
    public void agregarEmpleado(Empleado NuevoEmpleado){
        listaEmpleados.add(NuevoEmpleado);
    }
    public void quitarEmpleado(Empleado DespedirEmpleado){
        listaEmpleados.remove(DespedirEmpleado);
    }
    @Override
    public String toString(){
        return String.format("Departamento: %s | Presupuesto: %d | Responsable: %s",
                nombre, presupuesto, responsable != null ? responsable.getNombre() : "(sin responsable)");
    }

}
