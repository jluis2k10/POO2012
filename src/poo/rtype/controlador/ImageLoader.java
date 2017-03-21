package poo.rtype.controlador;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Esta clase sireve para cargar los archivos de imagen necesarios,
 * e intentar que a su vez sean imágenes capaces de ser manejadas
 * directamente por la tarjeta gráfica del equipo.
 * @author José Luis Pérez González
 */
public class ImageLoader {
    GraphicsDevice gd;
    GraphicsConfiguration gc;

    /**
     * Constructor de la clase.
     * Recogemos el entorno gráfico que usa el sistema y a su vez
     * la configuración del mismo.
     */
    public ImageLoader() {
        gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        gc = gd.getDefaultConfiguration();
    }

    /**
     * Lee un archivo con la imagen e intenta crear una imagen capaz
     * de ser acelerada por la tarjeta gráfica.
     * @param imageSource La ruta hasta el archivo con la imagen.
     * @return La imagen contenida en un objeto Image
     */
    public Image LoadImage(String imageSource) {
        // Cargar imagen
        try {
            Image image = ImageIO.read(getClass().getResource(imageSource));
            image.setAccelerationPriority(1f);

            if(isAccelerated(image)) {
                if(Game.DEBUG)
                    System.out.println("La imagen está acelerada.");
                return image;
            } else {
                if(Game.DEBUG) {
                    System.out.println("La imagen no está acelerada.");
                    System.out.println("Intentando acelerar la imagen.");
                }

                // Intento de hacer imagen acelerada
                Image tmpImage = gc.createCompatibleVolatileImage(image.getWidth(null), image.getHeight(null), Transparency.TRANSLUCENT);
                Graphics2D g = (Graphics2D) tmpImage.getGraphics();
                g.setComposite(AlphaComposite.Src); // Componer la imagen de modo que los píxels transparentes 'se vean' transparantes.
                g.drawImage(image, 0, 0, null);
                g.dispose();
                image = tmpImage;

                if(Game.DEBUG) {
                    if (isAccelerated(image))
                        System.out.println("La imagen está ahora acelerada!");
                    else
                        System.err.println("Falló un intento de acelerar la imagen!");
                }

                return image;
            }

        } catch(IOException e) {
            System.out.println("No se ha podido leer el archivo de imagen: " + imageSource);
        }
        return null;
    }

    /**
     * Devuelve true si una imagen está siendo controlada (acelarada)
     * directamente en la tarjeta gráfica.
     * @param image La imagen a comprobar.
     * @return True si está acelerada.
     */
    private boolean isAccelerated(Image image) {
        return image.getCapabilities(gc).isAccelerated();
    }

}
