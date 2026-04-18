package com.unah.soundrush.enemigos;

import com.unah.soundrush.clases.Enemigo;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * Enemigo: La Burbuja del Shhh!
 * Esfera tétrica con lettering macabro "Shhh!" dibujado a mano.
 * Estética: círculo irregular con trazos de pincel (ref imagen 1 — estilo dibujo).
 * Al morir: fragmentos vuelan + letras caen.
 */
public class BurbujaShhh extends Enemigo {

    public enum EstadoBurbuja { VIVA, ROMPIENDO, MUERTA }
    private EstadoBurbuja estadoBurbuja = EstadoBurbuja.VIVA;

    private double[] fragX, fragY, fragVX, fragVY, fragAlpha, fragTam, fragAng;
    private double[] letraX, letraY, letraVX, letraVY, letraAlpha;
    private static final int    NUM_FRAGS        = 20;
    private static final int    NUM_LETRAS        = 5; // S h h h !
    private double tiempoRuptura                 = 0;
    private static final double DURACION_RUPTURA = 1.5;

    // Tipografía artística — serif itálica para efecto lettering
    private static final Font F_S    = Font.font("Serif", FontWeight.BOLD, FontPosture.ITALIC, 20);
    private static final Font F_HHH  = Font.font("Serif", FontWeight.BOLD, FontPosture.ITALIC, 15);
    private static final Font F_EXCL = Font.font("Serif", FontWeight.BOLD, FontPosture.ITALIC, 22);
    private static final Font F_FRAG = Font.font("Serif", FontWeight.BOLD, FontPosture.ITALIC, 13);

    // Irregularidades del círculo (simulan trazo a mano)
    private final double[] irregR;
    private final double[] irregAng;

    public BurbujaShhh(double x, double y) {
        super(x, y, 54, 54, 0.47, "burbuja", 1, 250);
        // Pre-calcular deformaciones del círculo para efecto dibujado a mano
        irregR   = new double[24];
        irregAng = new double[24];
        for (int i = 0; i < 24; i++) {
            irregR[i]   = 20 + Math.random() * 5 - 2.5;
            irregAng[i] = Math.random() * 0.4 - 0.2;
        }
    }

    @Override public void mover() {}

    public void actualizarConJugador(double jugadorX, double jugadorY) {
        if (estadoBurbuja == EstadoBurbuja.VIVA)
            moverHaciaJugador(jugadorX + 18, jugadorY + 18);
    }

    @Override public void actualizar() { tiempoAnimacion += 0.016; }

    public void actualizarConDelta(double dt) {
        tiempoAnimacion += dt;
        if (estadoBurbuja == EstadoBurbuja.ROMPIENDO) {
            tiempoRuptura += dt;
            actualizarFragmentos(dt);
            if (tiempoRuptura >= DURACION_RUPTURA) {
                estadoBurbuja = EstadoBurbuja.MUERTA;
                this.activo   = false;
            }
        }
    }

    public void romper() {
        if (estadoBurbuja != EstadoBurbuja.VIVA) return;
        estadoBurbuja = EstadoBurbuja.ROMPIENDO;
        tiempoRuptura = 0;

        double cx = this.x + ancho / 2.0;
        double cy = this.y + alto  / 2.0;

        fragX = new double[NUM_FRAGS]; fragY = new double[NUM_FRAGS];
        fragVX = new double[NUM_FRAGS]; fragVY = new double[NUM_FRAGS];
        fragAlpha = new double[NUM_FRAGS]; fragTam = new double[NUM_FRAGS];
        fragAng = new double[NUM_FRAGS];
        for (int i = 0; i < NUM_FRAGS; i++) {
            double ang = i * (Math.PI * 2.0 / NUM_FRAGS) + Math.random() * 0.5;
            double vel = 1.5 + Math.random() * 4.0;
            fragX[i] = cx + Math.cos(ang) * 24;
            fragY[i] = cy + Math.sin(ang) * 24;
            fragVX[i] = Math.cos(ang) * vel;
            fragVY[i] = Math.sin(ang) * vel - 1.0;
            fragAlpha[i] = 0.9;
            fragTam[i] = 4 + Math.random() * 10;
            fragAng[i] = Math.random() * Math.PI * 2;
        }

        // S, h, h, h, !
        letraX = new double[NUM_LETRAS]; letraY = new double[NUM_LETRAS];
        letraVX = new double[NUM_LETRAS]; letraVY = new double[NUM_LETRAS];
        letraAlpha = new double[NUM_LETRAS];
        char[] chars = {'S', 'h', 'h', 'h', '!'};
        for (int i = 0; i < NUM_LETRAS; i++) {
            letraX[i]     = cx - 22 + i * 12;
            letraY[i]     = cy + 4;
            letraVX[i]    = (Math.random() - 0.5) * 4.0;
            letraVY[i]    = 0.5 + Math.random() * 2.5;
            letraAlpha[i] = 1.0;
        }
    }

    private void actualizarFragmentos(double dt) {
        for (int i = 0; i < NUM_FRAGS; i++) {
            fragX[i] += fragVX[i]; fragY[i] += fragVY[i];
            fragVY[i] += 0.12; fragAng[i] += 0.15;
            fragAlpha[i] -= dt / DURACION_RUPTURA * 1.1;
            fragAlpha[i]  = Math.max(0, fragAlpha[i]);
        }
        for (int i = 0; i < NUM_LETRAS; i++) {
            letraX[i] += letraVX[i]; letraY[i] += letraVY[i];
            letraVY[i] += 0.18;
            letraAlpha[i] -= dt / DURACION_RUPTURA * 1.25;
            letraAlpha[i]  = Math.max(0, letraAlpha[i]);
        }
    }

    @Override
    public void pintar(GraphicsContext gc) {
        if (!activo && estadoBurbuja == EstadoBurbuja.MUERTA) return;
        if (estadoBurbuja == EstadoBurbuja.VIVA)           pintarBurbuja(gc);
        else if (estadoBurbuja == EstadoBurbuja.ROMPIENDO) pintarRuptura(gc);
    }

    private void pintarBurbuja(GraphicsContext gc) {
        double cx  = this.x + ancho / 2.0;
        double cy  = this.y + alto  / 2.0;
        double t   = tiempoAnimacion;
        double bob = Math.sin(t * 1.7) * 3.5;

        // ── Aura tétrica: halo oscuro-verdoso que pulsa ───────────────────────
        double auraR = 30 + Math.abs(Math.sin(t * 1.2)) * 6;
        RadialGradient aura = new RadialGradient(
            0, 0, cx, cy + bob, auraR, false, CycleMethod.NO_CYCLE,
            new Stop(0,   Color.web("#204820", 0.22)),
            new Stop(0.5, Color.web("#102810", 0.10)),
            new Stop(1,   Color.web("#000000", 0.0))
        );
        gc.setFill(aura); gc.fillOval(cx - auraR, cy - auraR + bob, auraR*2, auraR*2);

        // ── Círculo dibujado a mano (trazos irregulares tipo enso tétrico) ────
        gc.save();
        gc.translate(cx, cy + bob);

        double baseR = 22.0;

        // Capa 1: trazo exterior grueso y rugoso (múltiples segmentos irregulares)
        int segs = 36;
        gc.setStroke(Color.web("#0A1A0A", 0.88));
        gc.setLineWidth(7.5);
        double[] px = new double[segs + 1];
        double[] py = new double[segs + 1];
        for (int i = 0; i <= segs; i++) {
            double ang = (i / (double) segs) * Math.PI * 2;
            int idx = i % 24;
            double r = baseR + irregR[idx] - 20 + Math.sin(t * 0.8 + ang * 3) * 1.5;
            px[i] = Math.cos(ang + irregAng[idx]) * r;
            py[i] = Math.sin(ang + irregAng[idx]) * r;
        }
        // Dibujar como polilínea cerrada
        for (int i = 0; i < segs; i++) {
            gc.strokeLine(px[i], py[i], px[i+1], py[i+1]);
        }

        // Capa 2: trazo interior más delgado, tono verdoso oscuro
        gc.setStroke(Color.web("#1A3A1A", 0.80));
        gc.setLineWidth(3.5);
        for (int i = 0; i < segs; i++) {
            double factor = 0.82;
            gc.strokeLine(px[i]*factor, py[i]*factor, px[i+1]*factor, py[i+1]*factor);
        }

        // Capa 3: línea de brillo interior muy tenue (húmedo)
        gc.setStroke(Color.web("#50A050", 0.22));
        gc.setLineWidth(1.5);
        for (int i = 0; i < segs; i++) {
            double f = 0.70;
            gc.strokeLine(px[i]*f, py[i]*f, px[i+1]*f, py[i+1]*f);
        }

        // Manchas de tinta en puntos aleatorios del trazo (textura pincel)
        gc.setFill(Color.web("#0A180A", 0.75));
        for (int i = 0; i < 6; i++) {
            double ang = i * Math.PI / 3 + t * 0.1;
            double r   = baseR + 1;
            gc.fillOval(Math.cos(ang)*r - 4, Math.sin(ang)*r - 4, 8, 8);
        }

        gc.restore();

        // ── Lettering "Shhh!" tétrico ─────────────────────────────────────────
        // Posicionamos las letras dentro del círculo con ligero desplazamiento
        double pulsoT = 0.70 + Math.sin(t * 2.5) * 0.18;

        // Sombra del texto (profundidad + sensación dibujada)
        gc.setFont(F_S);
        gc.setFill(Color.web("#001800", 0.6));
        gc.fillText("S", cx - 17 + 2, cy - 2 + bob + 2);
        gc.setFont(F_HHH);
        gc.setFill(Color.web("#001800", 0.5));
        gc.fillText("hhh", cx - 3 + 2, cy + 1 + bob + 2);
        gc.setFont(F_EXCL);
        gc.setFill(Color.web("#003800", 0.6));
        gc.fillText("!", cx + 20 + 2, cy - 3 + bob + 2);

        // "S" grande — verde fantasmal pálido
        gc.setFont(F_S);
        gc.setFill(Color.web("#90E890", pulsoT * 0.88));
        gc.fillText("S", cx - 17, cy - 2 + bob);

        // "hhh" — verde más claro, ligeramente más transparente
        gc.setFont(F_HHH);
        gc.setFill(Color.web("#A8F0A8", pulsoT * 0.78));
        gc.fillText("hhh", cx - 3, cy + 1 + bob);

        // "!" — brillante, amarillo-verde como advertencia
        gc.setFont(F_EXCL);
        gc.setFill(Color.web("#C8FF60", pulsoT * 0.95 + Math.abs(Math.sin(t*4)) * 0.05));
        gc.fillText("!", cx + 20, cy - 3 + bob);

        // Puntos suspensivos debajo — vibran ligeramente
        gc.setFill(Color.web("#90E890", pulsoT * 0.40));
        for (int i = 0; i < 3; i++) {
            double dot = Math.abs(Math.sin(t * 3 + i * 1.2)) * 1.5;
            gc.fillOval(cx - 7 + i * 7, cy + 9 + bob - dot, 3, 3);
        }

        pintarBarraVida(gc, cx, cy + bob);
    }

    private void pintarBarraVida(GraphicsContext gc, double cx, double cyBob) {
        double p = (double) vida / vidaMaxima;
        gc.setFill(Color.web("#0A150A"));
        gc.fillRoundRect(cx - ancho/2.0, cyBob - alto/2.0 - 12, ancho, 5, 3, 3);
        gc.setFill(Color.web("#40A040"));
        gc.fillRoundRect(cx - ancho/2.0, cyBob - alto/2.0 - 12, ancho * p, 5, 3, 3);
    }

    private void pintarRuptura(GraphicsContext gc) {
        // Fragmentos del círculo — arcos con textura
        for (int i = 0; i < NUM_FRAGS; i++) {
            if (fragAlpha[i] <= 0) continue;
            gc.save();
            gc.translate(fragX[i], fragY[i]);
            gc.rotate(Math.toDegrees(fragAng[i]));
            // Trazo de fragmento
            gc.setStroke(Color.web("#40A040", fragAlpha[i] * 0.85));
            gc.setLineWidth(fragTam[i] * 0.35);
            gc.strokeArc(-fragTam[i]/2, -fragTam[i]/2,
                         fragTam[i], fragTam[i], 15, 270,
                         javafx.scene.shape.ArcType.OPEN);
            // Mancha de tinta
            gc.setFill(Color.web("#C8FF60", fragAlpha[i] * 0.5));
            gc.fillOval(-2, -2, 4, 4);
            gc.restore();
        }

        // Letras cayendo — S h h h !
        char[] chars = {'S', 'h', 'h', 'h', '!'};
        for (int i = 0; i < NUM_LETRAS; i++) {
            if (letraAlpha[i] <= 0) continue;
            Font f = (i == 0) ? F_S : (i == 4 ? F_EXCL : F_FRAG);
            gc.setFont(f);
            gc.setFill(Color.web("#000000", letraAlpha[i] * 0.4));
            gc.fillText(String.valueOf(chars[i]), letraX[i]+1.5, letraY[i]+1.5);
            gc.setFill(Color.web(i == 4 ? "#C8FF60" : "#90E890", letraAlpha[i]));
            gc.fillText(String.valueOf(chars[i]), letraX[i], letraY[i]);
        }
    }

    public void aplicarScroll(double d) {
        this.y += d;
        if (estadoBurbuja == EstadoBurbuja.ROMPIENDO) {
            for (int i = 0; i < NUM_FRAGS;  i++) fragY[i]  += d;
            for (int i = 0; i < NUM_LETRAS; i++) letraY[i] += d;
        }
        if (estadoBurbuja == EstadoBurbuja.VIVA && this.y > 700) this.activo = false;
    }

    @Override
    public void recibirDanio(int danio) {
        this.vida -= danio;
        if (this.vida <= 0) { this.vida = 0; romper(); }
    }

    public boolean estaRompiendo()   { return estadoBurbuja == EstadoBurbuja.ROMPIENDO; }
    public EstadoBurbuja getEstadoBurbuja() { return estadoBurbuja; }
}
