package com.unah.soundrush.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GestorPuntuacionesTest {

    @TempDir
    Path carpeta;

    private GestorPuntuaciones nuevoGestor() {
        return new GestorPuntuaciones(carpeta.resolve("puntuaciones.txt").toString());
    }

    @Test
    void ordenaDeMayorAMenor() {
        GestorPuntuaciones g = nuevoGestor();
        g.agregarPuntuacion("Ana", 100);
        g.agregarPuntuacion("Luis", 300);
        g.agregarPuntuacion("Maria", 200);

        List<Map.Entry<String, Integer>> lista = g.getPuntuaciones();
        assertEquals("Luis", lista.get(0).getKey());
        assertEquals("Maria", lista.get(1).getKey());
        assertEquals("Ana", lista.get(2).getKey());
    }

    @Test
    void conservaElMejorPuntajeDeCadaJugador() {
        GestorPuntuaciones g = nuevoGestor();
        g.agregarPuntuacion("Ana", 500);
        g.agregarPuntuacion("Ana", 100);

        assertEquals(1, g.getPuntuaciones().size());
        assertEquals(500, g.getPuntuaciones().get(0).getValue());
    }

    @Test
    void recortaAlTop10() {
        GestorPuntuaciones g = nuevoGestor();
        for (int i = 1; i <= 12; i++) {
            g.agregarPuntuacion("J" + i, i * 10);
        }
        List<Map.Entry<String, Integer>> lista = g.getPuntuaciones();
        assertEquals(10, lista.size());
        assertEquals(120, lista.get(0).getValue());
        assertEquals(30, lista.get(9).getValue());
    }

    @Test
    void esNuevoRecordSoloSiSuperaAlMinimoConTablaLlena() {
        GestorPuntuaciones g = nuevoGestor();
        assertTrue(g.esNuevoRecord(1));
        for (int i = 1; i <= 10; i++) {
            g.agregarPuntuacion("J" + i, i * 10);
        }
        assertFalse(g.esNuevoRecord(10));
        assertTrue(g.esNuevoRecord(11));
    }

    @Test
    void persisteYVuelveACargarDesdeArchivo() throws Exception {
        GestorPuntuaciones g = nuevoGestor();
        g.agregarPuntuacion("Elian", 3010);

        GestorPuntuaciones recargado = nuevoGestor();
        assertEquals(3010, recargado.getPuntuaciones().get(0).getValue());
        assertTrue(Files.readString(carpeta.resolve("puntuaciones.txt")).contains("Elian,3010"));
    }

    @Test
    void ignoraLineasMalFormadas() throws Exception {
        Path archivo = carpeta.resolve("puntuaciones.txt");
        Files.writeString(archivo, "Ana,100\nlinea sin coma\nLuis,200\n");

        GestorPuntuaciones g = new GestorPuntuaciones(archivo.toString());
        assertEquals(2, g.getPuntuaciones().size());
    }
}
