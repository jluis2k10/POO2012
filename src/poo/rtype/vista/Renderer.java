package poo.rtype.vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

import poo.rtype.modelo.Player;
import poo.rtype.controlador.Game;
import poo.rtype.controlador.ImageLoader;
import poo.rtype.modelo.interfaces.EffectIF;
import poo.rtype.modelo.interfaces.EntityIF;

/**
 * Esta clase dibujará en el búfer para que luego éste pueda ser
 * a su vez dibujado en el canvas.
 * <pre>
 * Rebderer render = new Renderer(game);
 * render.render();
 * </pre>
 * @author José Luis Pérez González
 */
public class Renderer {
    /** Instancia del controlador principal del juego */
    private Game game;
    /** Mensaje a mostrar en la pantalla de Game Over */
    private String notifyMessage = "";
    /** Texto con el nivel de dificultad selecciondo */
    private String gameMode = "";
    /** Fondo de la pantalla de juego. */
    private Image background;
    /** Posición horizontal del fondo en la pantalla */
    private double bgPos = 0;

    /** Diferentes tipos de fuentes que usaremos. */
    private Font title = new Font("Serif", Font.BOLD | Font.ITALIC, 48);
    private Font titleGO = new Font("Helvetica", Font.BOLD, 48);
    private Font options = new Font("Helvetica", Font.PLAIN, 18);
    private Font scores = new Font("Monospaced", Font.BOLD, 16);
    private Font info = new Font("Monospaced", Font.PLAIN, 12);
    /** Métrica de la fuente que se esté utilizando */
    private FontMetrics metr;

    /**
     * Constructor de la clase.
     * @param game Instancia del controlador principal de juego.
     */
    public Renderer(Game game) {
        this.game = game;
        this.background = new ImageLoader().LoadImage("/poo/rtype/background.gif");
    }

    /**
     * Este método se invoca desde el controlador principal del juego cada vez
     * que se necesita repintar la pantalla. Pinta el fondo de la misma y luego,
     * dependiendo de la fase de juego en la que estemos, pintará la pantalla
     * que corresponda:
     * <ul>
     * <li>Pantalla de selección: {@link #drawInSelection(Graphics2D)}</li>
     * <li>Pantalla de juego: {@link #drawInGame(Graphics2D, ArrayList, ArrayList, long)}</li>
     * <li>Pantalla de Game Over: {@link #drawGameOver(Graphics2D)}</li>
     * </ul>
     * @param g Objeto Graphics que encapsula la información necesaria para el pintado.
     * @param entities Contenedor con las diferentes entidades activas del juego.
     * @param effects Contenedor con los diferentes efectos especiales activos del juego.
     */
    public void render(Graphics2D g, ArrayList<EntityIF> entities, ArrayList<EffectIF> effects, long delta) {
        // Fondo degradado.
        Color color1 = new Color(100,100,100);
        Color color2 = Color.BLACK;
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, Game.P_HEIGHT - 50, color2);
        g.setPaint(gp);
        g.fillRect(0, 0, Game.P_WIDTH, Game.P_HEIGHT);

        if(game.inselection)
            drawInSelection(g);
        else if(game.ingame)
            drawInGame(g, entities, effects, delta);
        else
            drawGameOver(g);
    }

    /**
     * Actualizar el texto que debe aparecer en la pantalla de Game Over.
     * @param notifyMessage El texto que debe aparecer.
     */
    public void setNotifyMessage(String notifyMessage) {
        this.notifyMessage = notifyMessage;
    }

    /**
     * Actaulizar el texto con el modo de juego seleccionado.
     * Usando esto no nos vemos obligados a buscar el modo con
     * cada refresco de la pantalla.
     * @param gameMode El texto con el modo de juego seleccionado.
     */
    public void setGameModeText(String gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * Dibujar la pantalla inicial del juego, con la selección de nivel.
     * @param g Objeto Graphics2D que encapsula la información necesaria para el pintado.
     */
    private void drawInSelection(Graphics2D g) {

        String text;

        // Título
        g.setColor(Color.BLACK);
        g.setFont(title);
        metr = g.getFontMetrics();
        text = "R-Type";
        g.drawString(text, (Game.P_WIDTH - metr.stringWidth(text)) / 2 + 2, 112);
        g.setColor(Color.WHITE);
        g.drawString(text, (Game.P_WIDTH - metr.stringWidth(text)) / 2, 110);

        //Subtítulo
        g.setFont(info);
        metr = g.getFontMetrics();
        text = "Práctica de Programación orientada a objetos - 2013";
        g.drawString(text, (Game.P_WIDTH - metr.stringWidth(text)) / 2, 140);

        // Selección de dificultad
        g.setFont(options);
        metr = g.getFontMetrics();
        text = "Selecciona el nivel de dificultad:";
        g.drawString(text, (Game.P_WIDTH - metr.stringWidth(text)) / 2, 190);
        text = "(1)   Fácil";
        g.drawString(text, 330, 230);
        text = "(2)   Normal";
        g.drawString(text, 330, 255);
        text = "(3)   Complicado";
        g.drawString(text, 330, 280);
        text = "(4)   Imposible";
        g.drawString(text, 330, 305);

        // Info alumno
        g.setFont(info);
        metr = g.getFontMetrics();
        text = "José Luis Pérez González";
        g.drawString(text, Game.P_WIDTH - metr.stringWidth(text) - 15, 380);
        text = "DNI: 43127876R";
        g.drawString(text, Game.P_WIDTH - metr.stringWidth(text) - 15, 400);
    }

    /**
     * Dibujar la pantalla del juego propiamente dicha, con todos los objetos
     * que la forman.
     * @param g Objeto Graphics2D que encapsula la información necesaria para el pintado.
     * @param entities Contenedor con las entidades activas del juego.
     * @param effects Contenedor con los efectos activos del juego.
     */
    private void drawInGame(Graphics2D g, ArrayList<EntityIF> entities, ArrayList<EffectIF> effects, long delta) {

        String text;

        // Mover y dibujar el fondo
        if(bgPos + background.getWidth(null) < 0)
            bgPos = 0;
        bgPos += (delta * -25d)/1000; // El fondo se mueve a 25 píxels/segundo hacia la izquierda de la pantalla
        g.drawImage(background, (int)Math.round(bgPos), 0, null);
        if(bgPos + background.getWidth(null) < Game.P_WIDTH)
            g.drawImage(background, (int)Math.round(bgPos) + background.getWidth(null), 0, null);

        // Dibujar los efectos.
        for(EffectIF effect: effects) {
            effect.Draw(g);
        }

        // Dibujar entidades.
        for(EntityIF entity: entities) {
            entity.Draw(g);
            if(entity instanceof Player) {
                ArrayList<EntityIF> missiles = ((Player) entity).getMissiles();
                for(EntityIF m: missiles) {
                    m.Draw(g);
                }
            }
        }

        // Información 'relevante' que se dibuja en la zona superior de la pantalla.
        // Enemigos restantes, Nivel seleccionado y FPS.
        g.setFont(info);
        g.setColor(Color.WHITE);
        metr = g.getFontMetrics();

        text = "Naves enemigas:";
        int lastWidth = metr.stringWidth(text)+5;
        g.drawString(text, 5, 15);
        g.setColor(Color.GREEN);
        text = Integer.toString(Game.TOTAL_ENEMIES).trim();
        g.drawString(text, lastWidth+5, 15);
        lastWidth = lastWidth + 5 + metr.stringWidth(text);
        g.setColor(Color.WHITE);

        text = " Dificultad:";
        g.drawString(text, lastWidth+5, 15);
        lastWidth = lastWidth + 5 + metr.stringWidth(text);
        g.setColor(Color.GREEN);
        g.drawString(gameMode, lastWidth+5, 15);
        g.setColor(Color.WHITE);

        text = "FPS:";
        g.drawString(text, Game.P_WIDTH - 60, 15);
        g.setColor(Color.GREEN);
        g.drawString(Integer.toString(Game.CURRENT_FPS), Game.P_WIDTH -25, 15);
    }

    /**
     * Dibujar la pantalla de Game Over con el mensaje apropiado y con las opciones
     * de si seguir jugando o no.
     * @param g Objeto Graphics2D que encapsula la información necesaria para el pintado.
     */
    private void drawGameOver(Graphics2D g) {

        String text;

        // You Win o Game Over
        g.setColor(Color.WHITE);
        g.setFont(titleGO);
        metr = g.getFontMetrics();
        g.drawString(notifyMessage, (Game.P_WIDTH - metr.stringWidth(notifyMessage)) / 2, 50);

        // Puntuaciones
        g.setFont(scores);
        metr = g.getFontMetrics();
        int vPos = 90;
        boolean printMyScore = true;
        for(int i = 1; i < game.finalScore.length; i++) {
            g.setColor(Color.WHITE);
            if(game.finalScore[0] == game.finalScore[i] && printMyScore) {
                g.setColor(Color.YELLOW);
                printMyScore = false;
            }
            text = String.format("%02d", i) + ".  " + String.format("%06d", (int)game.finalScore[i]);
            g.drawString(text, (Game.P_WIDTH - metr.stringWidth(text)) / 2, vPos);
            vPos += 20;
        }
        text = "Mi puntuación: " + String.format("%05d", (int)game.finalScore[0]);
        g.drawString(text, (Game.P_WIDTH - metr.stringWidth(text)) / 2, vPos + 10);

        // Opciones
        g.setFont(options);
        metr = g.getFontMetrics();
        text = "¿Quieres jugar de nuevo?";
        g.drawString(text, (Game.P_WIDTH - metr.stringWidth(text)) / 2, 350);
        text = "(S) Sí     (N) No";
        g.drawString(text, (Game.P_WIDTH - metr.stringWidth(text)) / 2, 380);
    }
}
