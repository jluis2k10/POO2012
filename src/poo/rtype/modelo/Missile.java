package poo.rtype.modelo;

import poo.rtype.controlador.Game;

/**
 * Esta clase extiende la clase Entity y representa el objeto Missile.
 * @author José Luis Pérez Gonzáles
 */
public class Missile extends Entity {

    /**
     * Constructor para el objeto Missile.
     * @param x La posición inicial del objeto sobre el eje horizontal.
     * @param y La posición inicial del objeto sobre el eje vertical.
     * @param moveSpeed La velocidad horizontal que debe tener el misil.
     */
    public Missile(int x, int y, int moveSpeed) {
        super(x, y, "/poo/rtype/missile.png", "/poo/rtype/missile_mask.png");
        setHorizontalMovement(moveSpeed);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Si un misil 'sale' por la derecha de la pantalla cambiamos su estado a
     * no visible. De este modo, en la próxima actualización del estado del
     * juego, el controlador de movimiento se encargará de eliminarlo.
     * </p>
     */
    @Override
    public void move(long delta) {
        if (this.getX() > Game.P_WIDTH) {
            this.setVisible(false);
        }
        super.move(delta);
    }
}
