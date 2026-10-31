package es.upm.etsisi.fis.server.repository;

import es.upm.etsisi.fis.server.entity.LeaderboardScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para consultas de clasificacion y mejores puntuaciones.
 */
@Repository
public interface LeaderboardScoreRepository extends JpaRepository<LeaderboardScoreEntity, Long> {
    List<LeaderboardScoreEntity> findTop10ByUsernameOrderByPointsDesc(String username);

    @Query("SELECT s FROM LeaderboardScoreEntity s ORDER BY s.points DESC")
    List<LeaderboardScoreEntity> findGlobalTopScores();
}
