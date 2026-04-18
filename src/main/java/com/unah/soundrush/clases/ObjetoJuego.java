package com.unah.soundrush.clases;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

/**
 * Clase base para todos los objetos del juego.
 * Aplica herencia: todos los personajes y objetos extienden de esta clase.
 */
public abstract class ObjetoJuego {

    protected double x;
    protected double y;
    protected int ancho;
    protected int alto;
    protected double velocidad;
    protected String indiceImagen;
    protected boolean activo;

    public ObjetoJuego(double x, double y, int ancho, int alto, double velocidad, String indiceImagen) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.velocidad = velocidad;
        this.indiceImagen = indiceImagen;
        this.activo = true;
    }

    // Métodos abstractos — cada subclase define su comportamiento
    public abstract void pintar(GraphicsContext gc);
    public abstract void actualizar();

    public Rectangle obtenerRectangulo() {
        return new Rectangle(this.x, this.y, this.ancho, this.alto);
    }

    public boolean colisionaCon(ObjetoJuego otro) {
        return this.obtenerRectangulo()
                   .getBoundsInParent()
                   .intersects(otro.obtenerRectangulo().getBoundsInParent());
    }

    // Getters y Setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public double getVelocidad() { return velocidad; }
    public void setVelocidad(double velocidad) { this.velocidad = velocidad; }
    public String getIndiceImagen() { return indiceImagen; }
    public void setIndiceImagen(String indiceImagen) { this.indiceImagen = indiceImagen; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
