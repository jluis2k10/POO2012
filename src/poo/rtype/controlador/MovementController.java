package poo.rtype.controlador;

import java.util.ArrayList;
import java.util.Iterator;

import poo.rtype.modelo.Player;
import poo.rtype.modelo.interfaces.EffectIF;
import poo.rtype.modelo.interfaces.EntityIF;

/**
 * Controlador para actualizar las posiciones (mover) los diferentes
 * objetos que se sitúan en la pantalla de juego.
 *
 * @author José Luis Pérez González
 */
public class MovementController {

    /**
     * Constructor de la clase, nada que hacer.
     */
    public MovementController() {}

    /**
     * Actualizar las posiciones de las diferentes entidades que
     * pueden 'interactuar' entre ellas (Jugador, Naves enemigas y Misiles).
     * @param entities Contenedor de las entidades.
     * @param delta El tiempo que ha pasado desde la última actualización.
     * @return El contenedor de las entidades con las posiciones actualizadas.
     */
    public ArrayList<EntityIF> moveEntities(ArrayList<EntityIF> entities, long delta) {
        for(EntityIF entity: entities) {
            if(entity instanceof Player) {
                ArrayList<EntityIF> missiles = ((Player) entity).getMissiles();
                missiles = moveEntities(missiles, delta);
            }
            entity.move(delta);
        }
        return entities;
    }

    /**
     * Actualizar las posiciones de las diferentes entidades que no 'interactuan'
     * con el resto, los efectos del juego (Estrellas y Explosiones).
     * @param effects Contenedor de los efectos.
     * @param delta El tiempo que ha pasado desde la última actualización.
     * @return El contenedor de los efectos con las posiciones actualizadas.
     */
    public ArrayList<EffectIF> moveEffects(ArrayList<EffectIF> effects, long delta) {
        Iterator<EffectIF> it = effects.iterator();
        while(it.hasNext()) {
            EffectIF effect = it.next();
            if(effect.isVisible())
                effect.move(delta);
            else {
                it.remove();
                Game.CURRENT_EXPLOSIONS--;
            }
        }
        return effects;
    }
}
