package com.unah.soundrush.clases;

import com.unah.soundrush.App;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

/**
 * Jugador principal: La Nota Musical.
 * Hereda de ElementoMovil.
 */
public class Jugador extends ElementoMovil {

    private int vidas;
    private boolean escudoActivo;
    private double tiempoEscudo;
    private double tiempoInvulnerable;
    private boolean invulnerable;

    private double tiempoAnimacion;
    private double tiempoDisparo;
    private static final double COOLDOWN_DISPARO = 0.25;

    // Boosts
    private boolean boostVelocidadActivo;
    private double  tiempoBoostVelocidad;
    private boolean boostDisparoActivo;
    private double  tiempoBoostDisparo;

    private static final double VEL_BASE   = 3.2;
    private static final double VEL_BOOST  = 6.5;

    // Límites de movimiento
    private static final double LIMITE_SUPERIOR = 50;
    private static final double LIMITE_INFERIOR = App.ALTO_VENTANA - 50;
    private static final double LIMITE_IZQ      = 70;
    private static final double LIMITE_DER      = App.ANCHO_VENTANA - 70;

    public Jugador(double x, double y) {
        super(x, y, 36, 36, VEL_BASE, "jugador");
        this.vidas = 3;
    }

    @Override
    public void mover() {
        double vel = boostVelocidadActivo ? VEL_BOOST : VEL_BASE;
        if (App.teclaArriba    && this.y > LIMITE_SUPERIOR) this.y -= vel;
        if (App.teclaAbajo     && this.y < LIMITE_INFERIOR) this.y += vel;
        if (App.teclaIzquierda && this.x > LIMITE_IZQ)      this.x -= vel;
        if (App.teclaDerecha   && this.x < LIMITE_DER)       this.x += vel;
    }

    @Override
    public void actualizar() { mover(); }

    public void actualizarTiempo(double dt) {
        tiempoAnimacion += dt;
        tiempoDisparo   += dt;

        if (invulnerable) {
            tiempoInvulnerable -= dt;
            if (tiempoInvulnerable <= 0) invulnerable = false;
        }
        if (escudoActivo) {
            tiempoEscudo -= dt;
            if (tiempoEscudo <= 0) escudoActivo = false;
        }
        if (boostVelocidadActivo) {
            tiempoBoostVelocidad -= dt;
            if (tiempoBoostVelocidad <= 0) boostVelocidadActivo = false;
        }
        if (boostDisparoActivo) {
            tiempoBoostDisparo -= dt;
            if (tiempoBoostDisparo <= 0) boostDisparoActivo = false;
        }
    }

    public boolean puedisparar() { return tiempoDisparo >= COOLDOWN_DISPARO; }

    public Proyectil disparar() {
        tiempoDisparo = 0;
        return new Proyectil(this.x + this.ancho / 2.0 - 4, this.y - 10,
                             boostDisparoActivo);
    }

    @Override
    public void pintar(GraphicsContext gc) {
        if (invulnerable && (int)(tiempoAnimacion * 10) % 2 == 0) return;

        double cx  = this.x + ancho / 2.0;
        double cy  = this.y + alto  / 2.0;
        double t   = tiempoAnimacion;

        // Color aura: cambia con boost pero la NOTA siempre amarilla
        String hexAura = boostDisparoActivo   ? "#FF4080"
                       : boostVelocidadActivo  ? "#00CFFF"
                       : "#FFD700";

        // ── Escudo ────────────────────────────────────────────────────────────
        if (escudoActivo) {
            double pe = 1.0 + Math.sin(t * 4) * 0.08;
            gc.setStroke(Color.web("#9B59B6", 0.7));  gc.setLineWidth(3);
            gc.strokeOval(cx - 26*pe, cy - 26*pe, 52*pe, 52*pe);
            gc.setStroke(Color.web("#D2B4DE", 0.4)); gc.setLineWidth(1.5);
            gc.strokeOval(cx - 30*pe, cy - 30*pe, 60*pe, 60*pe);
        }

        // ── Aura dorada pulsante ──────────────────────────────────────────────
        double pulso = 1.0 + Math.sin(t * 4) * 0.15;
        RadialGradient aura = new RadialGradient(
            0, 0, cx, cy, 22 * pulso, false, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web(hexAura, 0.5)),
            new Stop(1, Color.web(hexAura, 0.0)));
        gc.setFill(aura);
        gc.fillOval(cx - 22*pulso, cy - 22*pulso, 44*pulso, 44*pulso);

        // ── Nota musical amarilla brillante (original) ────────────────────────
        // Cuerpo (óvalo)
        gc.setFill(Color.web("#FFD700"));
        gc.fillOval(this.x + 4, this.y + 12, 22, 18);
        // Sombra interior
        gc.setFill(Color.web("#B8860B"));
        gc.fillOval(this.x + 7, this.y + 15, 16, 12);

        // Palillo vertical
        gc.setStroke(Color.web("#FFD700")); gc.setLineWidth(3);
        gc.strokeLine(this.x + 26, this.y + 12, this.x + 26, this.y - 4);

        // Bandera
        gc.setStroke(Color.web("#FFA500")); gc.setLineWidth(2);
        gc.strokeLine(this.x + 26, this.y - 4, this.x + 34, this.y + 2);
        gc.strokeLine(this.x + 26, this.y + 2, this.x + 34, this.y + 8);

        // ── Destellos de boost ────────────────────────────────────────────────
        if (boostVelocidadActivo) {
            gc.setStroke(Color.web("#00CFFF", 0.6)); gc.setLineWidth(1.2);
            for (int i = 0; i < 4; i++) {
                double ang = t * 5 + i * Math.PI / 2;
                gc.strokeLine(cx, cy, cx + Math.cos(ang)*28, cy + Math.sin(ang)*28);
            }
        }
        if (boostDisparoActivo) {
            gc.setFill(Color.web("#FF2060", 0.25 + Math.abs(Math.sin(t*8))*0.2));
            gc.fillOval(cx - 18, cy - 18, 36, 36);
        }
    }

    @Override
    public Rectangle obtenerRectangulo() {
        return new Rectangle(this.x + 6, this.y + 8, this.ancho - 12, this.alto - 10);
    }

    public void recibirDanio() {
        if (invulnerable || escudoActivo) {
            if (escudoActivo) escudoActivo = false;
            return;
        }
        vidas--;
        invulnerable = true;
        tiempoInvulnerable = 2.5;
    }

    public void activarEscudo()        { escudoActivo = true; tiempoEscudo = 5.0; }
    public void activarBoostVelocidad(){ boostVelocidadActivo = true; tiempoBoostVelocidad = 5.0; }
    public void activarBoostDisparo()  { boostDisparoActivo   = true; tiempoBoostDisparo   = 15.0; }

    public int     getVidas()              { return vidas; }
    public void    setVidas(int v)         { this.vidas = v; }
    public boolean isEscudoActivo()        { return escudoActivo; }
    public boolean isInvulnerable()        { return invulnerable; }
    public boolean isBoostVelocidadActivo(){ return boostVelocidadActivo; }
    public boolean isBoostDisparoActivo()  { return boostDisparoActivo; }
    public double  getTimerAnimacion()     { return tiempoAnimacion; }
    public double  getTiempoBoostVelocidad(){ return tiempoBoostVelocidad; }
    public double  getTiempoBoostDisparo() { return tiempoBoostDisparo; }
}
