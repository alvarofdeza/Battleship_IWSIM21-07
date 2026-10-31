package es.upm.etsisi.fis.model;

/**
 * Portaaviones: La nave de mayor envergadura de la flota (longitud 5 casillas).
 */
public class Carrier extends Ship {

    public Carrier() {
        super(5);
    }

    @Override
    public String getName() {
        return "Carrier";
    }

    @Override
    public void executeSpecialAction(Game game) {
        // Reconocimiento de radar
    }
}
