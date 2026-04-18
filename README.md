# Sound Rush — POO I-2026

Videojuego 2D scroll vertical desarrollado en Java + JavaFX.
La música lucha por sobrevivir en un mundo que prefiere el silencio.

## Requisitos
- Java 21 LTS (Temurin)
- Maven 3.9+
- VSCode con extensión "Extension Pack for Java"

## Cómo correr

### Opción A — Maven (recomendado)
```bash
mvn javafx:run
```

### Opción B — VSCode
Abre la carpeta `soundrush` en VSCode, ve a `App.java` y presiona el botón **Run** que aparece encima de `main`.

## Controles
| Tecla | Acción |
|-------|--------|
| ↑ / W | Mover arriba |
| ↓ | Mover abajo |
| ← / A | Mover izquierda |
| → / D | Mover derecha |
| ESPACIO | Disparar onda de sonido |
| ENTER | Confirmar / Iniciar partida / Volver al menú |
| R | Reiniciar (en Game Over) |
| S | Ver puntuaciones (en Menú / Game Over) / Mover abajo (en juego) |

## Estructura del proyecto
```
soundrush/
├── pom.xml
└── src/main/java/
    ├── module-info.java
    └── com/unah/soundrush/
        ├── App.java                  ← Game loop principal
        ├── clases/
        │   ├── ObjetoJuego.java      ← Clase base abstracta
        │   ├── ElementoMovil.java    ← Hereda de ObjetoJuego
        │   ├── Jugador.java          ← Hereda de ElementoMovil
        │   ├── Enemigo.java          ← Abstracta, hereda de ElementoMovil
        │   ├── Proyectil.java        ← Hereda de ElementoMovil
        │   ├── ItemBonus.java        ← Hereda de ObjetoJuego
        │   ├── Obstaculo.java        ← Hereda de ObjetoJuego
        │   ├── Tile.java             ← Hereda de ObjetoJuego
        │   └── Animacion.java
        ├── enemigos/
        │   ├── Silenciador.java      ← Hereda de Enemigo (lento, resistente)
        │   ├── VientoOpresor.java    ← Hereda de Enemigo (zigzag, rápido)
        │   ├── BurbujaShhh.java      ← Hereda de Enemigo (burbuja que atrapa)
        │   └── Censor.java           ← Hereda de Enemigo (dispara proyectiles)
        └── ui/
            ├── GestorMapa.java       ← TileMap + scroll + generación
            ├── GestorPuntuaciones.java ← Top 10 en archivo .txt
            └── HUD.java              ← Interfaz en pantalla
```

## Base del proyecto
Desarrollado a partir del proyecto base del profesor (POO-I2026), extendiendo:
- La estructura `App.imagenes` (`HashMap<String, Image>`) y `cargarImagenes()` del patrón base
- Las clases `Jugador`, `Tile`, `Animacion` e `Item` del proyecto base
- El patrón de `AnimationTimer` y manejo de teclas con booleanos estáticos en `App`

Los sprites se colocan en `src/main/resources/com/unah/soundrush/` con extensión `.png`.
Si un sprite no existe, la entidad usa renderizado por formas como fallback.

## Conceptos POO aplicados
- **Herencia:** ObjetoJuego → ElementoMovil → Jugador / Enemigo → Silenciador / VientoOpresor / Censor
- **Clases abstractas:** ObjetoJuego, Enemigo
- **Sobrecarga de métodos:** Animacion.calcularFrame(double t) / calcularFrame(int indice)
- **Sobreescritura:** pintar(), actualizar(), mover() en cada subclase
- **Colecciones:** ArrayList para enemigos/proyectiles, HashMap para scores
- **Manejo de archivos:** GestorPuntuaciones lee/escribe puntuaciones.txt
- **Interfaces gráficas:** JavaFX Canvas, AnimationTimer, Stage/Scene
- **TileMap:** Matriz 2D de tiles con scroll vertical
