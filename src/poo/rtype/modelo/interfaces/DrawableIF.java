package poo.rtype.modelo.interfaces;

import java.awt.Graphics2D;

/**
 * Interfaz que debe implementar cualquier objeto que necesite
 * ser pintado en pantalla.
 * @author José Luis Pérez González
 */
public interface DrawableIF {
    /**
     * Devuelve cierto si la entidad debe dibujarse en pantalla, y falso
     * en caso contrario.
     * @return True si la entidad es visible.
     */
    public boolean isVisible();

    /**
     * Actualiza el estado de visible/no visible de la entidad.
     * @param visible True para hacerlo visible, False para hacerlo invisible.
     */
    public void setVisible(boolean visible);

    /**
     * Devuelve la altura de la entidad.
     * @return La altura de la entidad en píxels.
     */
    public int getHeight();

    /**
     * Devuelve el ancho de la entidad.
     * @return El ancho de la entidad en píxels.
     */
    public int getWidth();

    /**
     * Dibuja la entidad en la pantalla.
     * @param g2d
     */
    public void Draw(Graphics2D g2d);
}
