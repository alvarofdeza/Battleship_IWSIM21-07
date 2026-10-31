package es.upm.etsisi.fis.model;

/**
 * Submarino: Embarcación sumergible (longitud 3 casillas) capaz de autorreparar un impacto recibido.
 */
public class Submarine extends Ship implements ISubmarine {
    private boolean repaired;

    public Submarine() {
        super(3);
        this.repaired = false;
    }

    @Override
    public String getName() {
        return "Submarine";
    }

    @Override
    public void markRepaired() {
        this.repaired = true;
        this.restoreOneHit();
    }

    @Override
    public boolean isRepaired() {
        return repaired;
    }

    @Override
    public void executeSpecialAction(Game game) {
        if (!repaired && hitsTaken > 0) {
            markRepaired();
        }
    }
}
