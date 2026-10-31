package es.upm.etsisi.fis.model;

/**
 * Contrato de operaciones para el cálculo y asignación de puntuaciones de los jugadores.
 */
public interface IScore {
    /**
     * Obtiene los puntos acumulados en esta evaluación.
     * @return Puntuación numérica.
     */
    long getPoints();

    /**
     * Clona el estado de la puntuación actual.
     * @return Nueva instancia duplicada de IScore.
     */
    IScore cloneScore();

    void setPoints(long points);
    void setGameId(Long gameId);
    Long getGameId();
}
