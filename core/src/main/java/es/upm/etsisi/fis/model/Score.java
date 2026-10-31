package es.upm.etsisi.fis.model;

import javax.persistence.*;
import java.util.Objects;

/**
 * Entidad JPA persistente que almacena el resultado numérico obtenido por un jugador en una partida.
 */
@Entity
@Table(name = "scores")
public class Score implements IScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "points", nullable = false)
    private long points;

    @Column(name = "game_id")
    private Long gameId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected Score() {}

    /**
     * Construye un registro de puntuación.
     * @param points Valor numérico de puntos alcanzados.
     * @param gameId Identificador de la partida.
     */
    public Score(long points, Long gameId) {
        this.points = points;
        this.gameId = gameId;
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
    public long getPoints() {
        return points;
    }

    @Override
    public IScore cloneScore() {
        return new Score(points, gameId);
    }

    @Override
    public void setPoints(long points) {
        this.points = points;
    }

    @Override
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    @Override
    public Long getGameId() {
        return gameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score = (Score) o;
        return points == score.points &&
                Objects.equals(gameId, score.gameId) &&
                Objects.equals(user, score.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points, gameId, user);
    }

    @Override
    public String toString() {
        return "Score{" +
                "points=" + points +
                ", gameId=" + gameId +
                '}';
    }
}
