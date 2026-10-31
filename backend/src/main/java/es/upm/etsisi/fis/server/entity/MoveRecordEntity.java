package es.upm.etsisi.fis.server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA persistente que registra cada disparo efectuado en una partida.
 */
@Entity
@Table(name = "server_moves")
public class MoveRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", nullable = false, length = 64)
    private String gameId;

    @Column(name = "player_username", nullable = false, length = 50)
    private String playerUsername;

    @Column(name = "row_index", nullable = false)
    private int row;

    @Column(name = "column_index", nullable = false)
    private int col;

    @Column(name = "result", nullable = false, length = 30)
    private String result;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public MoveRecordEntity() {
        this.timestamp = LocalDateTime.now();
    }

    public MoveRecordEntity(String gameId, String playerUsername, int row, int col, String result) {
        this.gameId = gameId;
        this.playerUsername = playerUsername;
        this.row = row;
        this.col = col;
        this.result = result;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
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
