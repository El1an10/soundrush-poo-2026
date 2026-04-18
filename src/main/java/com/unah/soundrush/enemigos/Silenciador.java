package com.unah.soundrush.enemigos;

import com.unah.soundrush.clases.Enemigo;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Enemigo "Zen" — círculo enso auténtico inspirado en caligrafía zen.
 * Estética: trazo de pincel grueso, irregular, con abertura, múltiples capas.
 * Personalidad: vacío absoluto, frío, implacable. Tétrico pero sereno.
 * Se distingue del Shhh! en forma, color y comportamiento visual.
 */
public class Silenciador extends Enemigo {

    private double anguloPincel;
    private double velocidadGiro;

    // Fuente para la etiqueta "Zen"
    private static final Font F_ZEN = Font.font("Serif", FontWeight.BOLD, 9);

    public Silenciador(double x, double y) {
        super(x, y, 48, 48, 0.53, "silenciador", 2, 100);
        this.anguloPincel  = Math.random() * Math.PI * 2;
        this.velocidadGiro = 0.006 + Math.random() * 0.006; // cada uno gira diferente
    }

    @Override public void mover() {}

    public void actualizarConJugador(double jugadorX, double jugadorY) {
        moverHaciaJugador(jugadorX + 18, jugadorY + 18);
    }

    @Override
    public void actualizar() {
        tiempoAnimacion += 0.016;
        anguloPincel    += velocidadGiro;
    }

    @Override
    public void pintar(GraphicsContext gc) {
        if (!activo) return;

        double cx = this.x + ancho / 2.0;
        double cy = this.y + alto  / 2.0;
        double t  = tiempoAnimacion;

        // ── Aura: vacío gris azulado muy tenue que respira ────────────────────
        double pAura = 0.05 + Math.abs(Math.sin(t * 0.9)) * 0.07;
        gc.setFill(Color.web("#404858", pAura));
        gc.fillOval(cx - 32, cy - 32, 64, 64);

        // ── Enso: círculo de pincel zen auténtico ─────────────────────────────
        gc.save();
        gc.translate(cx, cy);
        gc.rotate(Math.toDegrees(anguloPincel));

        double r = 20.0;

        // La apertura del enso: ~60° sin pintura (parte superior-derecha)
        // Capa 1 — trazo más exterior y oscuro, muy grueso (la "sombra" del pincel)
        gc.setStroke(Color.web("#050508", 0.92));
        gc.setLineWidth(13);
        gc.strokeArc(-r - 1, -r - 1, (r+1)*2, (r+1)*2,
                     70, 250, javafx.scene.shape.ArcType.OPEN);

        // Capa 2 — cuerpo del trazo: gris oscuro con variación de grosor
        // Empieza grueso y se adelgaza en los extremos
        gc.setStroke(Color.web("#1A1A22", 0.95));
        gc.setLineWidth(10);
        gc.strokeArc(-r, -r, r*2, r*2,
                     68, 255, javafx.scene.shape.ArcType.OPEN);

        // Capa 3 — interior brillante (tinta mojada reciente)
        gc.setStroke(Color.web("#3A3A50", 0.70));
        gc.setLineWidth(5);
        gc.strokeArc(-r + 1.5, -r + 1.5, r*2 - 3, r*2 - 3,
                     72, 245, javafx.scene.shape.ArcType.OPEN);

        // Capa 4 — hilo de luz en el centro del trazo
        gc.setStroke(Color.web("#6068A0", 0.28));
        gc.setLineWidth(2);
        gc.strokeArc(-r + 3, -r + 3, r*2 - 6, r*2 - 6,
                     74, 238, javafx.scene.shape.ArcType.OPEN);

        // ── Extremos del trazo: manchas de pincel (inicio y fin pronunciados) ─
        // Inicio del trazo: mancha grande y redonda
        double angIni = Math.toRadians(68);
        double angFin = Math.toRadians(68 + 255);
        gc.setFill(Color.web("#080810", 0.90));
        gc.fillOval(Math.cos(angIni)*r - 8, Math.sin(angIni)*r - 8, 16, 16);
        // Fin del trazo: mancha más pequeña, trazo que se adelgaza
        gc.fillOval(Math.cos(angFin)*r - 5, Math.sin(angFin)*r - 5, 10, 10);
        gc.setFill(Color.web("#2A2A3A", 0.6));
        gc.fillOval(Math.cos(angFin)*r - 3, Math.sin(angFin)*r - 8, 6, 6);

        // ── Raspado de pincel: líneas interiores de textura ───────────────────
        gc.setStroke(Color.web("#2A2A38", 0.35));
        gc.setLineWidth(0.8);
        for (int i = 0; i < 5; i++) {
            double a1 = Math.toRadians(90 + i * 40);
            double a2 = Math.toRadians(95 + i * 40);
            gc.strokeLine(
                Math.cos(a1)*(r-7), Math.sin(a1)*(r-7),
                Math.cos(a2)*(r+1), Math.sin(a2)*(r+1)
            );
        }

        gc.restore();

        // ── Etiqueta "Zen" en el centro ────────────────────────────────────────
        // Pequeña, elegante, casi oculta — como un sello de caligrafía
        gc.setFont(F_ZEN);
        // Sombra
        gc.setFill(Color.web("#000000", 0.5));
        gc.fillText("Zen", cx - 8 + 1, cy + 4 + 1);
        // Texto
        gc.setFill(Color.web("#7080B0", 0.65));
        gc.fillText("Zen", cx - 8, cy + 4);

        // ── Barra de vida ─────────────────────────────────────────────────────
        double p = (double) vida / vidaMaxima;
        gc.setFill(Color.web("#0A0A14"));
        gc.fillRoundRect(this.x, this.y - 11, ancho, 5, 3, 3);
        gc.setFill(Color.web("#5060A0"));
        gc.fillRoundRect(this.x, this.y - 11, ancho * p, 5, 3, 3);
    }
}
