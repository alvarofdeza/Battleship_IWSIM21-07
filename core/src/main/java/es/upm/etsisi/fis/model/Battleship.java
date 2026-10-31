package es.upm.etsisi.fis.model;

/**
 * Acorazado: Buque con blindaje pesado (longitud 4 casillas) equipado con artillería especial.
 */
public class Battleship extends Ship implements IBattleship {
    private int availableArtilleryShots;

    public Battleship() {
        super(4);
        this.availableArtilleryShots = 1;
    }

    @Override
    public String getName() {
        return "Battleship";
    }

    @Override
    public int getAvailableArtilleryShots() {
        return availableArtilleryShots;
    }

    @Override
    public void reduceArtilleryShots() {
        if (availableArtilleryShots > 0) {
            availableArtilleryShots--;
        }
    }

    @Override
    public void executeSpecialAction(Game game) {
        if (availableArtilleryShots > 0 && game != null) {
            reduceArtilleryShots();
        }
    }
}
