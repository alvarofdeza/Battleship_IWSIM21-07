package es.upm.etsisi.fis.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase base abstracta que modela una nave de combate en el tablero.
 */
public abstract class Ship {
    protected final int length;
    protected int hitsTaken;
    protected boolean alive;
    protected final List<int[]> positions;

    /**
     * Inicializa una nave especificando su eslora (longitud de casillas).
     * @param length Eslora de la nave.
     */
    public Ship(int length) {
        this.length = length;
        this.hitsTaken = 0;
        this.alive = true;
        this.positions = new ArrayList<>();
    }

    /**
     * Determina si la nave sigue a flote.
     * @return true si la nave no ha recibido tantos impactos como su eslora, false si está hundida.
     */
    public boolean isAlive() {
        return alive && hitsTaken < length;
    }

    /**
     * Registra un impacto certero recibido sobre la nave.
     */
    public void registerHit() {
        if (!isAlive()) return;
        hitsTaken++;
        if (hitsTaken >= length) {
            sink();
        }
    }

    /**
     * Fuerza el estado de hundimiento completo de la nave.
     */
    public void sink() {
        this.alive = false;
        this.hitsTaken = length;
    }

    /**
     * Reduce un impacto previo restaurando operatividad (autoreparación).
     */
    protected void restoreOneHit() {
        this.alive = true;
        if (this.hitsTaken > 0) {
            this.hitsTaken--;
        }
    }

    public int getLength() {
        return length;
    }

    public int getHitsTaken() {
        return hitsTaken;
    }

    public List<int[]> getPositions() {
        return new ArrayList<>(positions);
    }

    public void addPosition(int row, int col) {
        this.positions.add(new int[]{row, col});
    }

    public void clearPositions() {
        this.positions.clear();
    }

    public abstract String getName();

    public abstract void executeSpecialAction(Game game);
}
