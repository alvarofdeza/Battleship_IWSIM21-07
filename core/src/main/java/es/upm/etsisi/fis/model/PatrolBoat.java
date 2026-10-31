package es.upm.etsisi.fis.model;

/**
 * Patrullero: Embarcación ligera de reconocimiento rápido (longitud 2 casillas).
 */
public class PatrolBoat extends Ship implements IPatrolBoat {
    private boolean evasionActive;

    public PatrolBoat() {
        super(2);
        this.evasionActive = false;
    }

    @Override
    public String getName() {
        return "PatrolBoat";
    }

    @Override
    public void activateEvasion() {
        this.evasionActive = true;
    }

    @Override
    public boolean isEvasionActive() {
        return evasionActive;
    }

    @Override
    public void executeSpecialAction(Game game) {
        activateEvasion();
    }
}
