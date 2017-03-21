package poo.rtype.controlador;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Controlador para las entradas del teclado.
 *
 * @author José Luis Pérez González
 */
public class InputController implements KeyListener {
    /** Instancia del controlador principal del juego */
    private Game game;

    /**
     * Constructor de la clase.
     * @param game Instancia del controlador principal.
     */
    public InputController(Game game) {
        game.addKeyListener(this);
        this.game = game;
    }

    /**
     * Éste tipo contiene información relevante sobre la tecla que
     * se ha pulsado y se usa desde otras clases/objetos para
     * determinar qué hacer en función de la tecla que se haya pulsado
     * o dejado de pulsar.
     *
     * @author José Luis Pérez González
     */
    public class Key {
        /** Indica si la tecla presionada en éste momento */
        private boolean isPressed = false;
        /** Indica si la tecla fue 'tecleada' (pulsada y luego soltada) */
        private boolean wasTyped = false;
        /** Selector del nivel de juego según la tecla pulsada */
        private int gameMode;

        public boolean isPressed() {
            return isPressed;
        }

        public boolean wasTyped() {
            return wasTyped;
        }

        public int getGameMode() {
            return gameMode;
        }

        public void status(boolean pressed) {
            isPressed = pressed;
        }

        public void typed(boolean typed) {
            wasTyped = typed;
        }

        public void setGameMode(int gameMode) {
            this.gameMode = gameMode;
        }
    }

    /** Las diferentes teclas que se necesitan para interactuar con el juego */
    public Key up = new Key();
    public Key down = new Key();
    public Key left = new Key();
    public Key right = new Key();
    public Key space = new Key();
    public Key restart = new Key();
    public Key gameMode = new Key();

    /**
     * Se invoca al pulsar un tecla.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switchKey(e.getKeyCode(), true);
    }

    /**
     * Se invoca al soltar una tecla.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switchKey(e.getKeyCode(), false);
    }

    /**
     * Se invoca al 'teclear' (pulsar y luego soltar)
     * una tecla.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        if(!game.ingame)
            typeKey(e.getKeyChar());

        // Si tecleamos la tecla de Escape en cualquier momento, salimos del juego.
        if (e.getKeyChar() == 27) {
            System.exit(0);
        }
    }

    /**
     * Al pulsar o soltar una tecla, si es necesario, actualizar el objeto Key que se
     * corresponda según haya sido pulsada o soltada.
     * @param keyCode El keyCode de la tecla pulsada.
     * @param pressed True si la tecla ha sido pulsada, False en caso contrario.
     */
    private void switchKey(int keyCode, boolean pressed) {
        if (keyCode == KeyEvent.VK_Q || keyCode == KeyEvent.VK_UP) {
            up.status(pressed);
        }
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_DOWN) {
            down.status(pressed);
        }
        if (keyCode == KeyEvent.VK_O || keyCode == KeyEvent.VK_LEFT) {
            left.status(pressed);
        }
        if (keyCode == KeyEvent.VK_P || keyCode == KeyEvent.VK_RIGHT) {
            right.status(pressed);
        }
        if (keyCode == KeyEvent.VK_SPACE) {
            space.status(pressed);
        }
    }

    /**
     * Al 'teclear' una tecla (pulsar y luego soltar), actualizar el estado
     * del objeto Key que se corresponda siempre que sea necesario.
     * @param keyChar El caracter correspondiente a la tecla 'tecleada'.
     */
    private void typeKey(char keyChar) {
        if(keyChar == '1') {
            gameMode.typed(true);
            gameMode.setGameMode(1);
        }
        if(keyChar == '2') {
            gameMode.typed(true);
            gameMode.setGameMode(2);
        }
        if(keyChar == '3') {
            gameMode.typed(true);
            gameMode.setGameMode(3);
        }
        if(keyChar == '4') {
            gameMode.typed(true);
            gameMode.setGameMode(4);
        }
        if(keyChar == 's' || keyChar == 'S')
            restart.typed(true);
        if(keyChar == 'n' || keyChar == 'N')
            System.exit(0); // TODO: funciona estando en inselection y no deberia
    }
}
