package poo.rtype.controlador;

import poo.rtype.modelo.Enemy;
import poo.rtype.modelo.Player;
import poo.rtype.modelo.interfaces.EntityIF;

import java.util.ArrayList;
import java.util.Random;

/**
 * Controlador de colisiones entre diferentes entidades.
 *
 * @author José Luis Pérez González
 */
public class CollisionsController {

    /** Generador de números aleatorios (para que las naves enemigas y la estrellas no salgan siempre en las mismas posiciones) */
    private Random randGenerator = new Random();
    /** Instancia del controlador principal del juego. */
    private Game game;
    /** Contenedor con las naves que se deben eliminar */
    private ArrayList<EntityIF> shipsToRemove = new ArrayList<EntityIF>();
    /** Contenedor con los misiles que se deben eliminar */
    private ArrayList<EntityIF> missilesToRemove = new ArrayList<EntityIF>();

    /**
     * Constructor de la clase.
     * @param game Instancia del controlador principal.
     */
    public CollisionsController(Game game) {
        this.game = game;
    }

    /**
     * Comprobar si hay colisiones entre las diferentes entidades contenidas
     * en una lista y llevar a cabo las acciones necesarias en caso de que
     * efectivamente se produzca.
     * <p>
     * Devuelve la lista de entrada con el estado de las entidades modificado
     * en función de si están involucradas en una colisión o no.
     * </p>
     * @param entities La lista que contiene las entidades a comprobar.
     * @return La lista con el estado modificado de las diferentes entidades.
     */
    public ArrayList<EntityIF> checkCollisions(ArrayList<EntityIF> entities) {
        for(EntityIF entity: entities) {
            if(entity instanceof Player) {
                checkPlayerCollisions(entity, entities);
                ArrayList<EntityIF> missiles = ((Player) entity).getMissiles();
                checkMissilesCollisions(missiles, entities);
                missiles.removeAll(missilesToRemove);
            } else {
                checkEnemiesCollisions(entity, entities);
            }
        }
        entities.removeAll(shipsToRemove);
        return entities;
    }

    /**
     * Comprobar si la nave del jugador colisiona con alguna de las
     * naves enemigas restantes.
     * @param player La entidad que representa al jugador.
     * @param entities Lista con las entidades sobre las que comprobar la colisión.
     */
    private void checkPlayerCollisions(EntityIF player, ArrayList<EntityIF> entities) {
        for(EntityIF entity: entities) {
            if(entity instanceof Enemy && player.collidesWith(entity)) {
                player.setVisible(false);
                shipsToRemove.add(player);
                entity.setVisible(false);
                shipsToRemove.add(player);
                Game.TOTAL_ENEMIES--;
                game.addExplosion(player.getX() + player.getWidth() / 2 - 50,
                        player.getY() + player.getHeight() / 2 - 50, "/poo/rtype/explosionPlayer.png",
                        100, 100, 9, 100);
            }
        }
    }

    /**
     * Comprobar si alguno de los misiles colisiona con alguna de las
     * naves enemigas restantes.
     * @param missiles Lista que contiene los misiles.
     * @param entities Lista con las entidades sobre las que comprobar la colisión.
     */
    private void checkMissilesCollisions(ArrayList<EntityIF> missiles, ArrayList<EntityIF> entities) {
        for(EntityIF ms: missiles) {
            // Comprobar primero si hay que eliminar alguno de los misiles que han salido de la pantalla.
            // No es una colisión como tal pero es el mejor sitio donde hacerlo.
            if(!ms.isVisible())
                missilesToRemove.add(ms);
            for(EntityIF entity: entities) {
                if(entity instanceof Enemy && ms.collidesWith(entity)) {
                    ms.setVisible(false);
                    missilesToRemove.add(ms);
                    entity.setVisible(false);
                    shipsToRemove.add(entity);
                    Game.TOTAL_ENEMIES--;
                    if(((Enemy) entity).getEnemyType() == 1)
                        game.partialScore += 1500;
                    else
                        game.partialScore += 1000;
                    game.addExplosion(ms.getX() + ms.getWidth() - 16, ms.getY() + ms.getHeight() / 2 - 16, "/poo/rtype/explosion.png", 32, 32, 5, 100);
                }
            }
        }
    }

    /**
     * Comprobar colisiones entre dos naves enemigas diferentes.
     * @param me Nave enemiga sobre la que se están comprobando colisiones.
     * @param entities Lista con las entidades sobre las que comprobar la colisión.
     */
    private void checkEnemiesCollisions(EntityIF me, ArrayList<EntityIF> entities) {
        for(EntityIF entity: entities) {
            // Cambio de la velocidad vertical de las naves de tipo 1 si colisionan con cualquier otra nave.
            if(((Enemy) me).getEnemyType() == 1 && entity instanceof Enemy && !me.equals(entity) && me.collidesWith(entity)) {
                ((Enemy) me).changeDirection();
                // También aceleramos o frenamos su velocidad horizontal para que 'escapen'.
                if(me.getX() < entity.getX())
                    ((Enemy) me).boost();
                else
                    ((Enemy) me).brake();
            }
            // Al reaparecer por la derecha de la pantalla, las naves de tipo 0 pueden superponerse unas con otras,
            // así que si detectamos una colisión en éste momento, las recolocamos aleatoriamente.
            if(((Enemy) me).getEnemyType() == 0 && me.getX() >= Game.P_WIDTH && entity instanceof Enemy && !me.equals(entity) && me.collidesWith(entity)) {
                me.setX(randGenerator.nextInt(1500 + Game.P_WIDTH) + Game.P_WIDTH + 80); // Colocar la nave entre WIDTH+80 y WIDTH+1500
                me.setY(randGenerator.nextInt(Game.P_HEIGHT - 60) + 30); // Colocar la nave entre 30 y HEIGHT-30
            }
        }
    }
}
