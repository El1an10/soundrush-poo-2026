package com.unah.soundrush.clases;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Proyectil del jugador. Si tiene powerShot mata en un solo golpe.
 * Hereda de ElementoMovil.
 */
public class Proyectil extends ElementoMovil {

    private double t = 0;
    private boolean powerShot;

    public Proyectil(double x, double y, boolean powerShot) {
        super(x, y, 10, 16, 10, "proyectil");
        this.velocidadY = -11;
        this.powerShot  = powerShot;
    }

    @Override
    public void mover() {
        this.y += velocidadY;
        if (this.y < -20) this.activo = false;
    }

    @Override
    public void actualizar() { mover(); t += 0.15; }

    @Override
    public void pintar(GraphicsContext gc) {
        if (!activo) return;
        double cx = this.x + 5;

        if (powerShot) {
            // Power shot: más grande, rojo-carmesí brillante
            for (int i = 1; i <= 4; i++) {
                gc.setFill(Color.web("#FF2060", 0.08 * (5-i)));
                gc.fillOval(cx - 4 - i*2, this.y + i*4, (4+i*2)*2, 7);
            }
            gc.setFill(Color.web("#FF6090")); gc.fillOval(cx-7, this.y, 14, 14);
            gc.setFill(Color.web("#FFFFFF", 0.6)); gc.fillOval(cx-3, this.y+2, 5, 4);
            gc.setStroke(Color.web("#CC0040", 0.9)); gc.setLineWidth(1.5);
            gc.strokeOval(cx-7, this.y, 14, 14);
        } else {
            // Normal: dorado cian neón
            for (int i = 1; i <= 3; i++) {
                gc.setFill(Color.web("#00CFFF", 0.07 * (4-i)));
                gc.fillOval(cx-4, this.y+i*4, 8, 5);
            }
            gc.setFill(Color.web("#E8C040")); gc.fillOval(cx-5, this.y, 10, 10);
            gc.setFill(Color.web("#FFFFFF", 0.55)); gc.fillOval(cx-2, this.y+2, 4, 3);
            gc.setStroke(Color.web("#8A6010", 0.7)); gc.setLineWidth(1.2);
            gc.strokeOval(cx-5, this.y, 10, 10);
        }
    }

    public boolean isPowerShot() { return powerShot; }
    public void aplicarScroll(double d) { this.y += d; }
}
