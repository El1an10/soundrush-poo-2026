package com.unah.soundrush.enemigos;

import com.unah.soundrush.clases.Enemigo;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * Enemigo "El Censor" — sello de tinta que borra la música.
 * Personalidad: autoritario, amenazante, morado oscuro.
 * Trazos de pincel circulares pesados + cruz tachada gestual + dispara.
 * Completamente distinto: oscuro-morado vs azul (Opresor) vs verde (Shhh) vs gris (Zen).
 */
public class Censor extends Enemigo {

    private double tiempoDisparo;
    private static final double COOLDOWN = 3.2;
    private double anguloSello;

    private static final Font F_LABEL = Font.font("Serif", FontWeight.BOLD, FontPosture.ITALIC, 8);

    public Censor(double x, double y) {
        super(x, y, 46, 46, 0.87, "censor", 1, 350);
        this.tiempoDisparo = Math.random() * COOLDOWN;
        this.anguloSello   = Math.random() * 0.3 - 0.15;
    }

    @Override public void mover() {}

    public void actualizarConJugador(double jugadorX, double jugadorY, double dt) {
        tiempoDisparo += dt;
        tiempoAnimacion += dt;
        moverHaciaJugador(jugadorX + 18, jugadorY + 18);
    }

    @Override public void actualizar() {}

    public boolean puedeDisparar() { return tiempoDisparo >= COOLDOWN; }

    public ProyectilEnemigo dispararHacia(double jugadorX, double jugadorY) {
        tiempoDisparo = 0;
        return new ProyectilEnemigo(this.x + ancho/2.0, this.y + alto, jugadorX, jugadorY);
    }

    @Override
    public void pintar(GraphicsContext gc) {
        if (!activo) return;

        double cx = this.x + ancho / 2.0;
        double cy = this.y + alto  / 2.0;
        double t  = tiempoAnimacion;

        // ── Aura morada amenazante ────────────────────────────────────────────
        double pAura = 0.08 + Math.abs(Math.sin(t * 1.3)) * 0.10;
        gc.setFill(Color.web("#300050", pAura * 0.7));
        gc.fillOval(cx - 32, cy - 32, 64, 64);

        gc.save();
        gc.translate(cx, cy);
        gc.rotate(Math.toDegrees(anguloSello));

        // ── Sello circular: anillo completo y cerrado (vs enso abierto) ───────
        // Esto lo diferencia del Zen — el Censor tiene el círculo CERRADO y pesado
        // Capa 1: exterior muy oscuro
        gc.setStroke(Color.web("#0A0012", 0.94));
        gc.setLineWidth(12);
        gc.strokeOval(-21, -21, 42, 42);

        // Capa 2: cuerpo morado oscuro
        gc.setStroke(Color.web("#1A0028", 0.92));
        gc.setLineWidth(9);
        gc.strokeOval(-21, -21, 42, 42);

        // Capa 3: brillo morado interior
        gc.setStroke(Color.web("#5010A0", 0.55));
        gc.setLineWidth(4.5);
        gc.strokeOval(-20, -20, 40, 40);

        // Capa 4: hilo de luz
        gc.setStroke(Color.web("#8040D0", 0.22));
        gc.setLineWidth(2);
        gc.strokeOval(-18, -18, 36, 36);

        // ── Relleno interior oscuro (sello opaco) ─────────────────────────────
        gc.setFill(Color.web("#0D0018", 0.82));
        gc.fillOval(-15, -15, 30, 30);

        // ── Cruz de censura: trazo gestual de pincel, no geométrico ───────────
        // Trazo 1: diagonal principal (gruesa, con presión variable)
        gc.setStroke(Color.web("#6010A0", 0.85));
        gc.setLineWidth(4.5);
        gc.strokeLine(-10, -8, 11, 9);
        gc.setStroke(Color.web("#8830C0", 0.55));
        gc.setLineWidth(2.5);
        gc.strokeLine(-9,  -7, 10, 8);

        // Trazo 2: diagonal inversa
        gc.setStroke(Color.web("#6010A0", 0.85));
        gc.setLineWidth(4.5);
        gc.strokeLine(11, -8, -10, 9);
        gc.setStroke(Color.web("#8830C0", 0.55));
        gc.setLineWidth(2.5);
        gc.strokeLine(10, -7, -9, 8);

        // Manchas en los 4 extremos de la cruz (terminaciones de pincel)
        gc.setFill(Color.web("#0A0014", 0.88));
        gc.fillOval(-13, -11, 7, 7);
        gc.fillOval(7, -11, 7, 7);
        gc.fillOval(-13, 5, 7, 7);
        gc.fillOval(7, 5, 7, 7);

        // Manchas de textura del sello (cera / tinta)
        gc.setFill(Color.web("#1A0030", 0.60));
        for (int i = 0; i < 5; i++) {
            double ang = i * Math.PI * 2 / 5;
            gc.fillOval(Math.cos(ang)*19 - 3.5, Math.sin(ang)*19 - 3.5, 7, 7);
        }

        gc.restore();

        // ── Indicador de carga de disparo ─────────────────────────────────────
        if (tiempoDisparo > COOLDOWN * 0.60) {
            double intens = (tiempoDisparo - COOLDOWN * 0.60) / (COOLDOWN * 0.40);
            gc.setFill(Color.web("#7000D0", intens * 0.40));
            gc.fillOval(cx - 28, cy - 28, 56, 56);
            // Rayos de aviso
            gc.setStroke(Color.web("#A030E0", intens * 0.55));
            gc.setLineWidth(1.2);
            for (int i = 0; i < 6; i++) {
                double ang = t * 4 + i * Math.PI / 3;
                gc.strokeLine(cx, cy,
                    cx + Math.cos(ang) * (22 + intens * 10),
                    cy + Math.sin(ang) * (22 + intens * 10));
            }
        }

        // Etiqueta
        gc.setFont(F_LABEL);
        gc.setFill(Color.web("#000000", 0.45));
        gc.fillText("Censor", cx - 12 + 1, cy + 5 + 1);
        gc.setFill(Color.web("#9050D0", 0.60));
        gc.fillText("Censor", cx - 12, cy + 5);

        // Barra de vida
        double p = (double) vida / vidaMaxima;
        gc.setFill(Color.web("#0A0016"));
        gc.fillRoundRect(this.x, this.y - 11, ancho, 5, 3, 3);
        gc.setFill(Color.web("#7020B0"));
        gc.fillRoundRect(this.x, this.y - 11, ancho * p, 5, 3, 3);
    }

    // ── Proyectil: mancha de tinta morada ────────────────────────────────────
    public static class ProyectilEnemigo {
        public double x, y, vx, vy;
        public boolean activo = true;
        private static final double SPEED = 2.8;
        private double t = 0;

        public ProyectilEnemigo(double ox, double oy, double tx, double ty) {
            this.x = ox; this.y = oy;
            double dx = tx-ox, dy = ty-oy;
            double dist = Math.sqrt(dx*dx + dy*dy);
            if (dist > 0) { vx = dx/dist*SPEED; vy = dy/dist*SPEED; }
        }

        public void actualizar() {
            x += vx; y += vy; t += 0.1;
            if (y>660||y<-30||x<-30||x>1054) activo = false;
        }

        public void aplicarScroll(double d) { y += d; }

        public void pintar(GraphicsContext gc) {
            if (!activo) return;
            gc.setFill(Color.web("#12001E", 0.88));
            gc.fillOval(x-7, y-7, 14, 14);
            gc.setFill(Color.web("#6010A0", 0.55));
            gc.fillOval(x-5, y-5, 10, 10);
            for (int i = 0; i < 4; i++) {
                double ang = t + i * Math.PI / 2;
                gc.setFill(Color.web("#8020C0", 0.30));
                gc.fillOval(x + Math.cos(ang)*6 - 3, y + Math.sin(ang)*6 - 3, 6, 6);
            }
            gc.setStroke(Color.web("#9030C0", 0.40));
            gc.setLineWidth(1);
            gc.strokeOval(x-8, y-8, 16, 16);
        }

        public Rectangle obtenerRectangulo() { return new Rectangle(x-7, y-7, 14, 14); }
    }
}
