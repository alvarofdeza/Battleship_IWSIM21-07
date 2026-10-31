package es.upm.etsisi.fis.server.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite de pruebas unitarias para el servicio de generacion y validacion de tokens JWT.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L); // 1 hora

        userDetails = new User("pilot", "secretPass", Collections.emptyList());
    }

    @Test
    @DisplayName("Generacion y validacion correcta de token JWT")
    void testGenerateAndValidateToken() {
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("pilot");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("Token es invalido para otro usuario")
    void testTokenInvalidForDifferentUser() {
        String token = jwtService.generateToken(userDetails);
        UserDetails differentUser = new User("intruder", "pass", Collections.emptyList());

        assertThat(jwtService.isTokenValid(token, differentUser)).isFalse();
    }
}
