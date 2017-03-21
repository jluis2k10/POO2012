package poo.rtype.modelo.interfaces;

/**
 * Esta inerfaz se usa para implementar los objetos considerados
 * 'efectos', púramente decorativos y que no interactúan con ningún
 * otro objeto del juego.
 * <p>
 * Extiende MovableIF y DrawableIF pero no necesita añadir ningún
 * otro método. Se hace así para que luego se pueda utilizar un contenedor
 * de efectos (ArrayList) y poder trabajar con él.
 * </p>
 * @author José Luis Pérez González
 */
public interface EffectIF extends MovableIF, DrawableIF {}
