package es.upm.etsisi.fis.model;

import java.util.List;

/**
 * Contrato de operaciones para la consulta y visualización del estado de una partida.
 */
public interface IGame {
    String getCurrentPlayerName();
    List<char[][]> getBoardViews();
    String getOpponentPlayerName();
}
