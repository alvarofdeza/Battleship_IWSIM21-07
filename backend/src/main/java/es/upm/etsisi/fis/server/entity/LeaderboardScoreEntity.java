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

    @Column(name = "difficulty", length = 20)
    private String difficulty;

    @Column(name = "duration_seconds")
    private long durationSeconds;

    @Column(name = "moves_count")
    private int movesCount;

    @Column(name = "result", length = 20)
    private String result;

    public LeaderboardScoreEntity() {
        this.recordedAt = LocalDateTime.now();
        this.difficulty = "EASY";
        this.result = "VICTORY";
    }

    public LeaderboardScoreEntity(String username, long points, String gameId) {
        this.username = username;
        this.points = points;
        this.gameId = gameId;
        this.recordedAt = LocalDateTime.now();
        this.difficulty = "EASY";
        this.result = "VICTORY";
    }

    public LeaderboardScoreEntity(String username, long points, String gameId,
                                  String difficulty, long durationSeconds, int movesCount, String result) {
        this.username = username;
        this.points = points;
        this.gameId = gameId;
        this.difficulty = difficulty != null ? difficulty.toUpperCase() : "EASY";
        this.durationSeconds = durationSeconds;
        this.movesCount = movesCount;
        this.result = result != null ? result.toUpperCase() : "VICTORY";
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

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public int getMovesCount() {
        return movesCount;
    }

    public void setMovesCount(int movesCount) {
        this.movesCount = movesCount;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
