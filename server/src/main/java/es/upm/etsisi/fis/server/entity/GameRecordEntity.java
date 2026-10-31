package es.upm.etsisi.fis.server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA persistente para el historial de partidas disputadas en la plataforma.
 */
@Entity
@Table(name = "server_games")
public class GameRecordEntity {

    @Id
    @Column(name = "game_id", nullable = false, length = 64)
    private String gameId;

    @Column(name = "player1_username", nullable = false, length = 50)
    private String player1Username;

    @Column(name = "player2_username", nullable = false, length = 50)
    private String player2Username;

    @Column(name = "game_mode", nullable = false, length = 30)
    private String gameMode;

    @Column(name = "ai_difficulty", length = 20)
    private String aiDifficulty;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "winner_username", length = 50)
    private String winnerUsername;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    public GameRecordEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public GameRecordEntity(String gameId, String player1Username, String player2Username,
                            String gameMode, String aiDifficulty, String status) {
        this.gameId = gameId;
        this.player1Username = player1Username;
        this.player2Username = player2Username;
        this.gameMode = gameMode;
        this.aiDifficulty = aiDifficulty;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
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

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public String getAiDifficulty() {
        return aiDifficulty;
    }

    public void setAiDifficulty(String aiDifficulty) {
        this.aiDifficulty = aiDifficulty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWinnerUsername() {
        return winnerUsername;
    }

    public void setWinnerUsername(String winnerUsername) {
        this.winnerUsername = winnerUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
}
