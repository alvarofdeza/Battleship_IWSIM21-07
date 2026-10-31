package es.upm.etsisi.fis.server.repository;

import es.upm.etsisi.fis.server.entity.GameRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para el acceso a registros historicos de partidas.
 */
@Repository
public interface GameRecordRepository extends JpaRepository<GameRecordEntity, String> {
    List<GameRecordEntity> findByPlayer1UsernameOrPlayer2UsernameOrderByCreatedAtDesc(String p1, String p2);
    List<GameRecordEntity> findByStatus(String status);
}
