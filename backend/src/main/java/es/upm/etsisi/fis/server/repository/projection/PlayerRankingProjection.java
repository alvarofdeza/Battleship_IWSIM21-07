package es.upm.etsisi.fis.server.repository.projection;

/**
 * Proyeccion JPA para la clasificacion agregada de jugadores por suma de puntos de carrera.
 */
public interface PlayerRankingProjection {
    String getUsername();
    Long getTotalPoints();
    Long getTotalGames();
    Long getTotalWins();
}
