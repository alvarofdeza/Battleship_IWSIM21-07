package es.upm.etsisi.fis.server.repository.projection;

/**
 * Proyeccion JPA para el calculo del expediente y estadisticas de carrera de un oficial.
 */
public interface UserStatsProjection {
    Long getTotalPoints();
    Long getTotalGames();
    Long getTotalWins();
    Long getTotalLosses();
}
