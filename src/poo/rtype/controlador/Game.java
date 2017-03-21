package poo.rtype.controlador;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import poo.rtype.modelo.*;
import poo.rtype.modelo.interfaces.*;
import poo.rtype.start.GameLauncher;
import poo.rtype.vista.Renderer;

@SuppressWarnings("serial")
public class Game extends Canvas {

    /** Ancho del panel contenedor del juego */
    public static final int P_WIDTH = 800;
    /** Altura del panel contenedor del juego */
    public static final int P_HEIGHT = 420;
    /** Indica si estamos en modo DEBUG o no */
    public static final boolean DEBUG = true;
    /** El número de frames por segundo que se muestra en pantalla */
    public static int CURRENT_FPS;
    /** Indicador del total de naves enemigas restantes */
    public static int TOTAL_ENEMIES;
    /** Indicador del total de explosiones activas en este momento */
    public static int CURRENT_EXPLOSIONS = 0;
    /** True si estamos en la pantalla de juego propiamente dicha, con las naves etc. */
    public boolean ingame;
    /** True si estamos en la pantalla de bienvenida/selección de nivel */
    public boolean inselection;
    /** Array con puntuaciones finales que contiene tanto la actual como las que se leen del registro histórico (scores.txt) */
    public double[] finalScore = new double[11];

    /** Tiempo en ms de cada 'tick' del timer. Si todo va bien, el juego tendrá unos FPS de 1000/period */
    private static final int period = 12;
    /** Velocidad base a la que se mueven las naves en pixels/s */
    private static final double moveSpeed = 150;

    /** Indicador del modo de juego */
    private int GAME_MODE;
    /** La última vez que se actualizón el framerate */
    private long lastFpsTime;
    /** El número de frames transcurridos hasta ahora */
    private int framesTillNow;
    /** En qué momento se pasó por última vez por el 'loop' principal del juego */
    private long lastLoopTime;
    /** Dibujaremos en el canvas mediante triple búfer */
    private BufferStrategy buffer = null;
    /** Generador de números aleatorios (para que las naves enemigas y la estrellas no salgan siempre en las mismas posiciones) */
    private Random randGenerator = new Random();
    /** Puntuación parcial obtenida que se basa en la cantidad total de enemigos eliminados */
    public int partialScore;

    /** Instancia de la clase Stats para mostrar diferentes estadísticas mientras corre el juego. */
    private Stats stats;
    /** Representa la Vista del patrón MVC. Dibuja lo que se necesite en cada momento */
    private Renderer render;
    /** Controlador de las entradas del teclado */
    private InputController input;
    /** Controlador de la posición en la ventana de los diferentes objetos */
    private MovementController movement;
    /** Controlador para manejar las colisiones entre objetos */
    private CollisionsController collisions;
    /** Contenedor de entidades que pueden 'interaccionar' (jugador, enemigos y misiles */
    private ArrayList<EntityIF> entities;
    /** Contenedor de efectos, entidades que no interaccionan con ninguna otra (estrellas del fondo y explosiones) */
    private ArrayList<EffectIF> effects;
    /** Instancia del objeto que representa al jugador, estará a su vez contenida en 'entities' pero tenerla accesible
     * por separado facilitará ciertas operaciones */
    private Player player;

    /** Timer para actualizaciones del estado del juego */
    private Timer timer;
    /** Clase que contiene el método run() que será invocado en cada 'tic' del Timer */
    private TimerTask gameLoop;

    /**
     * Constructor del juego.
     * @param gameLauncher Instancia del lanzador del juego.
     */
    public Game(GameLauncher gameLauncher) {
        // Iniciar el timer.
        timer = new Timer();

        input = new InputController(this);
        collisions = new CollisionsController(this);
        movement = new MovementController();
        render = new Renderer(this);
        if(DEBUG) stats = new Stats(period);

        entities = new ArrayList<EntityIF>();
        effects = new ArrayList<EffectIF>();

        inselection = true;
        ingame = false;

        // Obtener el panel de la ventana contenedor (GameLauncher) y añadirle el Canvas,
        // que es esta misma clase y será donde se dibujará.
        JPanel panel = (JPanel) gameLauncher.getContentPane();
        panel.setPreferredSize(new Dimension(P_WIDTH,P_HEIGHT));
        panel.setLayout(null);
        setBounds(0,0,P_WIDTH,P_HEIGHT);
        panel.add(this);
        // No es necesario el repintado automático puesto que lo haremos nosotros
        setIgnoreRepaint(true);
        // Utilizamos KeyListener en lugar de los KeyBindings de la propia swing, es necesario poner el
        // 'foco' en éste canvas.
        setFocusable(true);
        requestFocus();
    }

    /**
     * Crear la estrategia que tendrá el búfer (doble búfer) e iniciar
     * el Timer que marcará los tiempos de actualización de la lógica y
     * el renderizado del juego.
     */
    public void createBufferAndStart() {
        this.createBufferStrategy(2);
        this.buffer = getBufferStrategy();

        if(gameLoop != null)
            gameLoop.cancel();

        gameLoop = new GameLoop();
        timer.scheduleAtFixedRate(gameLoop, 0, period);
    }

    /**
     * Este método es llamado cuando se elije un nivel y empieza
     * el juego propiamente dicho.
     * Actualiza el nivel del juego seleccionado.
     */
    private void startGame() {
        entities.clear();
        effects.clear();

        GAME_MODE = input.gameMode.getGameMode();
        switch (GAME_MODE) {
            case 1:
                TOTAL_ENEMIES = 10;
                render.setGameModeText("Fácil");
                break;
            case 2:
                TOTAL_ENEMIES = 15;
                render.setGameModeText("Normal");
                break;
            case 3:
                TOTAL_ENEMIES = 20;
                render.setGameModeText("Complicado");
                break;
            case 4:
                TOTAL_ENEMIES = 30;
                render.setGameModeText("Imposible");
                break;
        }

        input.gameMode.typed(false);
        input.restart.typed(false);

        initObjects();
        inselection = false;
        ingame = true;
        partialScore = 0;
        lastLoopTime = System.currentTimeMillis();
    }

    /**
     * Inicializamos diferentes elementos que se mostrarán en la pantalla con su posición
     * inicial y la velocidad que les corresponda.
     * <p>
     * El tipo de nave enemiga, así como sus respectivas coordenadas iniciales
     * sobre la pantalla, se generan aleatoriamente.
     * </p>
     */
    private void initObjects() {
        // Inicializar Jugador y añadirlo al contenedor de entidades.
        player = new Player((int)moveSpeed, input);
        entities.add(player);

        // Inicializar Enemigos y añadirlos al contenedor de entidades.
        int[][] pos = makeEnemyPositions(); // Posiciones iniciales de las naves enemigas.
        byte enemyType;
        for (int i = 0; i<pos.length; i++) {
            enemyType = (byte)randGenerator.nextInt(2); // ¿De qué tipo es la nave enemiga?
            if (enemyType == 0) {
                entities.add(new Enemy(pos[i][0], pos[i][1], "/poo/rtype/enemyB.png", "/poo/rtype/enemy_mask.png", enemyType, (double)(moveSpeed * GAME_MODE)/2));
            }
            else {
                entities.add(new Enemy(pos[i][0], pos[i][1], "/poo/rtype/enemyA.png", "/poo/rtype/enemy_mask.png", enemyType, (double)(moveSpeed * GAME_MODE)/2));
            }
        }

        // Añadir estrellas al contenedor de efectos (50 estrellas)
        for (int i = 0; i<50; i++) {
            effects.add(new Star(randGenerator.nextInt(P_WIDTH), randGenerator.nextInt(P_HEIGHT), -40));
        }
    }

    /**
     * Crea una matriz con las posiciones iniciales de las naves enemigas
     * fuera de la pantalla. Las posiciones son aleatorias y se comprueba
     * que no se superpongan naves entre ellas.
     * @return La matriz con las coordenadas para cada nave enemiga.
     */
    private int[][] makeEnemyPositions() {
        int[][] pos = new int[TOTAL_ENEMIES][2];
        for (int i = 0; i<TOTAL_ENEMIES; i++) {
            pos[i][0] = randGenerator.nextInt(500 * GAME_MODE + P_WIDTH) + P_WIDTH; // Posición horizontal entre WIDTH y WIDTH+2500
            pos[i][1] = randGenerator.nextInt(P_HEIGHT - 60) + 30; // Posición vertical entre 30 y HEIGHT-30

            // Comprobar que no se superpongan las naves enemigas al generar sus posiciones.
            // Cada nave tiene una 'zona de seguridad' a su alrededor en la que no puede posicionarse otra nave.
            Rectangle sz1 = new Rectangle(pos[i][0], pos[i][1], 100, 100);
            for (int j = 0; j<(i+1); j++) {
                Rectangle sz2 = new Rectangle(pos[j][0], pos[j][1], 100, 100);
                if ((sz1.intersects(sz2)) && (j != i)) {
                    i = i - 1;
                }
            }
        }
        return pos;
    }

    /**
     * Comprobar si se debe finalizar el juego, ya sea porque la nave del
     * jugador ha colisionado con una nave enemiga (Game Over) o porque
     * hemos destruido todas las naves enemigas (You Win!).
     * <p>
     * Devuelve cierto si se debe abandonar la pantalla de juego.
     * </p>
     * @return true si se debe abandonar la pantalla del juego.
     */
    private boolean checkForVictory() {
        if(!entities.contains(player) && CURRENT_EXPLOSIONS == 0) {
            render.setNotifyMessage("Game Over");
            calculateScore();
            return false;
        }
        else if(TOTAL_ENEMIES == 0 && CURRENT_EXPLOSIONS == 0) {
            render.setNotifyMessage("You Win!");
            calculateScore();
            return false;
        }
        return true;
    }

    /**
     * Calcula la puntuación final obtenida y guarda el resultado
     * en un archivo con los históricos.
     */
    private void calculateScore() {

        // Cálculo de la puntuación final de la partida.
        finalScore[0] = partialScore * (1 + GAME_MODE / 4);

        // Leer el archivo con las puntuaciones y actualizar el array finalScore en consecuencia.
        InputStream input = null;
        BufferedReader br = null;
        try {
            input = Game.class.getResourceAsStream("/poo/rtype/scores.txt");
            br = new BufferedReader(new InputStreamReader(input));

            String line;
            int index = 1;
            while((line = br.readLine()) != null) {
                finalScore[index] = Double.parseDouble(line);
                index++;
            }

            if(finalScore[0] >= finalScore[10]) {
                finalScore[10] = finalScore[0];
                for(int i = 9; i>0; i--) {
                    if(finalScore[0] >= finalScore[i]) {
                        finalScore[i+1] = finalScore[i];
                        finalScore[i] = finalScore[0];
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(br != null) br.close();
                if(input != null) input.close();
            } catch(Exception e2) {
                e2.printStackTrace();
            }
        }

        // Guardar el array generado con las puntuaciones en el archivo de registro.
        BufferedWriter bw = null;
        try {
            File file = new File(Game.class.getResource("/poo/rtype/scores.txt").toURI().getPath());
            FileWriter fw = new FileWriter(file);
            bw = new BufferedWriter(fw);

            for(int i = 1; i < finalScore.length; i++) {
                String line = Double.toString(finalScore[i]);
                bw.write(line);
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(bw != null) {
                    bw.flush();
                    bw.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * Añade una explosión como objeto EffectIF al contenedor de efectos.
     * @param x Posición de la explosión en el eje horizontal.
     * @param y Posición de la explosión en el eje vertical.
     * @param imageSource Ubicación del archivo dentro del directorio de la aplicación.
     * @param frameWidth Anchura de cada frame de la animación de la explosión.
     * @param frameHeight Altura de cada frame de la animación de la explosión.
     * @param totalFrames Número total de frames que componen la animación de la explosión.
     * @param frameTimeStep Cada cuanto tiempo (en milisegundos) debemos actualizar la animación.
     */
    public void addExplosion(int x, int y, String imageSource, int frameWidth, int frameHeight, int totalFrames, int frameTimeStep) {
        effects.add(new Explosion(x, y, imageSource, frameWidth, frameHeight, totalFrames, frameTimeStep));
        CURRENT_EXPLOSIONS++;
    }

    /**
     * Dibujar en pantalla a través del búfer.
     * @param delta El tiempo que ha pasado desde el último render.
     */
    private void render(long delta) {
        Graphics2D g2d = (Graphics2D) buffer.getDrawGraphics();
        render.render(g2d, entities, effects, delta);
        g2d.dispose();
        buffer.show();
        // Sincronizar pintado con la tasa de refresco ¿?
        Toolkit.getDefaultToolkit().sync();
        if(DEBUG) stats.reportStats();
    }

    /**
     * Con cada 'tic' del timer se invoca el método run() de esta clase.
     * Es como un bucle o loop en el que se van invocando los diferentes
     * métodos de controladores y vistas para ir actualizando el estado
     * del juego.
     * @author José Luis Pérez González
     */
    class GameLoop extends TimerTask {

        /** Tiempo que pasa en ms entre loop y loop */
        private long delta = 0;

        @Override
        public void run() {
            if(inselection && input.gameMode.wasTyped()) {
                startGame();
                return;
            }
            if(!inselection && !ingame && input.restart.wasTyped()) {
                inselection = true;
                return;
            }
            if(ingame) {
                // Las siguientes líneas actualizan el contador de frames por segundo.
                delta = System.currentTimeMillis() - lastLoopTime;
                lastLoopTime = System.currentTimeMillis();
                lastFpsTime += delta;
                framesTillNow++;
                if (lastFpsTime >= 1000) {
                    CURRENT_FPS = framesTillNow;
                    lastFpsTime = 0;
                    framesTillNow = 0;
                }

                ingame = checkForVictory(); // ¿Seguimos jugando?

                // Comprobar colisiones y mover entidades y efectos.
                entities = collisions.checkCollisions(entities);
                entities = movement.moveEntities(entities, delta);
                effects = movement.moveEffects(effects, delta);

                if(input.space.isPressed())
                    player.fire();
            }
            render(delta);
        }
    }

    /**
     * Basado en un ejemplo del libro 'Killer Game Programming in Java'.
     * Esta clase nos proporciona diversa información del estado del juego.
     * No influye para nada en el mismo. Imprime los resultados en la consola.
     * @author Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
     */
    public class Stats {

        private long MAX_STATS_INTERVAL = 1000L; // Cada segundo
        private int NUM_FPS = 10;
        private long frameCount = 0;
        private long statsInterval = 0L; // En ms
        private long prevStatsTime;
        private long totalElapsedTime = 0L;
        private int period;
        private double fpsStore[];
        private long statsCount = 0;
        private double averageFPS = 0.0;
        private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
        private DecimalFormat timedf = new DecimalFormat("0.####");  // 4 dp

        public Stats(int period) {
            this.period = period;
            this.prevStatsTime = System.currentTimeMillis();
            this.fpsStore = new double[NUM_FPS];
            for (int i=0; i < NUM_FPS; i++)
                this.fpsStore[i] = 0.0;
        }

        private void reportStats() {
            frameCount++;
            statsInterval += this.period;

            if(statsInterval > MAX_STATS_INTERVAL) {
                long timeNow = System.currentTimeMillis();

                long realElapsedTime = timeNow - prevStatsTime;
                totalElapsedTime += realElapsedTime;
                long timingError = realElapsedTime - statsInterval;

                double actualFPS = 0;
                if(totalElapsedTime > 0)
                    actualFPS = ((double)frameCount/totalElapsedTime) * 1000;

                fpsStore[ (int)statsCount%NUM_FPS ] = actualFPS;
                statsCount = statsCount+1;

                double totalFPS = 0.0;
                for(int i = 0; i<NUM_FPS; i++) {
                    totalFPS += fpsStore[i];
                }

                if(statsCount < NUM_FPS) {
                    averageFPS = totalFPS/statsCount;
                } else {
                    averageFPS = totalFPS/NUM_FPS;
                }

                System.out.println(timedf.format( (double) statsInterval/1000) + " " +
                        timedf.format((double) realElapsedTime/1000) + "s " +
                        df.format(timingError) + "% " +
                        frameCount + "frames " +
                        df.format(actualFPS) + "fps " +
                        df.format(averageFPS) + " afps" );

                prevStatsTime = timeNow;
                statsInterval = 0L;
            }
        }
    }
}
