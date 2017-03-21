package poo.rtype.modelo.interfaces;

/**
 * Interfaz que debe implementar cualquier objeto que deba
 * moverse por la pantalla.
 * @author José Luis Pérez González
 */
public interface MovableIF {
    /**
     * Actualizamos la posición (movemos) la entidad dependiendo
     * del tiempo en milisegundos que haya pasado.
     * @param delta El tiempo en ms que ha pasado.
     */
    public void move(long delta);

    /**
     * Devuelve la posición de la entidad en el eje horizontal.
     * @return La posición en el eje horizontal.
     */
    public int getX();

    /**
     * Devuelve la posición de la entidad en el eje vertical.
     * @return La posición en el eje vertical.
     */
    public int getY();

    /**
     * Actualizamos la posición de la entidad en el eje horizontal.
     * @param x La nueva posición en el eje horizontal.
     */
    public void setX(int x);

    /**
     * Actualizamos la posición de la entidad en el eje vertical.
     * @param y La nueva posición en el eje vertical.
     */
    public void setY(int y);

    /**
     * Definimos la velocidad horizontal de la entidad en
     * píxels/segundo.
     * @param dx La velocidad horizontal en píxels/segundo.
     */
    public void setHorizontalMovement(double dx);

    /**
     * Definimos la velocidad vertical de la entidad en
     * píxels/segundo.
     * @param dy La velocidad vertical en píxels/segundo.
     */
    public void setVerticalMovement(double dy);

    /**
     * Devuelve la velocidad horizontal de la entidad en
     * píxels/segundo.
     * @return La velocidad horizontal en píxels/segundo.
     */
    public double getHorizontalMovement();

    /**
     * Devuelve la velocidad vertical de la entidad en
     * píxels/segundo.
     * @return La velocidad vertical en píxels/segundo.
     */
    public double getVerticalMovement();
}
