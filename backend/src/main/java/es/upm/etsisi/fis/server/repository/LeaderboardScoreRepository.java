package es.upm.etsisi.fis.server.repository;

import es.upm.etsisi.fis.server.entity.LeaderboardScoreEntity;
import es.upm.etsisi.fis.server.repository.projection.PlayerRankingProjection;
import es.upm.etsisi.fis.server.repository.projection.UserStatsProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para consultas de clasificacion, rankings y expedientes de oficiales.
 */
@Repository
public interface LeaderboardScoreRepository extends JpaRepository<LeaderboardScoreEntity, Long> {

    List<LeaderboardScoreEntity> findTop10ByUsernameOrderByPointsDesc(String username);

    List<LeaderboardScoreEntity> findTop10ByUsernameOrderByRecordedAtDesc(String username);

    List<LeaderboardScoreEntity> findTop10ByOrderByPointsDesc();

    boolean existsByGameId(String gameId);

    @Query("SELECT s FROM LeaderboardScoreEntity s ORDER BY s.points DESC")
    List<LeaderboardScoreEntity> findGlobalTopScores();

    @Query("SELECT s.username AS username, " +
           "SUM(s.points) AS totalPoints, " +
           "COUNT(s) AS totalGames, " +
           "SUM(CASE WHEN s.result = 'VICTORY' THEN 1L ELSE 0L END) AS totalWins " +
           "FROM LeaderboardScoreEntity s " +
           "GROUP BY s.username " +
           "ORDER BY SUM(s.points) DESC")
    List<PlayerRankingProjection> findTopPlayersRanking(Pageable pageable);

    @Query("SELECT COALESCE(SUM(s.points), 0L) AS totalPoints, " +
           "COUNT(s) AS totalGames, " +
           "COALESCE(SUM(CASE WHEN s.result = 'VICTORY' THEN 1L ELSE 0L END), 0L) AS totalWins, " +
           "COALESCE(SUM(CASE WHEN s.result = 'DEFEAT' THEN 1L ELSE 0L END), 0L) AS totalLosses " +
           "FROM LeaderboardScoreEntity s " +
           "WHERE s.username = :username")
    UserStatsProjection getUserCareerStats(@Param("username") String username);
}
