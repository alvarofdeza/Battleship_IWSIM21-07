package es.upm.etsisi.fis.model;

import es.upm.etsisi.fis.controller.UserController;
import javax.persistence.*;
import java.util.*;

/**
 * Entidad JPA persistente que representa a un usuario registrado en el sistema.
 */
@Entity
@Table(name = "users")
public class User implements IPlayer {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Movement> movements = new LinkedList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Score> scores = new ArrayList<>();

    private transient Scanner scanner;

    protected User() {}

    /**
     * Construye un usuario con identificador único, nombre y rol administrativo.
     * @param id Correo o ID único institucional.
     * @param name Nombre para mostrar en el juego.
     * @param isAdmin Indica si el usuario goza de privilegios de administrador.
     */
    public User(String id, String name, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    public String getId() {
        return this.id;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public boolean acceptSpecialAction(ShipSpecialAction action, int remainingUses) {
        return remainingUses > 0;
    }

    @Override
    public int[] takeTurn(char[][] enemyBoardView) {
        return UserController.getInstance().requestUserTurn(enemyBoardView);
    }

    @Override
    public void addMovement(IMovement movement) {
        if (movement instanceof Movement) {
            Movement m = (Movement) movement;
            m.setUser(this);
            this.movements.add(m);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void addScore(IScore score) {
        if (score instanceof Score) {
            Score s = (Score) score;
            s.setUser(this);
            this.scores.add(s);
        }
    }

    @Override
    public List<IMovement> getMovements() {
        return new ArrayList<>(movements);
    }

    @Override
    public List<IScore> getScores() {
        return new ArrayList<>(scores);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
