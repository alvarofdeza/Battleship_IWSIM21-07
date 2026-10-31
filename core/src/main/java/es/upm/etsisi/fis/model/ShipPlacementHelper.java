package es.upm.etsisi.fis.model;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Clase utilitaria para la generación y consulta de distribuciones de flotas en tableros.
 */
public class ShipPlacementHelper {

    /**
     * Genera un mapa con las coordenadas ocupadas por cada nave desplegada aleatoriamente.
     * @return Mapa asociativo de nombre de nave a conjunto de pares [fila, columna].
     */
    public static HashMap<String, HashSet<int[]>> generateFleetPlacement() {
        HashMap<String, HashSet<int[]>> fleetMap = new HashMap<>();
        Board board = new Board(null);
        board.placeRandomFleet();

        for (Ship ship : board.getShips()) {
            HashSet<int[]> positions = new HashSet<>(ship.getPositions());
            fleetMap.put(ship.getName(), positions);
        }
        return fleetMap;
    }
}
