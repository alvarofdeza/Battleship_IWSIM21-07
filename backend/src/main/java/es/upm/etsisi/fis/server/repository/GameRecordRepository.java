package es.upm.etsisi.fis.server.repository;

import es.upm.etsisi.fis.server.entity.GameRecordEntity;
import es.upm.etsisi.fis.server.repository.projection.PlayerRankingProjection;
import es.upm.etsisi.fis.server.repository.projection.UserStatsProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para el acceso al modelo definitivo de partidas (server_games).
 * Ejecuta agregaciones en tiempo real para el Escalafon Militar con soporte de indices compuestos.
 */
@Repository
public interface GameRecordRepository extends JpaRepository<GameRecordEntity, String> {

    List<GameRecordEntity> findTop10ByPlayer1UsernameOrderByPointsDesc(String username);

    List<GameRecordEntity> findTop10ByPlayer1UsernameOrderByCreatedAtDesc(String username);

    @Query("SELECT g FROM GameRecordEntity g WHERE g.points IS NOT NULL ORDER BY g.points DESC, g.durationSeconds ASC")
    List<GameRecordEntity> findTopMatchesRanking(Pageable pageable);

    @Query("SELECT g FROM GameRecordEntity g WHERE g.points IS NOT NULL ORDER BY g.points DESC")
    List<GameRecordEntity> findGlobalTopScores();

    @Query("SELECT g.player1Username AS username, " +
           "SUM(g.points) AS totalPoints, " +
           "COUNT(g) AS totalGames, " +
           "SUM(CASE WHEN g.result = 'VICTORY' THEN 1L ELSE 0L END) AS totalWins " +
           "FROM GameRecordEntity g " +
           "WHERE g.player1Username IS NOT NULL AND g.player1Username != 'Guest' " +
           "GROUP BY g.player1Username " +
           "ORDER BY SUM(g.points) DESC")
    List<PlayerRankingProjection> findTopPlayersRanking(Pageable pageable);

    @Query("SELECT COALESCE(SUM(g.points), 0L) AS totalPoints, " +
           "COUNT(g) AS totalGames, " +
           "COALESCE(SUM(CASE WHEN g.result = 'VICTORY' THEN 1L ELSE 0L END), 0L) AS totalWins, " +
           "COALESCE(SUM(CASE WHEN g.result = 'DEFEAT' THEN 1L ELSE 0L END), 0L) AS totalLosses " +
           "FROM GameRecordEntity g " +
           "WHERE g.player1Username = :username")
    UserStatsProjection getUserCareerStats(@Param("username") String username);
}
