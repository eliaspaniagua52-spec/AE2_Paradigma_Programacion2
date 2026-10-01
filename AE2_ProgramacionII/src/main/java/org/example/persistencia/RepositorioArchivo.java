package org.example.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.example.excepciones.PersistenciaException;
import org.example.interfaces.Guardable;

// Clase con una única responsabilidad: guardar y leer elementos Guardable en un archivo de texto.
public class RepositorioArchivo {
    private final Path ruta;

    public RepositorioArchivo(String rutaArchivo) {
        this.ruta = Paths.get(rutaArchivo);
    }

    // Escribe una línea por elemento (sobrescribe el archivo si ya existía)
    public void guardar(List<? extends Guardable> elementos) throws PersistenciaException {
        List<String> lineas = new ArrayList<>();
        for (Guardable g : elementos) {
            lineas.add(g.aLineaArchivo());
        }
        try {
            Files.write(ruta, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo guardar el archivo " + ruta, e);
        }
    }

    // Devuelve las líneas del archivo
    public List<String> leer() throws PersistenciaException {
        try {
            return Files.readAllLines(ruta, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer el archivo " + ruta, e);
        }
    }

    public Path getRuta() {
        return ruta;
    }
}
