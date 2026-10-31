package es.upm.etsisi.fis.model;

import java.util.List;

/**
 * Contrato de operaciones que define el comportamiento de cualquier jugador participante (humano o bot).
 */
public interface IPlayer {
    /**
     * Evalúa si el jugador acepta ejecutar una acción táctica complementaria.
     * @param action Tipo de acción especial solicitada.
     * @param remainingUses Usos restantes disponibles.
     * @return true si acepta la acción, false en caso contrario.
     */
    boolean acceptSpecialAction(ShipSpecialAction action, int remainingUses);

    /**
     * Calcula o solicita las coordenadas del disparo para el turno actual.
     * @param enemyBoardView Matriz con la niebla de guerra del tablero rival.
     * @return Array de 2 enteros [fila, columna] objetivo.
     */
    int[] takeTurn(char[][] enemyBoardView);

    /**
     * Registra un movimiento realizado por el jugador.
     * @param movement Instancia del movimiento.
     */
    void addMovement(IMovement movement);

    /**
     * Obtiene el nombre del jugador.
     * @return Cadena con el nombre o identificador visible.
     */
    String getName();

    /**
     * Asigna una puntuación obtenida al historial del jugador.
     * @param score Puntuación a añadir.
     */
    void addScore(IScore score);

    /**
     * Devuelve la lista histórica de movimientos efectuados por el jugador.
     * @return Lista de movimientos.
     */
    List<IMovement> getMovements();

    /**
     * Devuelve el historial de puntuaciones del jugador.
     * @return Lista de puntuaciones.
     */
    List<IScore> getScores();
}
