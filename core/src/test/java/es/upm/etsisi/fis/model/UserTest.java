package es.upm.etsisi.fis.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite de pruebas unitarias para la entidad User.
 */
class UserTest {
    private User user;
    private Movement movement;
    private Score score;

    @BeforeEach
    void setUp() {
        user = new User("upm123", "Alice", false);
        movement = new Movement(1, 1, 1L, 1000L);
        score = new Score(100L, 1L);
    }

    @Test
    @DisplayName("Constructor protegido para JPA instanciable por reflexión")
    void testProtectedConstructor() throws Exception {
        Constructor<User> constructor = User.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        User u = constructor.newInstance();
        assertThat(u).isNotNull();
    }

    @Test
    @DisplayName("Constructor con parámetros y Getters")
    void testParameterizedConstructorAndGetters() {
        assertThat(user.getId()).isEqualTo("upm123");
        assertThat(user.getName()).isEqualTo("Alice");
        assertThat(user.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("Asignación de Scanner transitorio")
    void testSetScanner() {
        Scanner scanner = new Scanner(System.in);
        user.setScanner(scanner);
        // Sin excepción
        assertThat(user).isNotNull();
    }

    @Test
    @DisplayName("Añadir movimiento válido e inválido")
    void testAddMovement() {
        user.addMovement(movement);
        assertThat(user.getMovements()).hasSize(1);
        assertThat(user.getMovements().get(0)).isEqualTo(movement);

        user.addMovement(null);
        assertThat(user.getMovements()).hasSize(1);
    }

    @Test
    @DisplayName("Añadir puntuación válida")
    void testAddScore() {
        user.addScore(score);
        assertThat(user.getScores()).hasSize(1);
        assertThat(user.getScores().get(0)).isEqualTo(score);
    }

    @Test
    @DisplayName("Equals y HashCode basados en ID")
    void testEqualsAndHashCode() {
        User sameId = new User("upm123", "Bob", true);
        User differentId = new User("upm456", "Alice", false);

        assertThat(user).isEqualTo(sameId);
        assertThat(user).isNotEqualTo(differentId);
        assertThat(user).isNotEqualTo(null);
        assertThat(user.hashCode()).isEqualTo(sameId.hashCode());
    }

    @Test
    @DisplayName("ToString contiene ID y nombre")
    void testToString() {
        String str = user.toString();
        assertThat(str).contains("User{");
        assertThat(str).contains("id='upm123'");
        assertThat(str).contains("name='Alice'");
    }

    @Test
    @DisplayName("Aceptar acción complementaria según usos restantes")
    void testAcceptSpecialAction() {
        assertThat(user.acceptSpecialAction(null, 1)).isTrue();
        assertThat(user.acceptSpecialAction(null, 0)).isFalse();
        assertThat(user.acceptSpecialAction(null, -1)).isFalse();
    }
}
