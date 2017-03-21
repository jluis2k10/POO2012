package poo.rtype.modelo.interfaces;

/**
 * Esta interfaz se usa para implementar los objetos (entidades) que
 * interaccionan entre ellos.
 * <p>
 * Extiende MovableIF, DrawableIF y CollisionableIF pero no implementa
 * ningún otro método extra porque no lo necesita. Se agrupan aquí para
 * que luego se pueda crear un contenedor (ArrayList) de este tipo de
 * objetos y poder trabajar con él.
 *  </p>
 * @author José Luis Pérez González
 */
public interface EntityIF extends MovableIF, DrawableIF, CollisionableIF {}
