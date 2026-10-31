package es.upm.etsisi.fis.model;

/**
 * Representa una celda unitaria dentro de la cuadrícula del tablero de juego.
 */
public class Cell {
    private boolean hit;
    private Ship ship;

    public Cell() {
        this.hit = false;
        this.ship = null;
    }

    /**
     * Marca la celda como impactada por un ataque.
     */
    public void markHit() {
        this.hit = true;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public boolean isHit() {
        return hit;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public boolean hasShip() {
        return ship != null;
    }
}
