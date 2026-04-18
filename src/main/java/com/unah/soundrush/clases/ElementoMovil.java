package com.unah.soundrush.clases;

/**
 * Clase intermedia para elementos que se mueven.
 * Hereda de ObjetoJuego y agrega velocidades direccionales.
 */
public abstract class ElementoMovil extends ObjetoJuego {

    protected double velocidadX;
    protected double velocidadY;

    public ElementoMovil(double x, double y, int ancho, int alto, double velocidad, String indiceImagen) {
        super(x, y, ancho, alto, velocidad, indiceImagen);
        this.velocidadX = 0;
        this.velocidadY = 0;
    }

    public abstract void mover();

    public double getVelocidadX() { return velocidadX; }
    public void setVelocidadX(double velocidadX) { this.velocidadX = velocidadX; }
    public double getVelocidadY() { return velocidadY; }
    public void setVelocidadY(double velocidadY) { this.velocidadY = velocidadY; }
}
