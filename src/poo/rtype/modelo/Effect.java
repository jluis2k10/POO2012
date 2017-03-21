package poo.rtype.modelo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import poo.rtype.controlador.ImageLoader;
import poo.rtype.modelo.interfaces.EffectIF;

/**
 * Los diferentes 'efectos especiales' del juego extienden esta clase.
 * Pueden moverse y ser dibujados en pantalla pero no interactúan con
 * ningún otro objeto.
 * <p>
 * Dos tipos de efectos: animaciones en forma de sprites que muestran
 * una parte de una imagen dependiendo del tiempo que pase, o bien
 * objetos que se dibujan directamente mediante métodos de Graphics2D
 * (líneas, puntos, formas, etc).
 * </p>
 * @author José Luis Pérez González
 */
public class Effect implements EffectIF {
    /** La posición de la entidad en el eje horizontal */
    private double x;
    /** La posición de la entidad en el eje vertical */
    private double y;
    /** La velocidad horizontal de la entidad (pixels/seg) */
    private double dx;
    /** La velocidad vertical de la entidad (pixels/seg) */
    private double dy;
    /** La visibilidad en pantalla de la entidad */
    private boolean visible;
    /** El ancho de la imagen */
    private int width;
    /** La altura de la imagen */
    private int height;

    /** Objeto Image con la información de la imagen que forma la entidad */
    private Image image;
    /** Objeto Image con la información del sprite que forma la entidad */
    private Image sprite;
    /** Ancho en píxels de cada frame de una animación (la animación será una imagen en forma de sprite) */
    private int frameWidth;
    /** Altura en píxels de cada frame de una animación */
    private int frameHeight;
    /** Posición inicial dentro del sprite, del frame que debe dibujarse */
    private int startOffset;
    /** Posición final dentro del sprite, del frame que debe dibujarse */
    private int endOffset;
    /** Momento en el que se inicia la animación */
    private long initTime;
    /** Cada cuánto tiempo, en milisegundos, sebe actualizarse el frame de la animación */
    private long frameTimeStep;
    /** Momento en el cual se debe cambiar de frame en la animación */
    private long timeNextFrame;
    /** Número de frames de los que consta la animación */
    private int totalFrames;
    /** Frame que se muestra actualmente */
    private int currentFrame;

    /** Nos ayudamos de esta clase para cargar las imágenes de la entidad */
    private ImageLoader loader = new ImageLoader();

    /**
     * Constructor si se trata de una animación (sprite).
     * @param x Posición inicial del efecto en el eje horizontal.
     * @param y Posición inicial del efecto en el eje vertical.
     * @param imageSource Ruta hasta el archivo con la imagen/sprite.
     * @param frameWidth Ancho en píxels de cada frame de la animación.
     * @param frameHeight Alto en píxels de cada frame de la animación.
     * @param totalFrames Número de frames que contiene la animación.
     * @param frameTimeStep Tiempo que debe transcurrir entre frame y frame de la animación.
     */
    public Effect(int x, int y, String imageSource, int frameWidth, int frameHeight, int totalFrames, int frameTimeStep) {
        this.x = x;
        this.y = y;

        this.initTime = System.currentTimeMillis();
        this.frameTimeStep = frameTimeStep;
        this.timeNextFrame = this.initTime + this.frameTimeStep;

        sprite = loader.LoadImage(imageSource);
        this.width = sprite.getWidth(null);
        this.height = sprite.getHeight(null);
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.totalFrames = totalFrames;

        this.currentFrame = 0;
        setVisible(true);
    }

    /**
     * Constructor alternativo para el efecto si se trata de una imagen estática.
     * En la práctica no se usa, pero ya que estamos...
     * @param x Posición inicial del efecto en el eje horizontal.
     * @param y Posición horizontal del efecto en el eje vertical.
     * @param imageSource Ruta hasta el archivo de la imagen.
     */
    public Effect(int x, int y, String imageSource) {
        this.x = x;
        this.y = y;
        image = loader.LoadImage(imageSource);
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
        setVisible(true);
    }

    /**
     * Constructor alternativo si se trata de un efecto sin imágenes.
     * @param x Posición inicial del efecto en el eje horizontal.
     * @param y Posición inicial del efecto en el eje vertical.
     */
    public Effect(int x, int y) {
        this.x = x;
        this.y = y;
        setVisible(true);
    }

    /**
     * Si el efecto se trata de una animación, deberemos
     * llamar a éste método con cada actualización de estado
     * del juego para comprobar si debe cambiar el frame
     * que se muestra.
     */
    public void animateEffect() {
        if(sprite == null)
            return;

        if(timeNextFrame <= System.currentTimeMillis()) {
            currentFrame++;
            if(currentFrame >= totalFrames) {
                currentFrame = 0;
                setVisible(false);
            }
            startOffset = currentFrame * frameWidth;
            endOffset = startOffset + frameWidth;
            timeNextFrame = System.currentTimeMillis() + frameTimeStep;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void move(long delta) {
        x += (delta * dx) / 1000;
        y += (delta * dy) / 1000;
    }

    /**
     * {@inheritDoc}
     */
    public int getX() {
        return (int)Math.round(x);
    }

    /**
     * {@inheritDoc}
     */
    public int getY() {
        return (int)Math.round(y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setX(int x) {
        this.x = x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setY(int y) {
        this.y = y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHeight() {
        return this.height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWidth() {
        return this.width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHorizontalMovement(double dx) {
        this.dx = dx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVerticalMovement(double dy) {
        this.dy = dy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getHorizontalMovement() {
        return dx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getVerticalMovement() {
        return dy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {
        return visible;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Si contiene un sprite, se trata de una animación y dibujamos
     * el frame correspondiente.
     * Si contiene una imagen se dibuja directamente en su posición.
     * Si no contiene una imagen o sprite, en principio se dibuja un
     * punto blanco en la posición del efecto.
     * </p>
     */
    @Override
    public void Draw(Graphics2D g2d) {
        if(!isVisible())
            return;
        // Dibujar el frame correspondiente al sprite...
        if(sprite != null) {
            g2d.drawImage(sprite, getX(), getY(), getX()+frameWidth, getY()+frameHeight, startOffset, 0, endOffset, frameHeight, null);
            return;
        }
        // o dibujar la imagen 'estática'...
        if(image != null) {
            g2d.drawImage(image, getX(), getY(), null);
            return;
        }
        // o dibujar un puntito.
        g2d.setColor(new Color(225,225,225));
        g2d.drawLine(getX(), getY(), getX(), getY());
    }

}
