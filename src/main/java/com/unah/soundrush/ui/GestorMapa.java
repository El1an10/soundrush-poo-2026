package com.unah.soundrush.ui;

import com.unah.soundrush.clases.*;
import com.unah.soundrush.enemigos.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gestiona el mapa, scroll, generación de enemigos y obstáculos.
 * El escenario avanza cuando el jugador sube.
 */
public class GestorMapa {

    // Ancho del juego en tiles
    public static final int TILES_ANCHO = 16; // 16 * 64 = 1024px
    private static final int ANCHO_VENTANA = 1024;
    private static final int ALTO_VENTANA  = 640;

    private List<Tile> tiles;
    private List<Obstaculo> obstaculos;
    private List<ItemBonus> items;
    private List<Enemigo> enemigos;
    private List<Censor.ProyectilEnemigo> proyectilesEnemigos;

    private double distanciaRecorrida;
    private double distanciaUltimoObstaculo;
    private double distanciaUltimoEnemigo;
    private double distanciaUltimoItem;

    private Random random;
    private int oleadaActual;

    // Datos del mapa: columnas (izq y der son bordes sólidos)
    // 0=vacío, 1=suelo oscuro, 2=suelo musical, 3=borde izq, 4=borde der, 5=deco
    private static final int[][] PLANTILLA_FILA = {
        {3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4},
    };

    public GestorMapa() {
        tiles = new ArrayList<>();
        obstaculos = new ArrayList<>();
        items = new ArrayList<>();
        enemigos = new ArrayList<>();
        proyectilesEnemigos = new ArrayList<>();
        random = new Random();
        distanciaRecorrida = 0;
        oleadaActual = 0;
        generarFilasIniciales();
    }

    private void generarFilasIniciales() {
        // Genera suficientes filas para llenar la pantalla + buffer
        for (int fila = -5; fila < 12; fila++) {
            generarFilaTile(fila * Tile.TAMANO);
        }
    }

    private void generarFilaTile(double y) {
        for (int col = 0; col < TILES_ANCHO; col++) {
            int tipo;
            if (col == 0) tipo = Tile.BORDE_IZQ;
            else if (col == TILES_ANCHO - 1) tipo = Tile.BORDE_DER;
            else {
                // Alternar suelo oscuro y musical según distancia
                tipo = (distanciaRecorrida % 400 < 200) ? Tile.SUELO_OSC : Tile.SUELO_MUS;
                if (random.nextInt(20) == 0) tipo = Tile.DECORACION;
            }
            tiles.add(new Tile(tipo, col * Tile.TAMANO, y));
        }
    }

    /**
     * Calcula la velocidad de scroll según posición del jugador.
     * Cuando el jugador llega al 38% superior, el escenario se desplaza
     * exactamente a la misma velocidad que el jugador se mueve hacia arriba.
     * Retorna la velocidad de scroll (positiva = mundo baja).
     */
    public double calcularScroll(double jugadorY, double velJugador) {
        double lineaScroll = ALTO_VENTANA * 0.38;
        if (jugadorY <= lineaScroll) {
            // Scroll proporcional: más lejos de la línea = más rápido, máx 8
            double exceso = lineaScroll - jugadorY;
            return Math.min(exceso * 0.18 + velJugador, 8.0);
        }
        return 0;
    }

    public void aplicarScroll(double velocidadScroll) {
        if (velocidadScroll <= 0) return;

        distanciaRecorrida       += velocidadScroll;
        distanciaUltimoObstaculo += velocidadScroll;
        distanciaUltimoEnemigo   += velocidadScroll;
        distanciaUltimoItem      += velocidadScroll;

        // Mover todos los elementos
        for (Tile t                     : tiles)               t.aplicarScroll(velocidadScroll);
        for (Obstaculo o                : obstaculos)          o.aplicarScroll(velocidadScroll);
        for (ItemBonus i                : items)               i.aplicarScroll(velocidadScroll);
        for (Enemigo e                  : enemigos)            e.aplicarScroll(velocidadScroll);
        for (Censor.ProyectilEnemigo p  : proyectilesEnemigos) p.aplicarScroll(velocidadScroll);

        // Limpiar PRIMERO para no acumular
        limpiarElementosInactivos();

        // Generar nuevas filas: buscar el tile superior sin stream()
        double yMin = Double.MAX_VALUE;
        for (Tile t : tiles) {
            if (t.getY() < yMin) yMin = t.getY();
        }
        // Generar hacia arriba mientras haya hueco
        while (yMin > -Tile.TAMANO * 2) {
            yMin -= Tile.TAMANO;
            generarFilaTile(yMin);
        }

        // Generar obstáculos cada ~220px
        if (distanciaUltimoObstaculo > 220) {
            generarObstaculos();
            distanciaUltimoObstaculo = 0;
        }

        // Generar enemigos cada ~300px
        if (distanciaUltimoEnemigo > 300) {
            generarEnemigos();
            distanciaUltimoEnemigo = 0;
        }

        // Generar items cada ~400px
        if (distanciaUltimoItem > 400) {
            generarItem();
            distanciaUltimoItem = 0;
        }
    }

    private void generarObstaculos() {
        int cantidad = 1 + random.nextInt(3);
        for (int i = 0; i < cantidad; i++) {
            // Posición aleatoria en X (evitar bordes)
            double x = 80 + random.nextInt(ANCHO_VENTANA - 200);
            double y = -60 - random.nextInt(80);
            int ancho = 40 + random.nextInt(80);
            int alto = 30 + random.nextInt(30);

            Obstaculo.TipoObstaculo[] tipos = Obstaculo.TipoObstaculo.values();
            Obstaculo.TipoObstaculo tipo = tipos[random.nextInt(tipos.length)];
            obstaculos.add(new Obstaculo(x, y, ancho, alto, tipo));
        }
    }

    /**
     * Guión de oleadas: cada entrada define los tipos de enemigos de esa oleada.
     * 0 = Silenciador, 1 = VientoOpresor, 2 = BurbujaShhh, 3 = Censor
     * Las oleadas se recorren en orden y se repiten en ciclos con mayor densidad.
     */
    private static final int[][] GUION_OLEADAS = {
        {0},           // Oleada 1: Silenciador solitario
        {0, 0},        // Oleada 2: Par de Silenciadores
        {1},           // Oleada 3: Viento Opresor
        {0, 1},        // Oleada 4: Silenciador + Viento
        {2},           // Oleada 5: Burbuja Shhh
        {1, 2},        // Oleada 6: Viento + Burbuja
        {0, 1, 2},     // Oleada 7: Triple amenaza
        {3},           // Oleada 8: primer Censor (a partir de dist 500)
        {0, 3},        // Oleada 9: Silenciador + Censor
        {1, 3},        // Oleada 10: Viento + Censor
        {2, 3},        // Oleada 11: Burbuja + Censor
        {0, 1, 3},     // Oleada 12: Silenciador + Viento + Censor
        {3, 3},        // Oleada 13: Doble Censor
        {1, 2, 3},     // Oleada 14: Viento + Burbuja + Censor
        {3, 3, 3},     // Oleada 15: Triple Censor (máxima dificultad)
    };

    private void generarEnemigos() {
        // Índice de oleada en el guión (cicla al superar la última)
        int idxOleada = oleadaActual % GUION_OLEADAS.length;
        int[] tiposOleada = GUION_OLEADAS[idxOleada];
        oleadaActual++;

        for (int i = 0; i < tiposOleada.length; i++) {
            double x = 100 + random.nextInt(ANCHO_VENTANA - 200);
            double y = -50 - i * 70;

            switch (tiposOleada[i]) {
                case 0 -> enemigos.add(new com.unah.soundrush.enemigos.Silenciador(x, y));
                case 1 -> enemigos.add(new VientoOpresor(x, y));
                case 2 -> enemigos.add(new com.unah.soundrush.enemigos.BurbujaShhh(x, y));
                case 3 -> {
                    // El Censor solo aparece una vez superada la distancia mínima
                    if (distanciaRecorrida > 500) {
                        enemigos.add(new Censor(x, y));
                    } else {
                        enemigos.add(new com.unah.soundrush.enemigos.Silenciador(x, y));
                    }
                }
            }
        }
    }

    private void generarItem() {
        double x = 120 + random.nextInt(ANCHO_VENTANA - 240);
        double y = -40;
        // Todos los tipos disponibles con pesos: boosts menos frecuentes
        int roll = random.nextInt(10);
        ItemBonus.TipoItem tipo;
        if      (roll < 3) tipo = ItemBonus.TipoItem.PENTAGRAMA;
        else if (roll < 5) tipo = ItemBonus.TipoItem.AMPLIFICADOR;
        else if (roll < 7) tipo = ItemBonus.TipoItem.AURICULAR;
        else if (roll < 9) tipo = ItemBonus.TipoItem.BOOST_VELOCIDAD;
        else               tipo = ItemBonus.TipoItem.BOOST_DISPARO;
        items.add(new ItemBonus(x, y, tipo));
    }

    private void limpiarElementosInactivos() {
        // Tiles: eliminar los que salen por abajo
        tiles.removeIf(t -> t.getY() > ALTO_VENTANA + Tile.TAMANO * 2);

        // Cap duro: máximo 300 tiles en pantalla (16 cols × ~18 filas visibles + buffer)
        while (tiles.size() > 320) tiles.remove(tiles.size() - 1);

        obstaculos.removeIf(o -> !o.isActivo());
        items.removeIf(i -> !i.isActivo());

        // Enemigos: BurbujaShhh se mantiene mientras rompe
        enemigos.removeIf(e -> {
            if (e instanceof BurbujaShhh b) {
                return b.getEstadoBurbuja() == BurbujaShhh.EstadoBurbuja.MUERTA;
            }
            return !e.isActivo();
        });

        // Cap duro en enemigos y proyectiles para evitar acumulación
        if (enemigos.size() > 20) enemigos.subList(0, enemigos.size() - 20).clear();
        if (proyectilesEnemigos.size() > 30) proyectilesEnemigos.subList(0, proyectilesEnemigos.size() - 30).clear();

        proyectilesEnemigos.removeIf(p -> !p.activo);
    }

    public void agregarProyectilEnemigo(Censor.ProyectilEnemigo p) {
        proyectilesEnemigos.add(p);
    }

    // Getters
    public List<Tile> getTiles() { return tiles; }
    public List<Obstaculo> getObstaculos() { return obstaculos; }
    public List<ItemBonus> getItems() { return items; }
    public List<Enemigo> getEnemigos() { return enemigos; }
    public List<Censor.ProyectilEnemigo> getProyectilesEnemigos() { return proyectilesEnemigos; }
    public double getDistanciaRecorrida() { return distanciaRecorrida; }
}
