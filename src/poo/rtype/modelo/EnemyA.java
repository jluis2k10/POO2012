package poo.rtype.modelo;

/**
 * Created by Jose Luis on 01/05/2015.
 */
public class EnemyA extends Enemy {
    /**
     * Constructor de la clase Enemy.
     *
     * @param x         La posición inicial del objeto sobre el eje horizontal.
     * @param y         La posición inicial del objeto sobre el eje vertical.
     * @param shipImage La ubicación del archivo imagen del objeto.
     * @param shipMask  La ubicación del archivo con la máscara de la imagen del objeto.
     * @param enemyType El tipo de enmigo: 1 (se mueve verticalmente) ó 0 (no se mueve verticalmente).
     * @param moveSpeed La velocidad a la que se mueve el objeto en píxels/segundo.
     */
    public EnemyA(int x, int y, String shipImage, String shipMask, byte enemyType, double moveSpeed) {
        super(x, y, shipImage, shipMask, enemyType, moveSpeed);
    }
}
