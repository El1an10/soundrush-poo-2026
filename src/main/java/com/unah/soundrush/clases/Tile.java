package com.unah.soundrush.clases;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Tile del TileMap. Estética de ilustración/acuarela.
 * Hereda de ObjetoJuego.
 */
public class Tile extends ObjetoJuego {

    public static final int TAMANO = 64;

    public static final int VACIO      = 0;
    public static final int SUELO_OSC  = 1;
    public static final int SUELO_MUS  = 2;
    public static final int BORDE_IZQ  = 3;
    public static final int BORDE_DER  = 4;
    public static final int DECORACION = 5;

    private int tipoTile;
    private boolean solido;

    public Tile(int tipoTile, double x, double y) {
        super(x, y, TAMANO, TAMANO, 0, "tile");
        this.tipoTile = tipoTile;
        this.solido   = (tipoTile == BORDE_IZQ || tipoTile == BORDE_DER);
    }

    @Override public void actualizar() {}

    @Override
    public void pintar(GraphicsContext gc) {
        switch (tipoTile) {
            case SUELO_OSC  -> pintarSueloSilenciado(gc);
            case SUELO_MUS  -> pintarSueloMusical(gc);
            case BORDE_IZQ  -> pintarBorde(gc, true);
            case BORDE_DER  -> pintarBorde(gc, false);
            case DECORACION -> pintarDecoracion(gc);
        }
    }

    /** Suelo del mundo silenciado: semi-transparente para dejar ver el fondo neón */
    private void pintarSueloSilenciado(GraphicsContext gc) {
        gc.setFill(Color.web("#000000", 0.38));
        gc.fillRect(x, y, TAMANO, TAMANO);
        // Grietas finas
        gc.setStroke(Color.web("#000000", 0.25));
        gc.setLineWidth(0.7);
        gc.strokeLine(x + 12, y + 5,  x + 8,  y + 30);
        gc.strokeLine(x + 8,  y + 30, x + 15, y + 50);
        gc.strokeLine(x + 45, y + 10, x + 50, y + 40);
        // Borde grid muy sutil
        gc.setStroke(Color.web("#000000", 0.15));
        gc.setLineWidth(0.4);
        gc.strokeRect(x, y, TAMANO, TAMANO);
    }

    /** Suelo musical: más transparente, líneas de pentagrama neón */
    private void pintarSueloMusical(GraphicsContext gc) {
        gc.setFill(Color.web("#000000", 0.22));
        gc.fillRect(x, y, TAMANO, TAMANO);
        // Pentagrama tenue
        gc.setStroke(Color.web("#FFFFFF", 0.08));
        gc.setLineWidth(0.7);
        for (int i = 1; i <= 5; i++) {
            gc.strokeLine(x + 2, y + i * 10, x + TAMANO - 2, y + i * 10);
        }
        gc.setStroke(Color.web("#000000", 0.10));
        gc.setLineWidth(0.4);
        gc.strokeRect(x, y, TAMANO, TAMANO);
    }

    /** Bordes laterales: más opacos para dar sensación de límite */
    private void pintarBorde(GraphicsContext gc, boolean izquierdo) {
        gc.setFill(Color.web("#000000", 0.70));
        gc.fillRect(x, y, TAMANO, TAMANO);
        gc.setFill(Color.web("#FF1111", 0.35));
        if (izquierdo) gc.fillRect(x + TAMANO - 8, y, 8, TAMANO);
        else           gc.fillRect(x, y, 8, TAMANO);
        gc.setStroke(Color.web("#FF3333", 0.4));
        gc.setLineWidth(1.2);
        if (izquierdo) gc.strokeLine(x + TAMANO - 8, y, x + TAMANO - 8, y + TAMANO);
        else           gc.strokeLine(x + 8, y, x + 8, y + TAMANO);
    }

    /** Tile decorativo: muy transparente */
    private void pintarDecoracion(GraphicsContext gc) {
        gc.setFill(Color.web("#000000", 0.15));
        gc.fillRect(x, y, TAMANO, TAMANO);
        gc.setFont(javafx.scene.text.Font.font("Serif", 22));
        gc.setFill(Color.web("#FFFFFF", 0.07));
        gc.fillText("\u266B", x + 18, y + 44);
    }

    public void aplicarScroll(double d) { this.y += d; }
    public boolean isSolido() { return solido; }
    public int getTipoTile() { return tipoTile; }
}
