package com.unah.soundrush.ui;

import com.unah.soundrush.App;
import com.unah.soundrush.clases.Jugador;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;
import java.util.Map;

/**
 * HUD del juego: puntuación, vidas, distancia recorrida, escudo.
 */
public class HUD {

    private Font fuenteGrande;
    private Font fuenteNormal;
    private Font fuentePequena;

    public HUD() {
        fuenteGrande  = Font.font("Monospace", FontWeight.BOLD, 22);
        fuenteNormal  = Font.font("Monospace", FontWeight.BOLD, 14);
        fuentePequena = Font.font("Monospace", FontWeight.NORMAL, 11);
    }

    public void pintar(GraphicsContext gc, Jugador jugador, int puntuacion, double distancia) {
        // Barra superior
        gc.setFill(Color.web("#000000", 0.65));
        gc.fillRect(0, 0, App.ANCHO_VENTANA, 46);

        // Puntuación
        gc.setFont(fuenteGrande);
        gc.setFill(Color.web("#FFD700"));
        gc.fillText("\u266A " + puntuacion, 14, 31);

        // Distancia
        gc.setFont(fuenteNormal);
        gc.setFill(Color.web("#80E0FF"));
        gc.fillText(String.format("DIST: %dm", (int)(distancia / 10)),
                    App.ANCHO_VENTANA / 2 - 50, 29);

        // Vidas
        gc.setFont(fuenteGrande);
        for (int i = 0; i < jugador.getVidas(); i++) {
            gc.setFill(Color.web("#FF3366"));
            gc.fillText("\u2665", App.ANCHO_VENTANA - 32 - i * 28, 31);
        }

        // Borde inferior
        gc.setStroke(Color.web("#FFFFFF", 0.08));
        gc.setLineWidth(1);
        gc.strokeLine(0, 46, App.ANCHO_VENTANA, 46);

        // ── Indicadores de boost ──────────────────────────────────────────────
        int boostSlot = 0;

        if (jugador.isEscudoActivo()) {
            pintarBoostBar(gc, boostSlot++, "\u25C8 ESCUDO", "#A020F0", 1.0);
        }
        if (jugador.isBoostVelocidadActivo()) {
            double progreso = jugador.getTiempoBoostVelocidad() / 5.0;
            pintarBoostBar(gc, boostSlot++, "\u26A1 VELOCIDAD", "#00CFFF", progreso);
        }
        if (jugador.isBoostDisparoActivo()) {
            double progreso = jugador.getTiempoBoostDisparo() / 15.0;
            pintarBoostBar(gc, boostSlot++, "\u2605 PODER", "#FF2060", progreso);
        }
    }

    private void pintarBoostBar(GraphicsContext gc, int slot, String label,
                                 String color, double progreso) {
        double bx = 14 + slot * 150;
        double by = 50;
        // Fondo
        gc.setFill(Color.web("#000000", 0.5));
        gc.fillRoundRect(bx, by, 140, 16, 6, 6);
        // Barra de progreso
        gc.setFill(Color.web(color, 0.75));
        gc.fillRoundRect(bx + 1, by + 1, 138 * Math.min(1, progreso), 14, 5, 5);
        // Texto
        gc.setFont(fuentePequena);
        gc.setFill(Color.web("#FFFFFF", 0.9));
        gc.fillText(label, bx + 6, by + 12);
    }

    public void pintarGameOver(GraphicsContext gc, int puntuacion, double distancia) {
        // Overlay oscuro
        gc.setFill(Color.web("#000000", 0.82));
        gc.fillRect(0, 0, App.ANCHO_VENTANA, App.ALTO_VENTANA);

        // Título
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 52));
        gc.setFill(Color.web("#CC0000"));
        gc.fillText("SILENCIADO", App.ANCHO_VENTANA / 2.0 - 170, App.ALTO_VENTANA / 2.0 - 80);

        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 18));
        gc.setFill(Color.web("#888888"));
        gc.fillText("La música fue suprimida...", App.ANCHO_VENTANA / 2.0 - 110, App.ALTO_VENTANA / 2.0 - 40);

        // Puntuación final
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 26));
        gc.setFill(Color.web("#FFD700"));
        gc.fillText("PUNTUACIÓN: " + puntuacion, App.ANCHO_VENTANA / 2.0 - 120, App.ALTO_VENTANA / 2.0 + 10);

        gc.setFont(fuenteNormal);
        gc.setFill(Color.web("#7EC8E3"));
        gc.fillText(String.format("Distancia: %dm", (int)(distancia / 10)), App.ANCHO_VENTANA / 2.0 - 60, App.ALTO_VENTANA / 2.0 + 40);

        // Instrucciones
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        gc.setFill(Color.web("#AAAAAA"));
        gc.fillText("R = intentar de nuevo      ENTER = menú principal", App.ANCHO_VENTANA / 2.0 - 200, App.ALTO_VENTANA / 2.0 + 85);
        gc.setFill(Color.web("#FFD700", 0.85));
        gc.fillText("S = ver puntuaciones", App.ANCHO_VENTANA / 2.0 - 80, App.ALTO_VENTANA / 2.0 + 110);
    }

    public void pintarPantallaTitulo(GraphicsContext gc) {
        double cx = App.ANCHO_VENTANA / 2.0;

        // Panel central semi-transparente con borde dorado
        gc.setFill(Color.web("#000000", 0.72));
        gc.fillRoundRect(cx - 420, 30, 840, App.ALTO_VENTANA - 60, 18, 18);
        gc.setStroke(Color.web("#FFD700", 0.4));
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(cx - 420, 30, 840, App.ALTO_VENTANA - 60, 18, 18);

        // ── Título ────────────────────────────────────────────────────────────
        gc.setFont(Font.font("Serif", FontWeight.BOLD, 62));
        gc.setFill(Color.web("#FFD700"));
        gc.fillText("♪ SOUND RUSH ♪", cx - 235, 110);

        // Subtítulo
        gc.setFont(Font.font("Serif", FontWeight.BOLD, 17));
        gc.setFill(Color.web("#00CFFF"));
        gc.fillText("— La Batalla de la Última Nota —", cx - 168, 140);

        // Separador dorado
        gc.setStroke(Color.web("#FFD700", 0.5));
        gc.setLineWidth(1);
        gc.strokeLine(cx - 380, 155, cx + 380, 155);

        // ── Historia ──────────────────────────────────────────────────────────
        gc.setFont(Font.font("Serif", FontWeight.BOLD, 15));
        gc.setFill(Color.web("#FFD700", 0.9));
        gc.fillText("✦  HISTORIA  ✦", cx - 55, 185);

        String[] historia = {
            "En un mundo donde el progreso apagó cada melodía, la música fue declarada",
            "\"innecesaria\". Los gobiernos del silencio sellaron las salas de conciertos,",
            "quemaron las partituras y suprimieron toda frecuencia que no fuera el viento.",
            "",
            "Pero en las grietas de ese mundo gris sobrevivió UNA NOTA — la última.",
            "Tú eres esa nota. Tu misión: abrirte paso entre los Silenciadores, los",
            "Censores y las barreras del viento para llevar la música de vuelta",
            "al corazón de la humanidad. Si caes, el silencio gana para siempre."
        };

        gc.setFont(Font.font("Serif", FontWeight.NORMAL, 14));
        for (int i = 0; i < historia.length; i++) {
            if (historia[i].isEmpty()) continue;
            boolean esDestacado = historia[i].contains("UNA NOTA") || historia[i].contains("Tú eres");
            gc.setFill(esDestacado ? Color.web("#00FF88") : Color.web("#CCCCCC"));
            gc.fillText(historia[i], cx - 350, 210 + i * 22);
        }

        // Separador
        gc.setStroke(Color.web("#FFD700", 0.3));
        gc.setLineWidth(1);
        gc.strokeLine(cx - 380, 400, cx + 380, 400);

        // ── Enemigos ──────────────────────────────────────────────────────────
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 13));
        gc.setFill(Color.web("#FFD700", 0.85));
        gc.fillText("✦  ENEMIGOS  ✦", cx - 58, 425);

        gc.setFont(Font.font("Monospace", FontWeight.NORMAL, 12));
        gc.setFill(Color.web("#CC3333")); gc.fillText("⬛ Silenciador", cx - 360, 448);
        gc.setFill(Color.web("#AAAAAA")); gc.fillText("— Lento y resistente. Aplasta todo lo que encuentra.", cx - 250, 448);

        gc.setFill(Color.web("#4A90D9")); gc.fillText("☁ Viento Opresor", cx - 360, 468);
        gc.setFill(Color.web("#AAAAAA")); gc.fillText("— Rápido y errático. Se mueve en zigzag.", cx - 250, 468);

        gc.setFill(Color.web("#FF4444")); gc.fillText("◆ Censor", cx - 360, 488);
        gc.setFill(Color.web("#AAAAAA")); gc.fillText("— Dispara ondas de ruido. Peligroso a distancia.", cx - 250, 488);

        // ── Items ─────────────────────────────────────────────────────────────
        gc.setStroke(Color.web("#FFD700", 0.3));
        gc.strokeLine(cx - 380, 502, cx + 380, 502);

        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
        gc.setFill(Color.web("#00FF88")); gc.fillText("♥ Pentagrama = +1 vida", cx - 360, 522);
        gc.setFill(Color.web("#FF6B00")); gc.fillText("◈ Amplificador = +500 pts", cx - 110, 522);
        gc.setFill(Color.web("#9B59B6")); gc.fillText("● Auricular = escudo temporal", cx + 150, 522);

        // ── Controles ─────────────────────────────────────────────────────────
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 13));
        gc.setFill(Color.web("#7EC8E3"));
        gc.fillText("↑ ↓ ← →  mover     ESPACIO  disparar     R  reiniciar", cx - 240, 550);

        // ── Botones de acción ─────────────────────────────────────────────────
        // Botón ENTER
        gc.setFill(Color.web("#00FF88", 0.15));
        gc.fillRoundRect(cx - 230, 560, 340, 34, 10, 10);
        gc.setStroke(Color.web("#00FF88", 0.6));
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(cx - 230, 560, 340, 34, 10, 10);
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        gc.setFill(Color.web("#00FF88"));
        gc.fillText("▶  ENTER = COMENZAR", cx - 215, 583);

        // Botón SCORES
        gc.setFill(Color.web("#FFD700", 0.12));
        gc.fillRoundRect(cx + 125, 560, 215, 34, 10, 10);
        gc.setStroke(Color.web("#FFD700", 0.6));
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(cx + 125, 560, 215, 34, 10, 10);
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        gc.setFill(Color.web("#FFD700"));
        gc.fillText("★  S = PUNTUACIONES", cx + 133, 583);
    }

    public void pintarPantallaScores(GraphicsContext gc, GestorPuntuaciones gestor) {
        gc.setFill(Color.web("#000000", 0.88));
        gc.fillRect(0, 0, App.ANCHO_VENTANA, App.ALTO_VENTANA);

        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 32));
        gc.setFill(Color.web("#FFD700"));
        gc.fillText("TOP 10 - MEJORES NOTAS", App.ANCHO_VENTANA / 2.0 - 210, 80);

        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 18));
        List<Map.Entry<String, Integer>> lista = gestor.getPuntuaciones();
        for (int i = 0; i < lista.size(); i++) {
            gc.setFill(i == 0 ? Color.web("#FFD700") : Color.web("#AAAAAA"));
            String linea = String.format("%2d.  %-15s %7d", i + 1, lista.get(i).getKey(), lista.get(i).getValue());
            gc.fillText(linea, App.ANCHO_VENTANA / 2.0 - 180, 130 + i * 40);
        }

        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        gc.setFill(Color.web("#7EC8E3"));
        gc.fillText("ENTER = volver al menú", App.ANCHO_VENTANA / 2.0 - 100, App.ALTO_VENTANA - 40);
    }
}
