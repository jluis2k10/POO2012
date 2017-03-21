package poo.rtype.modelo;

import java.awt.Rectangle;
import java.util.Random;

import poo.rtype.controlador.Game;
import poo.rtype.modelo.interfaces.CollisionableIF;

/**
 * Esta clase extiende la clase Entity y representa el objeto Enemy.
 * Puede haber dos tipos diferentes de enmigos a su vez, que se comportan
 * de forma diferente en cuanto al método move().
 * @author José Luis Pérez González
 */
public class Enemy extends Entity {
    /** El tipo de enmigo: 1 (se mueve verticalmente) ó 0 (no se mueve verticalmente) */
    private byte enemyType;
    /** Distancia recorrida en vertical hasta el momento por el objeto */
    private double verticalDistance;
    /** ¿Está la nave acelerando o frenando en éste momento? */
    private boolean isBoosting, isBraking;
    /** Tiempo en el que se inició la aceleración o el frenado de la nave */
    private long boostTime, brakeTime;
    /** Generador de números aleatorios para usarlo al decidir hacia dónde debe moverse el objeto verticalmente */
    private static Random randGenerator = new Random();

    /**
     * Constructor de la clase Enemy.
     * @param x La posición inicial del objeto sobre el eje horizontal.
     * @param y La posición inicial del objeto sobre el eje vertical.
     * @param shipImage La ubicación del archivo imagen del objeto.
     * @param shipMask La ubicación del archivo con la máscara de la imagen del objeto.
     * @param enemyType El tipo de enmigo: 1 (se mueve verticalmente) ó 0 (no se mueve verticalmente).
     * @param moveSpeed La velocidad a la que se mueve el objeto en píxels/segundo.
     */
    public Enemy(int x, int y, String shipImage, String shipMask, byte enemyType, double moveSpeed){
        super(x, y, shipImage, shipMask);
        this.enemyType = enemyType;
        setHorizontalMovement(-moveSpeed);
        if(this.enemyType == 1) {
            setHorizontalMovement(-moveSpeed*1.1);
            setVerticalMovement(moveSpeed/2);
            setRandomDirection();
        }
    }
    /**
     * {@inheritDoc}
     * <p>
     * El objeto Enemy se debe mover de un modo particular.
     * No puede salir por los márgenes superior e inferior de la pantalla,
     * y si sale por el margen izquierdo debe reposicionarse a la derecha
     * de la misma para continuar el movimiento.
     * </p>
     */
    @Override
    public void move(long delta) {
        // Si está acelerando y ha pasado el tiempo indicado, restablecer su velocidad horizontal.
        if(isBoosting && (System.currentTimeMillis() - boostTime) > 1500) {
            this.setHorizontalMovement(getHorizontalMovement() * 0.8);
            isBoosting = false;
        }
        // Si está frenando y ha pasado el tiempo insicado, restablecer su velocidad horizontal.
        if(isBraking && (System.currentTimeMillis() - brakeTime) > 1000) {
            this.setHorizontalMovement(getHorizontalMovement() / 0.7);
            isBraking = false;
        }
        // Si el enemigo desaparece por la parte izquierda de la ventana, recolocarlo a la derecha de la misma.
        if (this.getX() < -this.getWidth()) {
            this.setX(Game.P_WIDTH);
            return;
        }
        // Si el enemigo alcanza o sobrepasa el límite superior o inferior de la ventana, recolocarlo,
        // cambiar su dirección vertical y resetear la distancia vertical recorrida.
        if (this.getY() < 10) {
            this.setY(10);
            changeDirection();
            return;
        }
        if (this.getY() > Game.P_HEIGHT - this.getHeight() - 5) {
            this.setY(Game.P_HEIGHT - this.getHeight() - 5);
            changeDirection();
            return;
        }
        // Si el enemigo es del tipo 1 (se mueve verticalmente), actualizar la distancia vertical
        // recorrida con cada movimiento y, si supera los 150 píxels, cambiar su dirección aleatoriamente.
        if (enemyType == 1) {
            verticalDistance += delta * Math.abs(this.getVerticalMovement()) / 1000;
            if (verticalDistance > 150) {
                setRandomDirection();
            }
        }

        super.move(delta);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Devuelve cierto si la entidad colisiona con otra.
     * En este caso sólo se usa para detectar colisiones entre dos
     * naves enemigas, por eso incrementamos un poco el tamaño de
     * los límites, así no se quedan bloqueadas en ciertos casos,
     * que sucedería si no lo estuvieran.
     * </p>
     * @param e La entidad con la que efectuar la comprobación.
     * @return True si existe colisión.
     */
    @Override
    public boolean collidesWith(CollisionableIF e) {
        Rectangle r1 = this.getBounds();
        r1.grow(8, 8);
        Rectangle r2 = e.getBounds();
        r2.grow(8, 8);
        if(r1.intersects(r2)) {
            return true;
        }
        return false;
    }

    /**
     * Cambia aleatoriamente la dirección vertical del objeto.
     */
    private void setRandomDirection() {
        verticalDistance = 0;
        if (randGenerator.nextInt(2) == 0)
            setVerticalMovement(-this.getVerticalMovement());
    }

    /**
     * Invierte la dirección vertical del objeto.
     * Se invoca cuando se detecta una colisión entre dos naves enemigas.
     */
    public void changeDirection() {
        verticalDistance = 0;
        this.setVerticalMovement(-this.getVerticalMovement());
    }

    /**
     * Incrementar la velocidad del objeto durante un determinado periodo de tiempo.
     * Si ya está acelerando, refrescar el tiempo en el que se inició.
     */
    public void boost() {
        if(!isBoosting) {
            isBoosting = true;
            boostTime = System.currentTimeMillis();
            this.setHorizontalMovement(this.getHorizontalMovement() / 0.8);
        } else {
            boostTime = System.currentTimeMillis();
        }
    }

    /**
     * Reducir la velocidad del objeto durante un determinado periodo de tiempo.
     * Si está acelerando, reducir primero su velocidad a la normal. Si ya estaba
     * frenando, refrescar el tiempo en el que se inició.
     */
    public void brake() {
        if(isBoosting) {
            isBoosting = false;
            this.setHorizontalMovement(this.getHorizontalMovement() * 0.8);
        }
        if(!isBraking) {
            isBraking = true;
            brakeTime = System.currentTimeMillis();
            this.setHorizontalMovement(this.getHorizontalMovement() * 0.7);
        } else {
            brakeTime = System.currentTimeMillis();
        }
    }

    /**
     * Devuelve el tipo de Enemy, si es 1 es del tipo de los que se mueven
     * verticalmente, y 0 en caso contrario.
     * @return El tipo de enemigo.
     */
    public byte getEnemyType() {
        return enemyType;
    }
}
