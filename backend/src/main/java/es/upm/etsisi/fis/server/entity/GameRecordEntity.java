package es.upm.etsisi.fis.server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA persistente para el historial definitivo de partidas finalizadas en FleetGrid (server_games).
 * Almacena unicamente partidas concluidas con indices optimizados para el computo de clasificaciones.
 */
@Entity
@Table(
        name = "server_games",
        indexes = {
                @Index(name = "idx_games_points_desc", columnList = "points DESC, duration_seconds ASC"),
                @Index(name = "idx_games_user_points", columnList = "player1_username, points DESC"),
                @Index(name = "idx_games_user_created", columnList = "player1_username, created_at DESC")
        }
)
public class GameRecordEntity {

    @Id
    @Column(name = "id", nullable = false, length = 64)
    private String id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "player1_username", nullable = false, length = 50)
    private String player1Username;

    @Column(name = "player2_username", length = 50)
    private String player2Username;

    @Column(name = "difficulty", length = 20)
    private String difficulty;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Column(name = "moves_count")
    private Integer movesCount;

    @Column(name = "result", length = 20)
    private String result;

    @Column(name = "points")
    private Long points;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public GameRecordEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public GameRecordEntity(String id, Long userId, String player1Username, String player2Username,
                            String difficulty, Long durationSeconds, Integer movesCount,
                            String result, Long points) {
        this.id = id;
        this.userId = userId;
        this.player1Username = player1Username;
        this.player2Username = player2Username;
        this.difficulty = difficulty;
        this.durationSeconds = durationSeconds;
        this.movesCount = movesCount;
        this.result = result;
        this.points = points;
        this.createdAt = LocalDateTime.now();
    }

    // Compatibilidad con constructores anteriores
    public GameRecordEntity(String id, String player1Username, String player2Username,
                            String gameMode, String difficulty, String status) {
        this.id = id;
        this.player1Username = player1Username;
        this.player2Username = player2Username;
        this.difficulty = difficulty;
        this.result = "FINISHED".equals(status) ? "VICTORY" : status;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Metodos puente para gameId
    public String getGameId() {
        return id;
    }

    public void setGameId(String gameId) {
        this.id = gameId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPlayer1Username() {
        return player1Username;
    }

    public void setPlayer1Username(String player1Username) {
        this.player1Username = player1Username;
    }

    public String getPlayer2Username() {
        return player2Username;
    }

    public void setPlayer2Username(String player2Username) {
        this.player2Username = player2Username;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getAiDifficulty() {
        return difficulty;
    }

    public void setAiDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Integer getMovesCount() {
        return movesCount;
    }

    public void setMovesCount(Integer movesCount) {
        this.movesCount = movesCount;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStatus() {
        return "FINISHED";
    }

    public String getWinnerUsername() {
        return "VICTORY".equalsIgnoreCase(result) ? player1Username : player2Username;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
