package es.upm.etsisi.fis.server.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de pruebas unitarias para el validador de politicas de nombres de usuario y callsigns militares.
 */
class UsernamePolicyValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "alvaro", "alvaro_07", "Ghost-Recon", "alpha_1", "Viper-99", "Task_Force_7",
            "Cadet01", "SeaWolf", "commander_x", "tactical-navy"
    })
    @DisplayName("Nombres de usuario validos con letras, numeros, guiones y barras bajas son aceptados")
    void testValidUsernamesAccepted(String username) {
        assertDoesNotThrow(() -> UsernamePolicyValidator.validate(username));
        assertTrue(UsernamePolicyValidator.isAllowed(username));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "admin", "ADMIN", "Administrator", "root", "system", "null", "undefined",
            "guest", "moderator", "support", "official", "battleship", "server", "api",
            "administrador", "soporte", "sistema", "invitado"
    })
    @DisplayName("Nombres reservados del sistema son rechazados (independiente de mayusculas)")
    void testReservedUsernamesRejected(String username) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> UsernamePolicyValidator.validate(username));
        assertTrue(ex.getMessage().contains("reserved"));
        assertFalse(UsernamePolicyValidator.isAllowed(username));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "bad_fuck_user", "shit_happens", "bitch_01", "little_asshole", "nazi_soldier", "hitler_fan",
            "puta_barata", "el_cabron_99", "maricon_x", "polla_loca", "p0ll4", "h1tl3r"
    })
    @DisplayName("Terminos ofensivos en ingles o espanol (incluyendo leetspeak basico) son rechazados")
    void testOffensiveUsernamesRejected(String username) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> UsernamePolicyValidator.validate(username));
        assertTrue(ex.getMessage().contains("inappropriate or prohibited terminology"));
        assertFalse(UsernamePolicyValidator.isAllowed(username));
    }

    @Test
    @DisplayName("Nombres nulos o vacios son rechazados con excepcion explicita")
    void testEmptyUsernameRejected() {
        assertThrows(IllegalArgumentException.class, () -> UsernamePolicyValidator.validate(null));
        assertThrows(IllegalArgumentException.class, () -> UsernamePolicyValidator.validate("   "));
        assertFalse(UsernamePolicyValidator.isAllowed(null));
        assertFalse(UsernamePolicyValidator.isAllowed(""));
    }
}
