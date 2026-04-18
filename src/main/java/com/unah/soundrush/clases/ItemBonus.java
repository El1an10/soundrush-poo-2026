package com.unah.soundrush.clases;

import com.unah.soundrush.App;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Items coleccionables.
 * PENTAGRAMA: +1 vida
 * AMPLIFICADOR: +500 puntos
 * AURICULAR: escudo
 * BOOST_VELOCIDAD: jugador 2x velocidad 10s
 * BOOST_DISPARO: un disparo mata de un tiro 15s
 */
public class ItemBonus extends ObjetoJuego {

    public enum TipoItem {
        PENTAGRAMA,
        AMPLIFICADOR,
        AURICULAR,
        BOOST_VELOCIDAD,
        BOOST_DISPARO
    }

    private TipoItem tipo;
    private boolean capturado;
    private double tiempoAnimacion;

    public ItemBonus(double x, double y, TipoItem tipo) {
        super(x, y, 32, 32, 0, "item");
        this.tipo = tipo;
    }

    @Override
    public void actualizar() { tiempoAnimacion += 0.05; }

    @Override
    public void pintar(GraphicsContext gc) {
        if (capturado || !activo) return;
        double bob = Math.sin(tiempoAnimacion) * 3;
        double dy  = this.y + bob;

        switch (tipo) {
            case PENTAGRAMA      -> pintarPentagrama(gc, dy);
            case AMPLIFICADOR    -> pintarAmplificador(gc, dy);
            case AURICULAR       -> pintarAuricular(gc, dy);
            case BOOST_VELOCIDAD -> pintarBoostVelocidad(gc, dy);
            case BOOST_DISPARO   -> pintarBoostDisparo(gc, dy);
        }
    }

    // ── Visuales ──────────────────────────────────────────────────────────────

    private void pintarPentagrama(GraphicsContext gc, double dy) {
        // Corazón neón verde
        RadialGradient g = new RadialGradient(0,0,x+16,dy+16,18,false,CycleMethod.NO_CYCLE,
            new Stop(0,Color.web("#00FF88",0.5)), new Stop(1,Color.web("#00FF88",0.0)));
        gc.setFill(g); gc.fillOval(x-2,dy-2,36,36);

        gc.setFill(Color.web("#00DD66")); gc.fillRoundRect(x+2,dy+2,28,28,8,8);
        gc.setStroke(Color.web("#00FF88",0.9)); gc.setLineWidth(1.5);
        gc.strokeRoundRect(x+2,dy+2,28,28,8,8);
        gc.setFill(Color.web("#FFFFFF",0.9));
        gc.setFont(Font.font("Serif",FontWeight.BOLD,20));
        gc.fillText("\u2665", x+8, dy+24);
    }

    private void pintarAmplificador(GraphicsContext gc, double dy) {
        RadialGradient g = new RadialGradient(0,0,x+16,dy+16,18,false,CycleMethod.NO_CYCLE,
            new Stop(0,Color.web("#FFB400",0.5)), new Stop(1,Color.web("#FFB400",0.0)));
        gc.setFill(g); gc.fillOval(x-2,dy-2,36,36);

        gc.setFill(Color.web("#CC8800")); gc.fillRoundRect(x+2,dy+2,28,28,8,8);
        gc.setStroke(Color.web("#FFD700",0.9)); gc.setLineWidth(1.5);
        gc.strokeRoundRect(x+2,dy+2,28,28,8,8);
        gc.setFill(Color.web("#FFFFFF",0.9));
        gc.setFont(Font.font("Monospace",FontWeight.BOLD,13));
        gc.fillText("+500", x+4, dy+21);
    }

    private void pintarAuricular(GraphicsContext gc, double dy) {
        RadialGradient g = new RadialGradient(0,0,x+16,dy+16,18,false,CycleMethod.NO_CYCLE,
            new Stop(0,Color.web("#A020F0",0.5)), new Stop(1,Color.web("#A020F0",0.0)));
        gc.setFill(g); gc.fillOval(x-2,dy-2,36,36);

        gc.setFill(Color.web("#7010C0")); gc.fillRoundRect(x+2,dy+2,28,28,8,8);
        gc.setStroke(Color.web("#C060FF",0.9)); gc.setLineWidth(1.5);
        gc.strokeRoundRect(x+2,dy+2,28,28,8,8);
        // Auricular icon
        gc.setStroke(Color.web("#FFFFFF",0.9)); gc.setLineWidth(2);
        gc.strokeArc(x+8,dy+6,16,12,0,180,javafx.scene.shape.ArcType.OPEN);
        gc.setFill(Color.web("#FFFFFF",0.9));
        gc.fillRoundRect(x+6,dy+15,5,8,3,3);
        gc.fillRoundRect(x+21,dy+15,5,8,3,3);
    }

    private void pintarBoostVelocidad(GraphicsContext gc, double dy) {
        // Rayo cian
        RadialGradient g = new RadialGradient(0,0,x+16,dy+16,20,false,CycleMethod.NO_CYCLE,
            new Stop(0,Color.web("#00CFFF",0.6)), new Stop(1,Color.web("#00CFFF",0.0)));
        gc.setFill(g); gc.fillOval(x-4,dy-4,40,40);

        gc.setFill(Color.web("#006888")); gc.fillRoundRect(x+2,dy+2,28,28,8,8);
        gc.setStroke(Color.web("#00CFFF",0.95)); gc.setLineWidth(2);
        gc.strokeRoundRect(x+2,dy+2,28,28,8,8);
        // Rayo
        double[] rx = {x+16, x+11, x+15, x+10, x+20, x+15, x+19};
        double[] ry = {dy+4, dy+14, dy+14, dy+28, dy+14, dy+14, dy+4};
        gc.setFill(Color.web("#80FFFF",0.95));
        gc.fillPolygon(new double[]{x+16,x+10,x+15,x+10,x+22,x+17,x+22},
                       new double[]{dy+4, dy+15,dy+15,dy+28,dy+15,dy+15,dy+4}, 7);
        gc.setStroke(Color.web("#FFFFFF",0.6)); gc.setLineWidth(1);
        gc.strokePolygon(new double[]{x+16,x+10,x+15,x+10,x+22,x+17,x+22},
                         new double[]{dy+4, dy+15,dy+15,dy+28,dy+15,dy+15,dy+4}, 7);
    }

    private void pintarBoostDisparo(GraphicsContext gc, double dy) {
        // Estrella de fuego carmesí
        double t = tiempoAnimacion;
        RadialGradient g = new RadialGradient(0,0,x+16,dy+16,22,false,CycleMethod.NO_CYCLE,
            new Stop(0,Color.web("#FF2060",0.6)), new Stop(1,Color.web("#FF2060",0.0)));
        gc.setFill(g); gc.fillOval(x-4,dy-4,40,40);

        gc.setFill(Color.web("#880020")); gc.fillRoundRect(x+2,dy+2,28,28,8,8);
        gc.setStroke(Color.web("#FF4080",0.95)); gc.setLineWidth(2);
        gc.strokeRoundRect(x+2,dy+2,28,28,8,8);

        // Icono: nota con rayo
        gc.setFill(Color.web("#FFAACC",0.95));
        gc.fillOval(x+6, dy+17, 9, 7);
        gc.setStroke(Color.web("#FFFFFF",0.9)); gc.setLineWidth(2);
        gc.strokeLine(x+15, dy+17, x+15, dy+7);
        gc.strokeLine(x+15, dy+7, x+22, dy+10);
        // Pequeños rayos
        gc.setStroke(Color.web("#FF80A0",0.7)); gc.setLineWidth(1.2);
        gc.strokeLine(x+20,dy+6, x+24,dy+4);
        gc.strokeLine(x+22,dy+10,x+27,dy+9);
        gc.strokeLine(x+21,dy+14,x+26,dy+15);
    }

    public void aplicarScroll(double d) {
        this.y += d;
        if (this.y > App.ALTO_VENTANA + 40) this.activo = false;
    }

    public boolean isCapturado() { return capturado; }
    public void setCapturado(boolean c) { this.capturado = c; }
    public TipoItem getTipo() { return tipo; }
}
