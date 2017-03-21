package poo.rtype.modelo;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import poo.rtype.controlador.ImageLoader;
import poo.rtype.modelo.interfaces.CollisionableIF;
import poo.rtype.modelo.interfaces.EntityIF;

/**
 * Esta clase implementa la interfaz EntityIF y será a su vez extendida
 * por cualquier objeto de tipo Nave o Misil del juego.
 *
 * @author José Luis Pérez González
 */
public class Entity implements EntityIF {
    /** La posición de la entidad en el eje horizontal */
    private double x;
    /** La posición de la entidad en el eje vertical */
    private double y;
    /** La velocidad horizontal de la entidad (pixels/seg) */
    private double dx;
    /** La velocidad vertical de la entidad (pixels/seg) */
    private double dy;
    /** El ancho en píxels de la entidad (definido por su imagen) */
    private int width;
    /** La altura en píxels de la entidad (definido por su imagen) */
    private int height;
    /** La visibilidad en pantalla de la entidad */
    private boolean visible;
    /** Objeto Image con la información de la imagen que forma la entidad */
    private Image image;
    /** Un array de enteros que representa la máscara de la entidad, se usa para detectar colisiones precisas */
    private int[] maskArray;

    /** Se usa esta clase como ayuda para cargar las imágenes correspondientes a la entidad */
    private ImageLoader loader = new ImageLoader();

    /**
     * Constructor de la Entidad.
     * @param x La posición sobre el eje horizontal.
     * @param y La posición sobre el eje vertical.
     * @param entityImage La ubicación del archivo de imagen de la entidad.
     * @param entityImageMask La ubicación del archivo con la máscara de
     * 						  la imagen de la entidad.
     */
    public Entity(int x, int y, String entityImage, String entityImageMask) {
        image = loader.LoadImage(entityImage);
        visible = true;
        this.x = x;
        this.y = y;
        width = image.getWidth(null);
        height = image.getHeight(null);
        maskArray = makeMaskArray(entityImageMask);
    }

    /**
     * Construimos el array que nos servirá para calcular colisiones
     * "pixel-perfect", más precisas que si sólo usáramos detección mediante
     * intersects().
     * <p>
     * La máscara de la imagen es otra imagen que representa en blanco y negro
     * si un píxel de la imagen original es transparente (negro en la máscara)
     * y por lo tanto no debe tenerse en cuenta para calcular una colisión, o si
     * por el contrario no es transparente (blanco en la máscara) y sí debe
     * tenerse en cuenta en los cálculos de la colisión. 
     * </p>
     * <p>
     * El array que genera éste método contendrá N enteros, donde N es la altura
     * en píxels de la máscara de la imagen, y cada entero representa cada una de
     * las filas de la máscara. Este entero es interpretado en binario, de modo
     * que cada uno de los píxeles de la máscara corresponde con uno
     * de sus dígitos (1 indica un píxel de color blanco, y 0 un píxel de
     * color negro). 
     * </p>
     * <p>
     * OJO! Esto nos limita a usar imágenes de 32x32 píxeles de tamaño
     * máximo. Un valor int es de 32 bits y no podemos por lo tanto representar
     * imágenes mayores. Se podría implementar para imágenes más grandes
     * pero se complicaría demasiado el método y para la práctica es suficiente.
     * </p>
     * @param entityImageMask La ubicación del archvio con la máscara de la imagen.
     * @return El array que representa la máscara.
     */
    private int[] makeMaskArray(String entityImageMask) {

        // Se necesita bufferImage debido a que vamos a recoger una matriz con los valors RGB de cada píxel de la imagen.
        BufferedImage imageMask = null;

        try {
            imageMask = ImageIO.read(this.getClass().getResource(entityImageMask));
        }
        catch (IOException ex) {
            System.out.println("Error: no se ha podido leer el archivo de máscara indicado: " + entityImageMask);
        }

        // Array de ancho*alto elementos cada uno de los cuales contiene el valor RGB de cada pixel de la máscara.
        int [] pixels = imageMask.getRGB(0, 0, width, height, null, 0, width);
        // Array que contendrá un entero por cada fila de la máscara, y este entero en binario representará
        // con un 0 un pixel que no forma parte de la imagen (transparente), y con un 1 un pixel que sí forma
        // parte de ella, es decir que debe tenerse en cuenta en las colisiones. 
        int [] mask = new int[this.height];
        for (int i = 0; i<this.height; i++){
            for (int j = 0; j<this.width; j++) {
                // Eliminar Alpha channel de cada pixel y desplazar los bits 23 veces a la derecha, dejando su valor en 0 o en 1.
                // Recordatorio: los píxels son de color blanco (FFFFFF) o negro (000000). Nos quedamos con el bit
                // más significativo de la representación en binario de cada píxel.
                pixels[i*this.width + j] = (pixels[i*this.width + j] & 0x00FFFFFF) >> 23;
                // Con cada entrada de un píxel nuevo, se desplaza la fila correspondiente de la máscara
                // un bit a la izquierda para hacer sitio al que entra.
                mask[i] = mask[i] << 1;
                // Añadir la representación del píxel calculado anteriormente a la fila correspondiente de la máscara.
                mask[i] += pixels[i*this.width + j];
            }
        }
        pixels = null;
        return mask;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Añadimos una comprobación extra además de los límites de
     * la entidad, que consiste en calcular mediante una técnica de
     * 'pixel-perfect' si dos entidades han colisionado. Se invoca por
     * lo tanto al método {@link #checkPixelCollisions(CollisionableIF)}.
     * <p>
     */
    @Override
    public boolean collidesWith(CollisionableIF e) {
        Rectangle r1 = this.getBounds();
        Rectangle r2 = e.getBounds();
        if(r1.intersects(r2)) {
            return this.checkPixelCollisions(e);
        }
        return false;
    }

    /**
     * Este método comprueba de forma precisa si dos entidades colisionan. Calcula
     * la colisión atendiendo a los píxeles que forman las imágenes de cada Entidad,
     * y no sólo a los límites de la imagen (pueden existir píxels trasparentes y
     * queda "feo" decir que dos entidades colisionan cuando visualmente no es así).
     * <p>
     * Es mucho más costoso que calcular la colisión mediante intersects() como hace
     * el método collidesWith(), por lo tanto lo llamaremos de forma explícita cuando
     * hayamos detectado una colisión mediante intersects() y sólo se usará para
     * colisiones Jugador-Enemigo y Misil-Enemigo.
     *  </p>
     * @param e La entidad con la que comprobar si existe una colisión.
     * @return True si se produce una colisión entre las entidades.
     */
    private boolean checkPixelCollisions(CollisionableIF e) {
        /** La primera fila de la Entidad 1 en la cual se produce una intersección */
        int lineEntity1;
        /** La primera fila de la Entidad 2 en la cual se produce una intersección */
        int lineEntity2;
        /** El número total de filas de la máscara que intersectan entre ellas */
        int intersectedLines;
        /** Array que representa la máscara de la Entidad 1 */
        int[] entity1Mask = this.getMaskArray();
        /** Array que representa la máscara de la Entidad 2 */
        int[] entity2Mask = e.getMaskArray();
        /**
         *  El desplazamiento que hay que aplicar a las máscaras.
         *  Debemos tener en cuenta que las intersecciones pueden producirse
         *  siendo la posición sobre el eje horizontal (x) de cada Entidad diferente,
         *  por lo tanto tendremos que desplazar una de las máscaras para que
         *  coincidan entre ellas.
         */
        int offset;

        if(this.getY() <= e.getY()) {
            lineEntity1 = e.getY() - this.getY();
            lineEntity2 = 0;
            intersectedLines = Math.min(this.getHeight() - lineEntity1, e.getHeight());
        } else {
            lineEntity1 = 0;
            lineEntity2 = this.getY() - e.getY();
            intersectedLines = Math.min(this.getHeight(), e.getHeight() - lineEntity2);
        }

        offset = e.getX() - this.getX();
        for(int i = 0; i<intersectedLines; i++) {
            long intersectedLineE1 = entity1Mask[i + lineEntity1]; // Al aplicar el offset puede que se salga del rango de int, se necesita long
            long intersectedLineE2 = entity2Mask[i + lineEntity2]; // Idem
            if(offset > 0)
                intersectedLineE2 = intersectedLineE2 << offset;
            else
                intersectedLineE1 = intersectedLineE1 << -offset;
            if((intersectedLineE1 & intersectedLineE2) != 0)
                return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getX() {
        return (int) Math.round(x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getY() {
        return (int) Math.round(y);
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
        return height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * {@inheritDoc}
     */
    public void setVisible(boolean visible) {
        this.visible = visible;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getMaskArray() {
        return maskArray;
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
    public void Draw(Graphics2D g2d) {
        if(!isVisible())
            return;
        g2d.drawImage(image, getX(), getY(), null);
    }
}
