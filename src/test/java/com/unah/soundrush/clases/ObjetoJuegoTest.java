package com.unah.soundrush.clases;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.scene.canvas.GraphicsContext;
import org.junit.jupiter.api.Test;

class ObjetoJuegoTest {

    /** Objeto minimo para probar la logica de colisiones sin dibujar nada. */
    private static ObjetoJuego caja(double x, double y, int ancho, int alto) {
        return new ObjetoJuego(x, y, ancho, alto, 0, null) {
            @Override public void pintar(GraphicsContext gc) { }
            @Override public void actualizar() { }
        };
    }

    @Test
    void detectaColisionCuandoSeSuperponen() {
        assertTrue(caja(0, 0, 32, 32).colisionaCon(caja(16, 16, 32, 32)));
    }

    @Test
    void noHayColisionCuandoEstanSeparados() {
        assertFalse(caja(0, 0, 32, 32).colisionaCon(caja(100, 100, 32, 32)));
    }

    @Test
    void colisionEsSimetrica() {
        ObjetoJuego a = caja(10, 10, 20, 20);
        ObjetoJuego b = caja(25, 5, 20, 20);
        assertTrue(a.colisionaCon(b));
        assertTrue(b.colisionaCon(a));
    }
}
