package es.upm.etsisi.fis.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Suite de pruebas unitarias para la entidad Score.
 */
class ScoreTest {

    private Score score;
    private User user;

    @BeforeEach
    void setUp() {
        score = new Score(100L, 1L);
        user = new User("upm123", "Player1", false);
    }

    @Test
    @DisplayName("Constructor protegido para JPA inicializa con valores por defecto")
    void testProtectedConstructor() throws Exception {
        Constructor<Score> constructor = Score.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Score s = constructor.newInstance();

        assertThat(s).isNotNull();
        assertThat(s.getPoints()).isZero();
    }

    @Test
    @DisplayName("Constructor con parámetros asigna campos correctamente")
    void testParameterizedConstructor() {
        assertThat(score.getPoints()).isEqualTo(100L);
        assertThat(score.getGameId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Getters y Setters de Points y GameId")
    void testSettersAndGetters() {
        score.setPoints(200L);
        assertThat(score.getPoints()).isEqualTo(200L);

        score.setGameId(2L);
        assertThat(score.getGameId()).isEqualTo(2L);

        score.setGameId(null);
        assertThat(score.getGameId()).isNull();
    }

    @Test
    @DisplayName("Asociación con User")
    void testUserAssociation() {
        score.setUser(user);
        assertThat(score.getUser()).isEqualTo(user);

        score.setUser(null);
        assertThat(score.getUser()).isNull();
    }

    @Test
    @DisplayName("Clonación de Score")
    void testCloneScore() {
        Score clone = (Score) score.cloneScore();
        assertThat(clone).isNotSameAs(score);
        assertThat(clone.getPoints()).isEqualTo(score.getPoints());
        assertThat(clone.getGameId()).isEqualTo(score.getGameId());
    }

    @Test
    @DisplayName("Comparación por igualdad (equals)")
    void testEquals() {
        Score same = new Score(100L, 1L);
        Score different = new Score(50L, 2L);

        assertThat(score).isEqualTo(same);
        assertThat(score).isNotEqualTo(different);
        assertThat(score).isNotEqualTo(null);
        assertThat(score).isNotEqualTo(new Object());
    }

    @Test
    @DisplayName("Cálculo de HashCode")
    void testHashCode() {
        Score same = new Score(100L, 1L);
        assertThat(score.hashCode()).isEqualTo(same.hashCode());

        score.setUser(null);
        assertDoesNotThrow(() -> score.hashCode());
    }

    @Test
    @DisplayName("Representación en String")
    void testToString() {
        String str = score.toString();
        assertThat(str).contains("Score{");
        assertThat(str).contains("gameId=" + score.getGameId());
    }
}
