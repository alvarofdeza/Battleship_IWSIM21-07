package es.upm.etsisi.fis.model;

/**
 * Capacidad especial del Acorazado: disparo táctico de artillería pesada.
 */
public interface IBattleship {
    int getAvailableArtilleryShots();
    void reduceArtilleryShots();
}
