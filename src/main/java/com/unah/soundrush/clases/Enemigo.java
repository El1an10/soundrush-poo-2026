package com.unah.soundrush.clases;

import javafx.scene.canvas.GraphicsContext;

/**
 * Clase abstracta base para todos los enemigos.
 * Hereda de ElementoMovil.
 * Todos los enemigos deben moverse hacia el jugador.
 */
public abstract class Enemigo extends ElementoMovil {

    protected int vida;
    protected int vidaMaxima;
    protected int puntosAlMorir;
    protected double tiempoAnimacion;

    public Enemigo(double x, double y, int ancho, int alto, double velocidad,
                   String indiceImagen, int vida, int puntosAlMorir) {
        super(x, y, ancho, alto, velocidad, indiceImagen);
        this.vida = vida;
        this.vidaMaxima = vida;
        this.puntosAlMorir = puntosAlMorir;
    }

    /**
     * Todos los enemigos deben moverse hacia el jugador.
     * Sobrescritura obligatoria en cada subclase.
     */
    @Override
    public abstract void mover();

    @Override
    public abstract void pintar(GraphicsContext gc);

    /**
     * Mueve al enemigo en dirección al jugador.
     * Método reutilizable por subclases.
     */
    protected void moverHaciaJugador(double jugadorX, double jugadorY) {
        double dx = jugadorX - this.x;
        double dy = jugadorY - this.y;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        if (distancia > 0) {
            this.velocidadX = (dx / distancia) * velocidad;
            this.velocidadY = (dy / distancia) * velocidad;
        }
        this.x += velocidadX;
        this.y += velocidadY;
    }

    public void recibirDanio(int danio) {
        this.vida -= danio;
        if (this.vida <= 0) {
            this.vida = 0;
            this.activo = false;
        }
    }

    public void actualizarTiempo(double dt) {
        tiempoAnimacion += dt;
    }

    public void aplicarScroll(double desplazamiento) {
        this.y += desplazamiento;
        if (this.y > 700) this.activo = false;
    }

    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }
    public int getPuntosAlMorir() { return puntosAlMorir; }
}
