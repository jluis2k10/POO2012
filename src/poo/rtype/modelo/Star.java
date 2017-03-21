package poo.rtype.modelo;

import java.util.Random;

import poo.rtype.controlador.Game;

/**
 * Esta clase modela el objeto Star.
 * A pesar de ser un objeto que se mueve por la pantalla, es símplemente
 * decorativo y no interactúa con los demás objetos, con lo que extiende
 * la clase Effect.
 * @author José Luis Pérez González
 */
public class Star extends Effect {
    /** Generador de número aleatorios */
    private static Random randGenerator = new Random();

    /**
     * Constructor de la clase.
     * @param x La posición en el eje horizontal.
     * @param y La posición en el eje vertical.
     * @param dx La velocidad en píxels/segundo a la que se mueve.
     */
    public Star(int x, int y, double dx) {
        super(x, y);
        setHorizontalMovement(dx);
        setVerticalMovement(0);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Actualiza la posición (mueve) de la estrella en función del tiempo
     * en milisegundos que haya pasado desde el último movimiento. Si sale
     * por la izquierda de la pantalla, recolocarla aleatoriamente a la
     * derecha de la misma.
     * </p>
     * @param delta Tiempo transcurrido en milisegundos.
     */
    @Override
    public void move(long delta) {
        if(getX() < 0) {
            setX(Game.P_WIDTH);
            setY(randGenerator.nextInt(Game.P_HEIGHT - 60) + 30);
        }
        super.move(delta);
    }
}