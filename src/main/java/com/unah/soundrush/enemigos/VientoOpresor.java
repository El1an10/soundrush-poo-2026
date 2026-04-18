package com.unah.soundrush.enemigos;

import com.unah.soundrush.clases.Enemigo;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * Enemigo "El Opresor" — espiral de tinta que se deshace en zigzag.
 * Personalidad: caótico, nervioso, imprevisible. Azul-gris espectral.
 * Diferente al Zen (sereno) y al Shhh! (verde tétrico).
 * Trazos de pincel agresivos y fragmentados.
 */
public class VientoOpresor extends Enemigo {

    private double tiempoZigzag;
    private double amplitudZigzag;
    private double anguloEspiral;

    // Etiqueta visual
    private static final Font F_LABEL = Font.font("Serif", FontWeight.BOLD, FontPosture.ITALIC, 8);

    public VientoOpresor(double x, double y) {
        super(x, y, 42, 42, 1.07, "viento", 1, 200);
        this.tiempoZigzag   = Math.random() * Math.PI * 2;
        this.amplitudZigzag = 50 + Math.random() * 30;
        this.anguloEspiral  = Math.random() * Math.PI * 2;
    }

    @Override public void mover() {}

    public void actualizarConJugador(double jugadorX, double jugadorY) {
        tiempoZigzag  += 0.07;
        anguloEspiral += 0.035; // más rápido que Zen

        double dx = jugadorX - this.x;
        double dy = jugadorY - this.y;
        double d  = Math.sqrt(dx * dx + dy * dy);

        if (d > 0) {
            double perpX  = -dy / d;
            double perpY  =  dx / d;
            double zigzag = Math.sin(tiempoZigzag) * amplitudZigzag / 60.0;
            this.velocidadX = (dx / d) * velocidad + perpX * zigzag;
            this.velocidadY = (dy / d) * velocidad + perpY * zigzag;
        }
        this.x += velocidadX;
        this.y += velocidadY;
    }

    @Override
    public void actualizar() { tiempoAnimacion += 0.016; }

    @Override
    public void pintar(GraphicsContext gc) {
        if (!activo) return;

        double cx = this.x + ancho / 2.0;
        double cy = this.y + alto  / 2.0;
        double t  = tiempoAnimacion;

        // ── Aura azul-gris espectral pulsante ────────────────────────────────
        double pAura = 0.07 + Math.abs(Math.sin(t * 2.2)) * 0.08;
        gc.setFill(Color.web("#2030A0", pAura * 0.6));
        gc.fillOval(cx - 30, cy - 30, 60, 60);

        gc.save();
        gc.translate(cx, cy);
        gc.rotate(Math.toDegrees(anguloEspiral));

        // ── Espiral doble fragmentada (trazo caótico vs enso sereno) ─────────
        // Arco exterior: grueso, azul oscuro
        gc.setStroke(Color.web("#050818", 0.92));
        gc.setLineWidth(10);
        gc.strokeArc(-19, -19, 38, 38, 40, 265, javafx.scene.shape.ArcType.OPEN);

        gc.setStroke(Color.web("#0A1030", 0.88));
        gc.setLineWidth(7);
        gc.strokeArc(-19, -19, 38, 38, 38, 270, javafx.scene.shape.ArcType.OPEN);

        // Brillo de tinta azul pizarra
        gc.setStroke(Color.web("#2840A0", 0.55));
        gc.setLineWidth(3.5);
        gc.strokeArc(-18, -18, 36, 36, 42, 260, javafx.scene.shape.ArcType.OPEN);

        // Hilo interno
        gc.setStroke(Color.web("#4868D0", 0.25));
        gc.setLineWidth(1.5);
        gc.strokeArc(-16, -16, 32, 32, 45, 250, javafx.scene.shape.ArcType.OPEN);

        // Arco interior más pequeño, descentrado (efecto espiral / caos)
        gc.setStroke(Color.web("#050818", 0.80));
        gc.setLineWidth(7);
        gc.strokeArc(-12, -14, 26, 28, 220, 230, javafx.scene.shape.ArcType.OPEN);

        gc.setStroke(Color.web("#1828A0", 0.50));
        gc.setLineWidth(3);
        gc.strokeArc(-11, -13, 24, 26, 222, 225, javafx.scene.shape.ArcType.OPEN);

        // Manchas de pincel: inicio/fin de ambos trazos
        gc.setFill(Color.web("#040810", 0.88));
        gc.fillOval(Math.cos(Math.toRadians(40))*19  - 6, Math.sin(Math.toRadians(40))*19  - 6, 12, 12);
        gc.fillOval(Math.cos(Math.toRadians(305))*19 - 4, Math.sin(Math.toRadians(305))*19 - 4, 8,  8);
        gc.fillOval(Math.cos(Math.toRadians(220))*13 - 4, Math.sin(Math.toRadians(220))*14 - 4, 8,  8);

        // Líneas de "velocidad" — diferencia clave del carácter caótico
        gc.setStroke(Color.web("#4060D0", 0.30));
        gc.setLineWidth(0.8);
        for (int i = 0; i < 4; i++) {
            double a = Math.toRadians(i * 90 + t * 180);
            gc.strokeLine(0, 0, Math.cos(a) * 22, Math.sin(a) * 22);
        }

        gc.restore();

        // Etiqueta pequeña
        gc.setFont(F_LABEL);
        gc.setFill(Color.web("#000000", 0.45));
        gc.fillText("Opresor", cx - 13 + 1, cy + 5 + 1);
        gc.setFill(Color.web("#5878D0", 0.55));
        gc.fillText("Opresor", cx - 13, cy + 5);

        // Barra de vida
        double p = (double) vida / vidaMaxima;
        gc.setFill(Color.web("#08081A"));
        gc.fillRoundRect(this.x, this.y - 11, ancho, 5, 3, 3);
        gc.setFill(Color.web("#2848C0"));
        gc.fillRoundRect(this.x, this.y - 11, ancho * p, 5, 3, 3);
    }
}
