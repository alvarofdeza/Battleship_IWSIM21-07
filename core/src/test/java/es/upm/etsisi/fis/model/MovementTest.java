package es.upm.etsisi.fis.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Suite de pruebas unitarias para la entidad Movement.
 */
class MovementTest {

    private Movement movement;
    private User user;

    @BeforeEach
    void setUp() {
        movement = new Movement(3, 4, 10L, 1000L);
        user = new User("usr1", "Gamer", false);
    }

    @Test
    @DisplayName("Constructor protegido para JPA")
    void testProtectedConstructor() throws Exception {
        Constructor<Movement> constructor = Movement.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Movement m = constructor.newInstance();
        assertThat(m).isNotNull();
    }

    @Test
    @DisplayName("Constructor parametrizado y Getters")
    void testParameterizedConstructor() {
        assertThat(movement.getRow()).isEqualTo(3);
        assertThat(movement.getColumn()).isEqualTo(4);
        assertThat(movement.getGameId()).isEqualTo(10L);
        assertThat(movement.getTimestamp()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("Setters actualizan los campos correctamente")
    void testSetters() {
        movement.setRow(5);
        movement.setColumn(6);
        movement.setGameId(20L);
        movement.setTimestamp(2000L);

        assertThat(movement.getRow()).isEqualTo(5);
        assertThat(movement.getColumn()).isEqualTo(6);
        assertThat(movement.getGameId()).isEqualTo(20L);
        assertThat(movement.getTimestamp()).isEqualTo(2000L);
    }

    @Test
    @DisplayName("Asociación con User")
    void testUserAssociation() {
        movement.setUser(user);
        assertThat(movement.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Clonación de Movement")
    void testCloneMovement() {
        IMovement clone = movement.cloneMovement();
        assertThat(clone).isNotSameAs(movement);
        assertThat(clone.getRow()).isEqualTo(movement.getRow());
        assertThat(clone.getColumn()).isEqualTo(movement.getColumn());
        assertThat(clone.getGameId()).isEqualTo(movement.getGameId());
        assertThat(clone.getTimestamp()).isEqualTo(movement.getTimestamp());
    }

    @Test
    @DisplayName("Equals y HashCode")
    void testEqualsAndHashCode() {
        Movement same = new Movement(3, 4, 10L, 1000L);
        Movement different = new Movement(1, 1, 10L, 1000L);

        assertThat(movement).isEqualTo(same);
        assertThat(movement).isNotEqualTo(different);
        assertThat(movement).isNotEqualTo(null);
        assertThat(movement.hashCode()).isEqualTo(same.hashCode());

        movement.setUser(null);
        assertDoesNotThrow(() -> movement.hashCode());
    }

    @Test
    @DisplayName("ToString contiene información relevante")
    void testToString() {
        String str = movement.toString();
        assertThat(str).contains("Movement{");
        assertThat(str).contains("row=3");
        assertThat(str).contains("column=4");
    }
}
