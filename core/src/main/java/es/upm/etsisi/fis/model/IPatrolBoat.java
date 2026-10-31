package es.upm.etsisi.fis.model;

/**
 * Capacidad especial del Patrullero: maniobra de movimiento evasivo y reconocimiento.
 */
public interface IPatrolBoat {
    void activateEvasion();
    boolean isEvasionActive();
}
