package com.unah.soundrush.clases;

import javafx.scene.shape.Rectangle;

/**
 * Gestiona animaciones de sprite sheets.
 * Calcula el frame actual basado en el tiempo transcurrido.
 */
public class Animacion {

    private String nombreAnimacion;
    private Rectangle[] coordenadasImagenes;
    private int cantidadFrames;
    private int frameActual;
    private double duracion; // segundos por frame

    public Animacion(String nombreAnimacion, Rectangle[] coordenadasImagenes, double duracion) {
        this.nombreAnimacion = nombreAnimacion;
        this.coordenadasImagenes = coordenadasImagenes;
        this.duracion = duracion;
        this.cantidadFrames = coordenadasImagenes.length;
        this.frameActual = 0;
    }

    /**
     * Calcula y devuelve el rectángulo del frame actual según el tiempo.
     * Sobrecarga: también permite calcular por índice directo.
     */
    public Rectangle calcularFrame(double t) {
        this.cantidadFrames = coordenadasImagenes.length;
        this.frameActual = (int) ((t % (cantidadFrames * duracion)) / duracion);
        return coordenadasImagenes[frameActual];
    }

    // Sobrecarga del método calcularFrame — devuelve frame por índice
    public Rectangle calcularFrame(int indice) {
        if (indice >= 0 && indice < cantidadFrames) {
            this.frameActual = indice;
            return coordenadasImagenes[indice];
        }
        return coordenadasImagenes[0];
    }

    public String getNombreAnimacion() { return nombreAnimacion; }
    public int getFrameActual() { return frameActual; }
    public int getCantidadFrames() { return cantidadFrames; }
    public double getDuracion() { return duracion; }
    public void setDuracion(double duracion) { this.duracion = duracion; }
}
