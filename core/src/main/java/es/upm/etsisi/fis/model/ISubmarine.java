package es.upm.etsisi.fis.model;

/**
 * Capacidad especial del Submarino: autoreparación de daños sufridos.
 */
public interface ISubmarine {
    void markRepaired();
    boolean isRepaired();
}
