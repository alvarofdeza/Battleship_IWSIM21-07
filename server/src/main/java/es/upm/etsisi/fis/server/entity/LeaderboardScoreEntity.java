package es.upm.etsisi.fis.server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA persistente para el registro de puntuaciones y calculo de clasificaciones (Leaderboard).
 */
@Entity
@Table(name = "server_leaderboard")
public class LeaderboardScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "points", nullable = false)
    private long points;

    @Column(name = "game_id", nullable = false, length = 64)
    private String gameId;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    public LeaderboardScoreEntity() {
        this.recordedAt = LocalDateTime.now();
    }

    public LeaderboardScoreEntity(String username, long points, String gameId) {
        this.username = username;
        this.points = points;
        this.gameId = gameId;
        this.recordedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
