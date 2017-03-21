package poo.rtype.modelo.interfaces;

import java.awt.Rectangle;

/**
 * Interfaz que debe implementar cualquier objeto que requiera que
 * se detecte si colisiona con otro de los que aparecen en pantalla.
 * @author José Luis Pérez González
 */
public interface CollisionableIF {
    /**
     * Devuelve la posición de la entidad en el eje horizontal.
     * @return La posición en el eje horizontal
     */
    public int getX();

    /**
     * Devuelve la posición de la entidad en el eje vertical.
     * @return La posición en el eje vertical.+
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
     * Devuelve la altura en píxels de la imagen que forma la entidad.
     * @return La altura de la imagen.
     */
    public int getHeight();

    /**
     * Devuelve el ancho en píxels de la imagen que forma la entidad.
     * @return El ancho de la imagen.
     */
    public int getWidth();

    /**
     * Devuelve los límites de la entidad en forma de rectángulo,
     * los cuales están definidos por el tamaño de la imagen que
     * le da forma (altura y anchura de la imagen).
     * @return Los límites de la entidad.
     */
    public Rectangle getBounds();

    /**
     * Devuelve un array de N enteros donde N es la altura en píxels
     * de la imagen que forma la entidad. Cada entero representa una fila
     * diferente de píxeles de la imagen.
     * Se utiliza para detectar colisiones "pixel-perfect".
     * @return La máscara de la imagen que forma la entidad.
     */
    public int[] getMaskArray();

    /**
     * Devuelve cierto si la entidad ha colisionado, atendiendo a sus
     * límites, con otra de las que aparecen en pantalla.
     * @param e La entidad con la que efectuar la comprobación.
     * @return True si existe colisión.
     */
    public boolean collidesWith(CollisionableIF e);
}
