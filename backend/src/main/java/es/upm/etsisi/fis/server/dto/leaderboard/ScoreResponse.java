package es.upm.etsisi.fis.server.dto.leaderboard;

import java.time.LocalDateTime;

/**
 * Respuesta DTO para un elemento de la tabla de clasificaciones y records.
 */
public class ScoreResponse {
    private Long id;
    private String username;
    private long points;
    private String gameId;
    private LocalDateTime recordedAt;

    public ScoreResponse() {}

    public ScoreResponse(Long id, String username, long points, String gameId, LocalDateTime recordedAt) {
        this.id = id;
        this.username = username;
        this.points = points;
        this.gameId = gameId;
        this.recordedAt = recordedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
