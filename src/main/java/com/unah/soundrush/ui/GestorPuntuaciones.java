package com.unah.soundrush.ui;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestiona las 10 mejores puntuaciones en un archivo de texto.
 * Usa HashMap<String, Integer> (nombre → puntaje) para almacenamiento interno,
 * conservando el mejor puntaje por jugador.
 */
public class GestorPuntuaciones {

    private static final String ARCHIVO = "puntuaciones.txt";
    private static final int MAX_PUNTUACIONES = 10;

    // HashMap: clave = nombre del jugador, valor = mejor puntuación
    private HashMap<String, Integer> puntuaciones;

    public GestorPuntuaciones() {
        puntuaciones = new HashMap<>();
        cargarDesdeArchivo();
    }

    public void cargarDesdeArchivo() {
        puntuaciones.clear();
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    String nombre = partes[0].trim();
                    int pts = Integer.parseInt(partes[1].trim());
                    // Si el nombre ya existe, conservar el mayor puntaje
                    puntuaciones.merge(nombre, pts, Math::max);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al leer puntuaciones: " + e.getMessage());
        }
    }

    public void guardarEnArchivo() {
        List<Map.Entry<String, Integer>> lista = getListaOrdenada();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO))) {
            for (Map.Entry<String, Integer> entrada : lista) {
                writer.write(entrada.getKey() + "," + entrada.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar puntuaciones: " + e.getMessage());
        }
    }

    /**
     * Verifica si la puntuación entra al top 10.
     */
    public boolean esNuevoRecord(int puntos) {
        if (puntuaciones.size() < MAX_PUNTUACIONES) return true;
        // Buscar la puntuación mínima del top 10
        return puntos > Collections.min(puntuaciones.values());
    }

    /**
     * Agrega o actualiza la puntuación del jugador (conserva la mejor).
     * Recorta al top 10 y guarda en archivo.
     */
    public void agregarPuntuacion(String nombre, int puntos) {
        // Guardar solo si es mejor que el puntaje previo del mismo nombre
        puntuaciones.merge(nombre, puntos, Math::max);

        // Recortar al top 10 eliminando la entrada con menor puntaje
        while (puntuaciones.size() > MAX_PUNTUACIONES) {
            String peorNombre = Collections.min(
                puntuaciones.entrySet(),
                Map.Entry.comparingByValue()
            ).getKey();
            puntuaciones.remove(peorNombre);
        }

        guardarEnArchivo();
    }

    /**
     * Retorna lista ordenada de entradas (mayor a menor) para el HUD.
     */
    public List<Map.Entry<String, Integer>> getPuntuaciones() {
        return getListaOrdenada();
    }

    private List<Map.Entry<String, Integer>> getListaOrdenada() {
        List<Map.Entry<String, Integer>> lista = new ArrayList<>(puntuaciones.entrySet());
        lista.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // Descendente
        return lista;
    }
}
