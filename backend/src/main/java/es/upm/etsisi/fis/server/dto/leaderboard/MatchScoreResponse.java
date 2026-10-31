package es.upm.etsisi.fis.server.dto.leaderboard;

import java.time.LocalDateTime;

/**
 * Respuesta DTO detallada para una partida individual en la clasificacion.
 */
public class MatchScoreResponse {
    private Long id;
    private String username;
    private long points;
    private String gameId;
    private String difficulty;
    private long durationSeconds;
    private int movesCount;
    private String result;
    private LocalDateTime recordedAt;

    public MatchScoreResponse() {}

    public MatchScoreResponse(Long id, String username, long points, String gameId,
                              String difficulty, long durationSeconds, int movesCount,
                              String result, LocalDateTime recordedAt) {
        this.id = id;
        this.username = username;
        this.points = points;
        this.gameId = gameId;
        this.difficulty = difficulty;
        this.durationSeconds = durationSeconds;
        this.movesCount = movesCount;
        this.result = result;
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

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
