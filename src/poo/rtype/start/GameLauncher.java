package poo.rtype.start;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import poo.rtype.controlador.Game;

/**
 * Lanzador de la aplicación. Invoca a la instancia principal del juego y
 * dibuja la ventana que lo contiene.
 * @author José Luis Pérez González
 */
@SuppressWarnings("serial")
public class GameLauncher extends JFrame {
    /**
     * Ajustes de rendimiento. Varían según sistema y lo que queramos utilizar.
     * En Windows funcionan tanto D3D como OpenGL, en Linux/Unix sólo OpenGL.
     * Si desactivamos OpenGL en Linux el juego va a unos FPS lamentables, pero
     * sorprendentemente sin OpenGL y con 'pmoffscreen' en false, funciona incluso
     * más rápido !!?¿ (probado en un portátil con VGA integrada Intel)
     * Windows activa D3D por defecto aunque no se lo indiquemos, si no queremos
     * hacer uso de la aceleración gráfica en Windows, debemos setearlo a False.
     * Con OpenGL en Windows y gráficas nVidia se pone el procesador a tope :(
     * parece que no se llevan bien.
     */
    static {
        //System.setProperty("sun.java2d.pmoffscreen", "false");
        System.setProperty("sun.java2d.transaccel", "True");
        //System.setProperty("sun.java2d.opengl", "True");
        System.setProperty("sun.java2d.d3d", "True");
        System.setProperty("sun.java2d.ddforcevram", "True");
    }
    /**
     * Constructor de la clase.
     */
    public GameLauncher() {

        Game gameCanvas = new Game(this); // El controlador principal del juego extiende un Canvas que será añadido al frame.

        setTitle("R-Type - Práctica POO 2013");
        add(gameCanvas);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack(); // Ajustar el tamaño del frame al tamaño del canvas que contiene.

        // Posicionar la ventana en el centro de la pantalla.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width/2 - getWidth()/2, screenSize.height/5 - getHeight()/5);
        setLocationRelativeTo(null);

        setVisible(true); // Para acabar, hacer visible la ventana.

        gameCanvas.createBufferAndStart(); // Crear el búfer e iniciar el Timer del controlador principal
    }

    /**
     * Crear y dibujar la ventana desde el hilo de eventos de Swing.
     * Se evita así que en algunos sistemas 'lentos' se intente dibujar
     * en el canvas del juego antes de que éste sea creado.
     * @param args Los parámetros de entrada.
     */
    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameLauncher();
            }
        });
    }
}
