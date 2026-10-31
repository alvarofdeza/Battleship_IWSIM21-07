package es.upm.etsisi.fis.model;

import javax.persistence.*;
import java.util.Objects;

/**
 * Entidad JPA persistente que registra un ataque ejecutado por un jugador.
 */
@Entity
@Table(name = "movements")
public class Movement implements IMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "row_index", nullable = false)
    private int row;

    @Column(name = "column_index", nullable = false)
    private int column;

    @Column(name = "game_id")
    private Long gameId;

    @Column(name = "recorded_time", nullable = false)
    private long timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected Movement() {}

    /**
     * Construye un nuevo movimiento con coordenadas y marca de tiempo.
     * @param row Fila objetivo.
     * @param column Columna objetivo.
     * @param gameId Identificador de la partida en curso.
     * @param timestamp Marca temporal del disparo en milisegundos.
     */
    public Movement(int row, int column, Long gameId, long timestamp) {
        this.row = row;
        this.column = column;
        this.gameId = gameId;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public IMovement cloneMovement() {
        return new Movement(row, column, gameId, timestamp);
    }

    @Override
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    @Override
    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public Long getGameId() {
        return gameId;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movement movement = (Movement) o;
        return row == movement.row &&
                column == movement.column &&
                Objects.equals(gameId, movement.gameId) &&
                Objects.equals(user, movement.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column, gameId, user);
    }

    @Override
    public String toString() {
        return "Movement{" +
                "id=" + id +
                ", row=" + row +
                ", column=" + column +
                ", gameId=" + gameId +
                ", timestamp=" + timestamp +
                '}';
    }
}
