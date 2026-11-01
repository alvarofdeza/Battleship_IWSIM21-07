package es.upm.etsisi.fis.server.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    @DisplayName("Validacion de clave secreta corta lanza excepcion de seguridad")
    void testValidateJwtSecretTooShortThrowsException() {
        ReflectionTestUtils.setField(jwtService, "secretKey", "short-secret-under-32-bytes");
        assertThatThrownBy(() -> jwtService.validateJwtSecret())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 256 bits");
    }

    @Test
    @DisplayName("Clave por defecto en perfil prod lanza excepcion critica")
    void testValidateJwtSecretDefaultInProdThrowsException() {
        Environment prodEnv = Mockito.mock(Environment.class);
        Mockito.when(prodEnv.getActiveProfiles()).thenReturn(new String[]{"prod"});

        JwtService prodJwtService = new JwtService(prodEnv);
        ReflectionTestUtils.setField(prodJwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");

        assertThatThrownBy(prodJwtService::validateJwtSecret)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CRITICAL SECURITY VIOLATION");
    }

    @Test
    @DisplayName("Clave fuerte y personalizada en perfil prod supera validacion")
    void testValidateJwtSecretStrongInProdPasses() {
        Environment prodEnv = Mockito.mock(Environment.class);
        Mockito.when(prodEnv.getActiveProfiles()).thenReturn(new String[]{"prod"});

        JwtService prodJwtService = new JwtService(prodEnv);
        ReflectionTestUtils.setField(prodJwtService, "secretKey", "9f8e7d6c5b4a3928170f1e2d3c4b5a69788796a5b4c3d2e1f0a9b8c7d6e5f4a3");

        // No debe lanzar ninguna excepcion
        prodJwtService.validateJwtSecret();
    }
}
