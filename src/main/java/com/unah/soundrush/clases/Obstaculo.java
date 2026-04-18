package com.unah.soundrush.clases;

import com.unah.soundrush.App;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

/**
 * Obstáculo: zonas no transitables del escenario.
 * Alta visibilidad — glow neón, bordes brillantes, fácilmente distinguibles.
 * Hereda de ObjetoJuego.
 */
public class Obstaculo extends ObjetoJuego {

    public enum TipoObstaculo {
        MURO_SILENCIO,   // bloque rojo neón pulsante
        BARRERA_VIENTO,  // barrera azul/cian con líneas de energía
        ZONA_MUERTA      // zona naranja/amarillo advertencia
    }

    private TipoObstaculo tipo;
    private double fase; // fase de animación individual

    public Obstaculo(double x, double y, int ancho, int alto, TipoObstaculo tipo) {
        super(x, y, ancho, alto, 0, "obstaculo");
        this.tipo = tipo;
        this.fase = Math.random() * Math.PI * 2;
    }

    @Override public void actualizar() { fase += 0.04; }

    @Override
    public void pintar(GraphicsContext gc) {
        if (!activo) return;
        switch (tipo) {
            case MURO_SILENCIO  -> pintarMuroRojo(gc);
            case BARRERA_VIENTO -> pintarBarreraCian(gc);
            case ZONA_MUERTA    -> pintarZonaAdvertencia(gc);
        }
    }

    /** Muro rojo neón — "NO PASAR" inmediato */
    private void pintarMuroRojo(GraphicsContext gc) {
        double cx = x + ancho / 2.0;
        double cy = y + alto  / 2.0;
        double puls = 0.55 + Math.abs(Math.sin(fase)) * 0.25;

        // Glow exterior amplio
        RadialGradient glow = new RadialGradient(
            0, 0, cx, cy, Math.max(ancho, alto) * 0.85,
            false, CycleMethod.NO_CYCLE,
            new Stop(0,   Color.web("#FF0020", puls * 0.35)),
            new Stop(0.5, Color.web("#FF0020", puls * 0.12)),
            new Stop(1,   Color.web("#FF0020", 0.0))
        );
        gc.setFill(glow);
        gc.fillOval(cx - ancho * 0.85, cy - alto * 0.85,
                    ancho * 1.7, alto * 1.7);

        // Cuerpo sólido oscuro con tinte rojo
        gc.setFill(Color.web("#2A0008", 0.92));
        gc.fillRoundRect(x, y, ancho, alto, 6, 6);

        // Relleno rojo semi-transparente
        gc.setFill(Color.web("#FF0020", 0.18 + Math.abs(Math.sin(fase)) * 0.10));
        gc.fillRoundRect(x + 2, y + 2, ancho - 4, alto - 4, 4, 4);

        // Borde neón rojo — doble trazo (borde exterior brillante)
        gc.setStroke(Color.web("#FF2040", 0.9)); gc.setLineWidth(2.5);
        gc.strokeRoundRect(x, y, ancho, alto, 6, 6);
        gc.setStroke(Color.web("#FF6070", 0.5)); gc.setLineWidth(1.0);
        gc.strokeRoundRect(x + 2, y + 2, ancho - 4, alto - 4, 4, 4);

        // Símbolo de bloqueo: X grande y visible
        gc.setStroke(Color.web("#FF3050", 0.85)); gc.setLineWidth(3.0);
        double m = Math.min(ancho, alto) * 0.28;
        gc.strokeLine(cx - m, cy - m * 0.7, cx + m, cy + m * 0.7);
        gc.strokeLine(cx + m, cy - m * 0.7, cx - m, cy + m * 0.7);
        // Sombra de la X
        gc.setStroke(Color.web("#000000", 0.4)); gc.setLineWidth(1.2);
        gc.strokeLine(cx - m + 1, cy - m * 0.7 + 1, cx + m + 1, cy + m * 0.7 + 1);
    }

    /** Barrera cian — energía contenida */
    private void pintarBarreraCian(GraphicsContext gc) {
        double cx = x + ancho / 2.0;
        double cy = y + alto  / 2.0;
        double puls = 0.5 + Math.abs(Math.sin(fase * 1.3)) * 0.3;

        // Glow cian exterior
        RadialGradient glow = new RadialGradient(
            0, 0, cx, cy, Math.max(ancho, alto) * 0.9,
            false, CycleMethod.NO_CYCLE,
            new Stop(0,   Color.web("#00C8FF", puls * 0.30)),
            new Stop(0.55,Color.web("#00C8FF", puls * 0.08)),
            new Stop(1,   Color.web("#00C8FF", 0.0))
        );
        gc.setFill(glow);
        gc.fillOval(cx - ancho * 0.9, cy - alto * 0.9, ancho * 1.8, alto * 1.8);

        // Cuerpo
        gc.setFill(Color.web("#001828", 0.90));
        gc.fillRoundRect(x, y, ancho, alto, 5, 5);
        gc.setFill(Color.web("#00C8FF", 0.12 + Math.abs(Math.sin(fase * 1.3)) * 0.08));
        gc.fillRoundRect(x + 2, y + 2, ancho - 4, alto - 4, 3, 3);

        // Líneas de energía horizontales animadas
        gc.setStroke(Color.web("#00CFFF", 0.55)); gc.setLineWidth(1.0);
        int nLineas = Math.max(2, alto / 10);
        for (int i = 0; i < nLineas; i++) {
            double ly = y + 4 + i * (alto - 8.0) / Math.max(1, nLineas - 1);
            double offset = Math.sin(fase * 2 + i * 0.8) * 4;
            gc.strokeLine(x + 4, ly + offset, x + ancho - 4, ly - offset * 0.5);
        }

        // Borde cian doble
        gc.setStroke(Color.web("#00D8FF", 0.88)); gc.setLineWidth(2.2);
        gc.strokeRoundRect(x, y, ancho, alto, 5, 5);
        gc.setStroke(Color.web("#80F0FF", 0.40)); gc.setLineWidth(0.9);
        gc.strokeRoundRect(x + 2, y + 2, ancho - 4, alto - 4, 3, 3);
    }

    /** Zona advertencia naranja/amarillo — peligro obvio */
    private void pintarZonaAdvertencia(GraphicsContext gc) {
        double cx = x + ancho / 2.0;
        double cy = y + alto  / 2.0;
        double puls = 0.45 + Math.abs(Math.sin(fase * 0.9)) * 0.30;

        // Glow naranja
        RadialGradient glow = new RadialGradient(
            0, 0, cx, cy, Math.max(ancho, alto) * 0.9,
            false, CycleMethod.NO_CYCLE,
            new Stop(0,   Color.web("#FF8000", puls * 0.38)),
            new Stop(0.5, Color.web("#FF8000", puls * 0.10)),
            new Stop(1,   Color.web("#FF8000", 0.0))
        );
        gc.setFill(glow);
        gc.fillOval(cx - ancho * 0.9, cy - alto * 0.9, ancho * 1.8, alto * 1.8);

        // Cuerpo
        gc.setFill(Color.web("#1A0A00", 0.92));
        gc.fillRoundRect(x, y, ancho, alto, 5, 5);

        // Franjas diagonales SIN clip: rectángulos pequeños alternados dentro del área
        int fW = 12;
        int cols = (ancho / fW) + 2;
        for (int i = 0; i < cols; i++) {
            if (i % 2 == 0) {
                double fx = x + i * fW;
                double fw = Math.min(fW, (x + ancho) - fx);
                if (fw > 0) {
                    gc.setFill(Color.web("#FF8000", 0.22));
                    gc.fillRect(fx, y + 1, fw, alto - 2);
                }
            }
        }

        // Borde naranja doble
        gc.setStroke(Color.web("#FF9000", 0.88)); gc.setLineWidth(2.2);
        gc.strokeRoundRect(x, y, ancho, alto, 5, 5);
        gc.setStroke(Color.web("#FFD060", 0.40)); gc.setLineWidth(0.9);
        gc.strokeRoundRect(x + 2, y + 2, ancho - 4, alto - 4, 3, 3);

        // Triángulo de advertencia
        double ts = Math.min(ancho, alto) * 0.28;
        double[] tx = {cx, cx - ts, cx + ts};
        double[] ty = {cy - ts * 0.85, cy + ts * 0.5, cy + ts * 0.5};
        gc.setFill(Color.web("#FFB000", 0.7 + Math.abs(Math.sin(fase)) * 0.2));
        gc.fillPolygon(tx, ty, 3);
        gc.setStroke(Color.web("#FFD060", 0.5)); gc.setLineWidth(1.2);
        gc.strokePolygon(tx, ty, 3);
        // Signo ! dentro
        gc.setFill(Color.web("#1A0A00", 0.9));
        gc.fillRect(cx - 1.2, cy - ts * 0.42, 2.4, ts * 0.45);
        gc.fillOval(cx - 1.5, cy + ts * 0.06, 3, 3);
    }

    public void aplicarScroll(double d) {
        this.y += d;
        fase += 0.01; // continuar animación durante scroll
        if (this.y > App.ALTO_VENTANA + 60) this.activo = false;
    }

    public TipoObstaculo getTipo() { return tipo; }
}
