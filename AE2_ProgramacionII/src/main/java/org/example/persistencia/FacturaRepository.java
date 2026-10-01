package org.example.Persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ObjetosClave.Factura;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FacturaRepository {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File archivo = new File("datos/facturas.json");

    public void guardarTodas(List<Factura> facturas) throws IOException {
        File carpeta = archivo.getParentFile();
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        mapper.writerFor(new TypeReference<List<Factura>>() {})
                .withDefaultPrettyPrinter()
                .writeValue(archivo, facturas);
    }

    public List<Factura> obtenerTodas() throws IOException {
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        return mapper.readValue(archivo, new TypeReference<List<Factura>>() {});
    }
}