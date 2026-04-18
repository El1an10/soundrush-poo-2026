package com.unah.soundrush;

import com.unah.soundrush.clases.*;
import com.unah.soundrush.enemigos.*;
import com.unah.soundrush.ui.*;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class App extends Application {

    public static final int ANCHO_VENTANA = 1024;
    public static final int ALTO_VENTANA  = 640;

    public static boolean teclaArriba    = false;
    public static boolean teclaAbajo     = false;
    public static boolean teclaIzquierda = false;
    public static boolean teclaDerecha   = false;
    public static boolean teclaEspacio   = false;
    public static boolean teclaEnter     = false;
    public static boolean teclaR         = false;
    public static boolean teclaS         = false; // S en menú = ver puntuaciones

    private enum EstadoJuego { TITULO, JUGANDO, GAME_OVER, SCORES }
    private EstadoJuego estado;

    private Canvas canvas;
    private GraphicsContext gc;
    private Scene escena;

    private Jugador jugador;
    private List<Proyectil> proyectiles;
    private GestorMapa gestorMapa;
    private GestorPuntuaciones gestorPuntuaciones;
    private HUD hud;

    /**
     * Mapa de imágenes del juego — patrón del proyecto base del profesor.
     * Clave: identificador del sprite (p.ej. "jugador", "silenciador").
     * Valor: Image de JavaFX cargada desde resources.
     */
    public static HashMap<String, Image> imagenes;

    private int puntuacion;
    private long tiempoUltimoFrame;
    private double tiempoAcumulado;
    private AnimationTimer ciclo;

    private double[] particulasX;
    private double[] particulasY;
    private double[] particulasVel;
    private double[] particulasAlpha;
    private double[] particulasTamano;
    private char[]   particulasSimbolo;
    private static final int NUM_PARTICULAS = 50;

    private double[] ondaFase;
    private static final int NUM_ONDAS = 8;

    private double[] estrellasX;
    private double[] estrellasY;
    private double[] estrellasBrillo;
    private static final int NUM_ESTRELLAS = 90;

    private double tiempoFondo = 0;

    // Fuentes cacheadas — se crean una vez, no cada frame
    private static final Font F_PART_S  = Font.font("Serif", 15);
    private static final Font F_PART_G  = Font.font("Serif", 22);
    private static final Font F_STREAM  = Font.font("Serif", FontWeight.BOLD, 17);
    private static final Font F_STREAM_G= Font.font("Serif", FontWeight.BOLD, 24);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage ventana) {
        inicializarComponentes();
        configurarVentana(ventana);
        gestionarEventos();
        iniciarCicloJuego();
        ventana.show();
    }

    private void inicializarComponentes() {
        canvas = new Canvas(ANCHO_VENTANA, ALTO_VENTANA);
        gc = canvas.getGraphicsContext2D();
        gestorPuntuaciones = new GestorPuntuaciones();
        hud = new HUD();
        estado = EstadoJuego.TITULO;
        imagenes = new HashMap<>();
        cargarImagenes();
        inicializarParticulas();
    }

    /**
     * Carga imágenes desde la carpeta de recursos.
     * Patrón del proyecto base del profesor (App.imagenes).
     * Si un recurso no existe aún, se omite sin romper la aplicación.
     */
    private void cargarImagenes() {
        String[] claves = {
            "jugador", "silenciador", "viento_opresor",
            "burbuja_shhh", "censor",
            "item_pentagrama", "item_amplificador",
            "item_auricular", "item_boost_velocidad", "item_boost_disparo",
            "tile_suelo", "tile_borde"
        };
        for (String clave : claves) {
            try {
                var url = getClass().getResource(clave + ".png");
                if (url != null) {
                    imagenes.put(clave, new Image(url.toExternalForm()));
                }
                // Si el archivo no existe, la entidad usa su renderizado por formas (fallback)
            } catch (Exception e) {
                System.out.println("[imagenes] Sprite no encontrado: " + clave + ".png (usando formas)");
            }
        }
        System.out.println("[imagenes] Cargadas: " + imagenes.size() + " / " + claves.length + " sprites.");
    }

    private void inicializarParticulas() {
        Random rng = new Random();
        char[] simbolos = { '\u266A', '\u266B', '\u266C', '\u2669', '\u266D', '\u266F' };

        particulasX       = new double[NUM_PARTICULAS];
        particulasY       = new double[NUM_PARTICULAS];
        particulasVel     = new double[NUM_PARTICULAS];
        particulasAlpha   = new double[NUM_PARTICULAS];
        particulasTamano  = new double[NUM_PARTICULAS];
        particulasSimbolo = new char[NUM_PARTICULAS];
        for (int i = 0; i < NUM_PARTICULAS; i++) {
            particulasX[i]       = rng.nextDouble() * ANCHO_VENTANA;
            particulasY[i]       = rng.nextDouble() * ALTO_VENTANA;
            particulasVel[i]     = 0.25 + rng.nextDouble() * 0.9;
            particulasAlpha[i]   = 0.08 + rng.nextDouble() * 0.25;
            particulasTamano[i]  = 10 + rng.nextDouble() * 18;
            particulasSimbolo[i] = simbolos[rng.nextInt(simbolos.length)];
        }

        ondaFase = new double[NUM_ONDAS];
        for (int i = 0; i < NUM_ONDAS; i++) ondaFase[i] = rng.nextDouble() * Math.PI * 2;

        estrellasX      = new double[NUM_ESTRELLAS];
        estrellasY      = new double[NUM_ESTRELLAS];
        estrellasBrillo = new double[NUM_ESTRELLAS];
        for (int i = 0; i < NUM_ESTRELLAS; i++) {
            estrellasX[i]      = rng.nextDouble() * ANCHO_VENTANA;
            estrellasY[i]      = rng.nextDouble() * ALTO_VENTANA;
            estrellasBrillo[i] = rng.nextDouble() * Math.PI * 2;
        }
    }

    private void iniciarNuevaPartida() {
        jugador     = new Jugador(ANCHO_VENTANA / 2.0 - 18, ALTO_VENTANA - 140);
        proyectiles = new ArrayList<>();
        gestorMapa  = new GestorMapa();
        puntuacion  = 0;
        tiempoAcumulado = 0;
        estado      = EstadoJuego.JUGANDO;
    }

    private void configurarVentana(Stage ventana) {
        Group root = new Group();
        root.getChildren().add(canvas);
        escena = new Scene(root, ANCHO_VENTANA, ALTO_VENTANA);
        escena.setFill(Color.BLACK);
        ventana.setScene(escena);
        ventana.setTitle("Sound Rush - POO I-2026");
        ventana.setResizable(false);
    }

    private void gestionarEventos() {
        escena.setOnKeyPressed(this::alPresionarTecla);
        escena.setOnKeyReleased(this::alSoltarTecla);
    }

    private void alPresionarTecla(KeyEvent e) {
        switch (e.getCode()) {
            case UP, W    -> teclaArriba    = true;
            case DOWN     -> teclaAbajo     = true;
            // S: en juego mueve hacia abajo; en menús navega a puntuaciones
            case S        -> { teclaAbajo = true; teclaS = true; }
            case LEFT, A  -> teclaIzquierda = true;
            case RIGHT, D -> teclaDerecha   = true;
            case SPACE    -> teclaEspacio   = true;
            case ENTER    -> teclaEnter     = true;
            case R        -> teclaR         = true;
            default       -> {}
        }
    }

    private void alSoltarTecla(KeyEvent e) {
        switch (e.getCode()) {
            case UP, W    -> teclaArriba    = false;
            case DOWN     -> teclaAbajo     = false;
            case S        -> { teclaAbajo = false; teclaS = false; }
            case LEFT, A  -> teclaIzquierda = false;
            case RIGHT, D -> teclaDerecha   = false;
            case SPACE    -> teclaEspacio   = false;
            case ENTER    -> teclaEnter     = false;
            case R        -> teclaR         = false;
            default       -> {}
        }
    }

    private void iniciarCicloJuego() {
        tiempoUltimoFrame = System.nanoTime();
        ciclo = new AnimationTimer() {
            @Override
            public void handle(long ahora) {
                double dt = (ahora - tiempoUltimoFrame) / 1_000_000_000.0;
                tiempoUltimoFrame = ahora;
                if (dt > 0.05) dt = 0.05;
                tiempoAcumulado += dt;
                actualizar(dt);
                pintar();
            }
        };
        ciclo.start();
    }

    private void actualizar(double dt) {
        tiempoFondo += dt;
        for (int i = 0; i < NUM_PARTICULAS; i++) {
            particulasY[i] -= particulasVel[i];
            if (particulasY[i] < -30) {
                particulasY[i] = ALTO_VENTANA + 10;
                particulasX[i] = Math.random() * ANCHO_VENTANA;
            }
        }
        for (int i = 0; i < NUM_ONDAS; i++)    ondaFase[i]       += 0.04;
        for (int i = 0; i < NUM_ESTRELLAS; i++) estrellasBrillo[i] += 0.03;

        switch (estado) {
            case TITULO    -> actualizarTitulo();
            case JUGANDO   -> actualizarJuego(dt);
            case GAME_OVER -> actualizarGameOver();
            case SCORES    -> actualizarScores();
        }
    }

    private void actualizarTitulo() {
        if (teclaEnter) { teclaEnter = false; iniciarNuevaPartida(); }
        if (teclaS)     { teclaS     = false; estado = EstadoJuego.SCORES; }
    }

    private void actualizarGameOver() {
        if (teclaR)     { teclaR     = false; iniciarNuevaPartida(); }
        if (teclaS)     { teclaS     = false; estado = EstadoJuego.SCORES; }
        if (teclaEnter) { teclaEnter = false; estado = EstadoJuego.TITULO; }
    }

    private void actualizarScores() {
        if (teclaEnter) { teclaEnter = false; estado = EstadoJuego.TITULO; }
    }

    private void actualizarJuego(double dt) {
        jugador.actualizar();
        jugador.actualizarTiempo(dt);

        if (teclaEspacio && jugador.puedisparar()) {
            proyectiles.add(jugador.disparar());
        }

        for (Proyectil p : proyectiles) p.actualizar();
        proyectiles.removeIf(p -> !p.isActivo());

        // ── Scroll corregido: NO empujar al jugador hacia abajo ───────────────
        // Calculamos vel del jugador (aproximada por input)
        double velJugadorActual = (App.teclaArriba) ?
            (jugador.isBoostVelocidadActivo() ? 6.5 : 3.2) : 0;
        double velScroll = gestorMapa.calcularScroll(jugador.getY(), velJugadorActual);
        gestorMapa.aplicarScroll(velScroll);
        // Solo mover al jugador si el scroll es causado por la inercia del mundo,
        // NO cuando el jugador está activamente presionando arriba (ya se movió)
        if (velScroll > 0 && !App.teclaArriba) {
            jugador.setY(jugador.getY() + velScroll);
        } else if (velScroll > 0 && App.teclaArriba) {
            // Anclar: si el jugador está por encima de la línea, fijarlo a ella
            double lineaScroll = App.ALTO_VENTANA * 0.38;
            if (jugador.getY() < lineaScroll) jugador.setY(lineaScroll);
        }

        for (Enemigo enemigo : gestorMapa.getEnemigos()) {
            enemigo.actualizarTiempo(dt);
            if (enemigo instanceof com.unah.soundrush.enemigos.Silenciador s) {
                s.actualizarConJugador(jugador.getX(), jugador.getY());
            } else if (enemigo instanceof VientoOpresor v) {
                v.actualizarConJugador(jugador.getX(), jugador.getY());
            } else if (enemigo instanceof Censor c) {
                c.actualizarConJugador(jugador.getX(), jugador.getY(), dt);
                if (c.puedeDisparar()) {
                    gestorMapa.agregarProyectilEnemigo(
                        c.dispararHacia(jugador.getX() + 18, jugador.getY() + 18));
                }
            } else if (enemigo instanceof com.unah.soundrush.enemigos.BurbujaShhh b) {
                b.actualizarConJugador(jugador.getX(), jugador.getY());
                b.actualizarConDelta(dt);
            }
        }

        for (Censor.ProyectilEnemigo pe : gestorMapa.getProyectilesEnemigos()) pe.actualizar();
        for (ItemBonus item : gestorMapa.getItems()) item.actualizar();

        verificarColisiones();

        if (velScroll > 0) puntuacion += (int)(velScroll * 0.5);
        if (jugador.getVidas() <= 0) terminarJuego();
    }

    private void verificarColisiones() {
        List<Proyectil> aEliminar = new ArrayList<>();
        for (Proyectil p : proyectiles) {
            for (Enemigo e : gestorMapa.getEnemigos()) {
                if (!e.isActivo()) continue;
                if (p.obtenerRectangulo().getBoundsInParent()
                     .intersects(e.obtenerRectangulo().getBoundsInParent())) {
                    // PowerShot: daño masivo (mata siempre)
                    int danio = p.isPowerShot() ? 99 : 1;
                    e.recibirDanio(danio);
                    // PowerShot NO se consume — atraviesa
                    if (!p.isPowerShot()) {
                        p.setActivo(false);
                        aEliminar.add(p);
                    }
                    if (!e.isActivo()) puntuacion += e.getPuntosAlMorir();
                    if (!p.isPowerShot()) break;
                }
            }
        }
        proyectiles.removeAll(aEliminar);

        for (Enemigo e : gestorMapa.getEnemigos()) {
            if (!e.isActivo()) continue;
            if (jugador.colisionaCon(e)) { jugador.recibirDanio(); break; }
        }

        for (Censor.ProyectilEnemigo pe : gestorMapa.getProyectilesEnemigos()) {
            if (!pe.activo) continue;
            if (jugador.obtenerRectangulo().getBoundsInParent()
                       .intersects(pe.obtenerRectangulo().getBoundsInParent())) {
                jugador.recibirDanio(); pe.activo = false;
            }
        }

        for (Obstaculo o : gestorMapa.getObstaculos()) {
            if (!o.isActivo()) continue;
            if (jugador.colisionaCon(o)) { jugador.recibirDanio(); break; }
        }

        for (ItemBonus item : gestorMapa.getItems()) {
            if (item.isCapturado() || !item.isActivo()) continue;
            if (jugador.colisionaCon(item)) {
                item.setCapturado(true);
                aplicarEfectoItem(item);
            }
        }
    }

    private void aplicarEfectoItem(ItemBonus item) {
        switch (item.getTipo()) {
            case PENTAGRAMA      -> jugador.setVidas(Math.min(jugador.getVidas() + 1, 5));
            case AMPLIFICADOR    -> puntuacion += 500;
            case AURICULAR       -> jugador.activarEscudo();
            case BOOST_VELOCIDAD -> jugador.activarBoostVelocidad();
            case BOOST_DISPARO   -> jugador.activarBoostDisparo();
        }
    }

    private void terminarJuego() {
        estado = EstadoJuego.GAME_OVER;
        if (gestorPuntuaciones.esNuevoRecord(puntuacion)) {
            int pts = puntuacion;
            Platform.runLater(() -> {
                TextInputDialog dialogo = new TextInputDialog("Jugador");
                dialogo.setTitle("Nuevo Record!");
                dialogo.setHeaderText("Puntuacion: " + pts);
                dialogo.setContentText("Ingresa tu nombre:");
                Optional<String> resultado = dialogo.showAndWait();
                resultado.ifPresent(nombre -> {
                    if (!nombre.isBlank())
                        gestorPuntuaciones.agregarPuntuacion(nombre.trim(), pts);
                });
            });
        }
    }

    // ── Pintado ───────────────────────────────────────────────────────────────

    private void pintar() {
        pintarFondoMusical();
        switch (estado) {
            case TITULO    -> hud.pintarPantallaTitulo(gc);
            case JUGANDO   -> pintarJuego();
            case GAME_OVER -> pintarGameOver();
            case SCORES    -> hud.pintarPantallaScores(gc, gestorPuntuaciones);
        }
    }

    private void pintarFondoMusical() {
        double t    = tiempoFondo;
        double dist = (gestorMapa != null) ? gestorMapa.getDistanciaRecorrida() : 0;

        // ── Fase de color según distancia ─────────────────────────────────────
        double faseDist  = (dist / 800.0) % 6.0;
        int    faseIdx   = (int) faseDist;
        double faseBlend = faseDist - faseIdx;
        double blend     = faseBlend * faseBlend * (3 - 2 * faseBlend);

        // Paletas [fondo, neón principal, neón secundario]
        String[][] paletas = {
            {"#011828", "#00C8FF", "#0060C0"},   // 0: azul-cian profundo (como ref)
            {"#0A0118", "#A020F0", "#4000C0"},   // 1: violeta
            {"#011408", "#00EE88", "#006040"},   // 2: verde
            {"#120A00", "#FFB400", "#804000"},   // 3: dorado
            {"#120004", "#FF2060", "#800020"},   // 4: carmesí
            {"#100012", "#FF00C0", "#600080"},   // 5: magenta
        };

        Color fondoA = Color.web(paletas[faseIdx % 6][0]);
        Color fondoB = Color.web(paletas[(faseIdx + 1) % 6][0]);
        Color neonA  = Color.web(paletas[faseIdx % 6][1]);
        Color neonB  = Color.web(paletas[(faseIdx + 1) % 6][1]);
        Color neo2A  = Color.web(paletas[faseIdx % 6][2]);
        Color neo2B  = Color.web(paletas[(faseIdx + 1) % 6][2]);

        Color colorFondo = fondoA.interpolate(fondoB, blend);
        Color colorNeon  = neonA.interpolate(neonB, blend);
        Color colorNeon2 = neo2A.interpolate(neo2B, blend);

        String hexNeon  = toHex(colorNeon);
        String hexNeon2 = toHex(colorNeon2);

        // ── 1. Fondo base: gradiente azul-marino muy oscuro ───────────────────
        // Izquierda más oscura, ligeramente más clara hacia la derecha (como referencia)
        LinearGradient fondoBase = new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0,   colorFondo.darker().darker()),
            new Stop(0.45,colorFondo.darker()),
            new Stop(0.7, colorFondo),
            new Stop(1,   colorFondo.darker())
        );
        gc.setFill(fondoBase);
        gc.fillRect(0, 0, ANCHO_VENTANA, ALTO_VENTANA);

        // ── 2. Bokeh SOLO en zona izquierda (< 55% del ancho) ─────────────────
        // Grandes círculos de luz difusa, como en la imagen de referencia
        double[][] bokeh = {
            { 80,  220, 130, 0.0},   // grande izq-superior
            {260,  380, 105, 1.6},   // mediano centro-izq
            {160,  520,  85, 3.0},   // mediano izq-inferior
            { 40,  460,  70, 4.5},   // pequeño izq borde
            {380,  180,  60, 2.2},   // pequeño centro (transición)
        };
        for (double[] b : bokeh) {
            double bx   = b[0];
            double by   = b[1];
            double br   = b[2] * (0.88 + Math.sin(t * 0.4 + b[3]) * 0.12);
            double ba   = 0.06 + Math.abs(Math.sin(t * 0.3 + b[3])) * 0.07;
            RadialGradient rg = new RadialGradient(
                0, 0, bx, by, br, false, CycleMethod.NO_CYCLE,
                new Stop(0,   Color.web(hexNeon, ba * 1.4)),
                new Stop(0.45,Color.web(hexNeon, ba * 0.5)),
                new Stop(1,   Color.web(hexNeon, 0.0))
            );
            gc.setFill(rg);
            gc.fillOval(bx - br, by - br, br * 2, br * 2);
        }

        // ── 3. Stream de notas en zona DERECHA (referencia exacta) ────────────
        // Una corriente principal y una secundaria, ambas en el lado derecho
        pintarStreamDerecho(gc, 0.75, t,       hexNeon,  hexNeon2, 1.0);  // stream principal
        pintarStreamDerecho(gc, 0.62, t + 0.9, hexNeon2, hexNeon,  0.55); // stream secundario

        // ── 4. Partículas de notas SOLO en zona izquierda ─────────────────────
        for (int i = 0; i < NUM_PARTICULAS; i++) {
            if (particulasX[i] > ANCHO_VENTANA * 0.50) continue;
            double pulso  = particulasAlpha[i] * (0.35 + 0.35 * Math.sin(t * 1.6 + i * 0.8));
            double desv   = Math.sin(t * 0.7 + i) * 4;
            gc.setFont(i % 6 == 0 ? F_PART_G : F_PART_S);
            gc.setFill(Color.web(hexNeon, pulso * 0.20));
            gc.fillText(String.valueOf(particulasSimbolo[i]),
                        particulasX[i] + desv - 1, particulasY[i] + 1);
            gc.setFill(Color.web(hexNeon, pulso * 0.70));
            gc.fillText(String.valueOf(particulasSimbolo[i]),
                        particulasX[i] + desv, particulasY[i]);
        }

        // ── 5. Estrellas de luz: solo en zona izquierda/centro ────────────────
        for (int i = 0; i < NUM_ESTRELLAS; i++) {
            if (estrellasX[i] > ANCHO_VENTANA * 0.52) continue;
            double b = 0.04 + Math.abs(Math.sin(estrellasBrillo[i])) * 0.14;
            double r = 0.6  + Math.abs(Math.sin(estrellasBrillo[i] * 0.7)) * 1.4;
            if (b > 0.14) {
                gc.setStroke(Color.web(hexNeon, b * 0.35)); gc.setLineWidth(0.4);
                gc.strokeLine(estrellasX[i] - r*2.5, estrellasY[i],
                              estrellasX[i] + r*2.5, estrellasY[i]);
                gc.strokeLine(estrellasX[i], estrellasY[i] - r*2.5,
                              estrellasX[i], estrellasY[i] + r*2.5);
            }
            gc.setFill(Color.web(hexNeon, b));
            gc.fillOval(estrellasX[i] - r, estrellasY[i] - r, r*2, r*2);
        }

        // ── 6. Pentagrama: SOLO en zona derecha, tenue ────────────────────────
        int espP = 90;
        for (int blk = -2; blk < ALTO_VENTANA / espP + 3; blk++) {
            double scrollY = (t * 18) % espP;
            double baseY   = blk * espP + scrollY;
            for (int ln = 0; ln < 5; ln++) {
                double ly = baseY + ln * 9;
                // Solo en zona derecha (55% → 100%)
                gc.setStroke(Color.web(hexNeon, 0.055));
                gc.setLineWidth(0.6);
                gc.strokeLine(ANCHO_VENTANA * 0.52, ly, ANCHO_VENTANA, ly);
            }
        }

        // ── 7. Viñeta: oscurece los cuatro bordes ─────────────────────────────
        RadialGradient vineta = new RadialGradient(
            0, 0, ANCHO_VENTANA * 0.5, ALTO_VENTANA * 0.5,
            Math.max(ANCHO_VENTANA, ALTO_VENTANA) * 0.68,
            false, CycleMethod.NO_CYCLE,
            new Stop(0,   Color.web("#000000", 0.0)),
            new Stop(0.50,Color.web("#000000", 0.0)),
            new Stop(1.0, Color.web("#000000", 0.78))
        );
        gc.setFill(vineta);
        gc.fillRect(0, 0, ANCHO_VENTANA, ALTO_VENTANA);
    }

    /**
     * Pinta el stream vertical de notas en la zona derecha,
     * imitando la imagen de referencia: curvas suaves de arriba a abajo,
     * notas brillantes sobre líneas delgadas curvas.
     */
    private void pintarStreamDerecho(GraphicsContext gc, double xBase, double t,
                                     String hexNeon, String hexNeon2, double intens) {
        char[] notas = {'\u266A', '\u266B', '\u266C', '\u2669', '\u266F',
                        '\u266D', '\u266A', '\u266B'};
        int N = 22;

        // Amplitud de ondulación — estrecha, se mueve suavemente
        double amp = ANCHO_VENTANA * 0.07;

        // Pre-calcular posiciones para dibujar líneas primero
        double[] posX = new double[N];
        double[] posY = new double[N];
        for (int i = 0; i < N; i++) {
            double prog  = (i / (double)(N - 1) + t * 0.10) % 1.0;
            posY[i] = prog * (ALTO_VENTANA + 50) - 25;
            posX[i] = ANCHO_VENTANA * xBase
                    + Math.sin(prog * Math.PI * 2.8 + t * 0.5) * amp
                    + Math.sin(prog * Math.PI * 5.2 + t * 0.3) * amp * 0.3;
        }

        // Líneas curvas conectoras (el pentagrama ondulado de la referencia)
        // Dos líneas paralelas a poca distancia = efecto de partitura
        for (int offset = -4; offset <= 4; offset += 4) {
            gc.setStroke(Color.web(hexNeon2, intens * 0.18));
            gc.setLineWidth(0.7);
            for (int i = 0; i < N - 1; i++) {
                gc.strokeLine(posX[i] + offset, posY[i],
                              posX[i+1] + offset, posY[i+1]);
            }
        }

        // Notas musicales sobre las líneas
        for (int i = 0; i < N; i++) {
            double prog  = (i / (double)(N - 1) + t * 0.10) % 1.0;
            double fade  = Math.sin(prog * Math.PI);
            double alpha = intens * (0.45 + fade * 0.45);

            boolean grande = (i % 5 == 0);
            gc.setFont(grande ? F_STREAM_G : F_STREAM);
            char nota = notas[i % notas.length];

            // Halo de glow (capa ancha difusa)
            gc.setFill(Color.web(hexNeon, alpha * 0.30));
            gc.fillText(String.valueOf(nota), posX[i] - 3, posY[i] + 3);

            // Nota principal brillante
            gc.setFill(Color.web(hexNeon, alpha * 0.90));
            gc.fillText(String.valueOf(nota), posX[i], posY[i]);

            // Brillo blanco en el interior
            gc.setFill(Color.web("#FFFFFF", alpha * 0.35));
            gc.fillText(String.valueOf(nota), posX[i], posY[i]);
        }
    }

    /** Convierte un Color JavaFX a string hex #RRGGBB */
    private String toHex(Color c) {
        return String.format("#%02X%02X%02X",
            (int)(c.getRed()   * 255),
            (int)(c.getGreen() * 255),
            (int)(c.getBlue()  * 255));
    }

    private void pintarJuego() {
        for (Tile t                      : gestorMapa.getTiles())               t.pintar(gc);
        for (ItemBonus it                : gestorMapa.getItems())               it.pintar(gc);
        for (Obstaculo o                 : gestorMapa.getObstaculos())          o.pintar(gc);
        for (Enemigo e                   : gestorMapa.getEnemigos())            e.pintar(gc);
        for (Censor.ProyectilEnemigo pe  : gestorMapa.getProyectilesEnemigos()) pe.pintar(gc);
        for (Proyectil p                 : proyectiles)                         p.pintar(gc);
        jugador.pintar(gc);
        hud.pintar(gc, jugador, puntuacion, gestorMapa.getDistanciaRecorrida());
    }

    private void pintarGameOver() {
        pintarJuego();
        hud.pintarGameOver(gc, puntuacion, gestorMapa.getDistanciaRecorrida());
    }
}
