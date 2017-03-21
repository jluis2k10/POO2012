package poo.rtype.modelo;

import poo.rtype.controlador.Game;
import poo.rtype.controlador.InputController;
import poo.rtype.modelo.interfaces.EntityIF;

import java.util.ArrayList;

/**
 * Esta clase extiende la clase Entity y representa el objeto Player
 * @author José Luis Pérez González
 */
public class Player extends Entity {
    /** Cuándo se disparó el último misil */
    private long lastFiredMissile;
    /** Tiempo en milisegundos que nos indica el intervalo mínimo de tiempo que puede pasar entre dos disparos consecutivos */
    private static final int fireInterval = 150;
    /** Misiles disparados que se encuentran en pantalla */
    private ArrayList<EntityIF> missiles;
    /** Velocidad de la nave del jugador en píxels/segundo */
    private int moveSpeed;
    /** El controlador de las entradas del teclado */
    private InputController input;

    /**
     * Constructor de la clase Player. La ruta hacia la imagen y su máscara,
     * así como su posición incial, no es necesario pasarlas como parámetro
     * porque siempre serán lo mismo.
     * @param moveSpeed La velocidad de movimiento de la nave del jugador.
     * @param input Instancia del controlador de las entradas de teclado.
     */
    public Player(int moveSpeed, InputController input) {
        super(60, 190, "/poo/rtype/player.png", "/poo/rtype/player_mask.png");
        this.moveSpeed = moveSpeed;
        missiles = new ArrayList<EntityIF>();
        lastFiredMissile = 0;
        this.input = input;
    }

    /**
     * Devuelve la lista con los misiles disparados que aún se encuentran en
     * pantalla.
     * @return La lista de misiles disparados por el Jugador.
     */
    public ArrayList<EntityIF> getMissiles() {
        return missiles;
    }

    /**
     * Intenta un disparo. Si han pasado menos de 150 ms desde el último
     * efectuado, no hacer nada.
     */
    public void fire() {
        if (System.currentTimeMillis() - lastFiredMissile < fireInterval) {
            return;
        }
        missiles.add(new Missile(this.getX() + this.getWidth() - 20, this.getY() + this.getHeight()/2, moveSpeed * 2));
        lastFiredMissile = System.currentTimeMillis();
        return;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Si se está pulsando alguna de las teclas de dirección, actualizar la velocidad
     * de movimiento corresponsiente. Además la nave no debe sobrepasar los límites
     * de la pantalla, deteniendo su movimiento si llega a alguno de ellos.
     * </p>
     */
    @Override
    public void move(long delta) {
        // Primero asumimos que no hay movimiento. Si alguna de las teclas
        // de movimiento están pulsadas actualizar la velocidad de movimiento
        // que corresponda.
        this.setHorizontalMovement(0);
        this.setVerticalMovement(0);

        if(input != null) {
            if(input.right.isPressed() && !input.left.isPressed())
                this.setHorizontalMovement(moveSpeed);
            if(input.left.isPressed() && !input.right.isPressed())
                this.setHorizontalMovement(-moveSpeed);
            if(input.up.isPressed() && !input.down.isPressed())
                this.setVerticalMovement(-moveSpeed);
            if(input.down.isPressed() && !input.up.isPressed())
                this.setVerticalMovement(moveSpeed);
        }

        // No moverse si estamos en el límite izquierdo de la pantalla.
        if ((this.getHorizontalMovement() < 0) && (this.getX() < 0)) {
            this.setX(0);
        }
        // No moverse si estamos en el límite derecho de la pantalla.
        if ((this.getHorizontalMovement() > 0) && (this.getX() > Game.P_WIDTH  - this.getWidth())) {
            this.setX(Game.P_WIDTH - this.getWidth());
        }
        // No moverse si estamos en el límite superior de la pantalla
        if ((this.getVerticalMovement() < 0) && (this.getY() <= 0)) {
            this.setY(0);
        }
        // No moverse si estamos en el límite inferior de la pantalla
        if ((this.getVerticalMovement() > 0) && (this.getY() > Game.P_HEIGHT - this.getHeight())) {
            this.setY(Game.P_HEIGHT - this.getHeight());
        }

        super.move(delta);
    }

}
