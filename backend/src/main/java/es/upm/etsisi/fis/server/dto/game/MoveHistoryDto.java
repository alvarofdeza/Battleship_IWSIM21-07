package es.upm.etsisi.fis.server.dto.game;

import java.time.LocalDateTime;

/**
 * DTO que representa un movimiento individual en el historial de la partida.
 */
public class MoveHistoryDto {
    private Long id;
    private String gameId;
    private String playerUsername;
    private int row;
    private int col;
    private String result;
    private LocalDateTime timestamp;

    public MoveHistoryDto() {}

    public MoveHistoryDto(Long id, String gameId, String playerUsername, int row, int col, String result, LocalDateTime timestamp) {
        this.id = id;
        this.gameId = gameId;
        this.playerUsername = playerUsername;
        this.row = row;
        this.col = col;
        this.result = result;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerUsername() {
        return playerUsername;
    }

    public void setPlayerUsername(String playerUsername) {
        this.playerUsername = playerUsername;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
