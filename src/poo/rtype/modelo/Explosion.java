package poo.rtype.modelo;

/**
 * Esta clase extiende Effect y será utilizada por los objetos
 * que sean de 'efectos especiales', concrétamente por las explosiones
 * que se dibujarán cuando se produzca alguna colisión.
 * @author José Luis Pérez González
 */
public class Explosion extends Effect {
    /**
     * Constructor de la clase.
     * @param x Posición de la explosión en el eje horizontal.
     * @param y Posición de la explosión en el eje horizontal.
     * @param imageSource Ubicación del archivo con el srpite de la explosión.
     * @param frameWidth Anchura de cada frame de la animación con la explosión.
     * @param frameHeight Alto de cada frame de la animación con la explosión.
     * @param totalFrames Número de frames que componen la animación de la explosión.
     * @param frameTimeStep Tiempo que se debe mostrar cada frame de la animación.
     */
    public Explosion(int x, int y, String imageSource, int frameWidth, int frameHeight, int totalFrames, int frameTimeStep) {
        super(x, y, imageSource, frameWidth, frameHeight, totalFrames, frameTimeStep);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Las explosiones no se mueven, en su lugar aprovechamos para invocar a
     * animateEffect() y así animar la explosión.
     * </p>
     * @see #animateEffect()
     */
    @Override
    public void move(long delta) {
        this.animateEffect();
    }
}
