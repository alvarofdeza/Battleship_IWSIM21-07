package es.upm.etsisi.fis.server.repository;

import es.upm.etsisi.fis.server.entity.MoveRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la consulta de trazabilidad de disparos.
 */
@Repository
public interface MoveRecordRepository extends JpaRepository<MoveRecordEntity, Long> {
    List<MoveRecordEntity> findByGameIdOrderByTimestampAsc(String gameId);
}
