package servidor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Suite de pruebas unitarias para el emulador de autenticacion externa LDAP.
 */
class ExternalLDAPTest {

    private boolean originalInteractiveState;

    @BeforeEach
    void setUp() {
        originalInteractiveState = ExternalLDAP.isInteractiveEnabled();
        ExternalLDAP.setInteractiveEnabled(false);
    }

    @AfterEach
    void tearDown() {
        ExternalLDAP.setInteractiveEnabled(originalInteractiveState);
    }

    @Test
    @DisplayName("Debe instanciarse correctamente sin errores")
    void shouldInstantiateSuccessfully() {
        ExternalLDAP externalLDAP = new ExternalLDAP();
        assertThat(externalLDAP).isNotNull();
    }

    @Test
    @DisplayName("Debe ejecutar LoginLDAP de forma segura en modo no interactivo devolviendo null")
    void shouldExecuteLoginLdapSafelyWithoutExceptions() {
        assertDoesNotThrow(() -> {
            String result = ExternalLDAP.LoginLDAP();
            assertThat(result).isNull();
        });
    }

    @Test
    @DisplayName("Debe permitir conmutar el estado del modo interactivo")
    void shouldToggleInteractiveState() {
        ExternalLDAP.setInteractiveEnabled(true);
        assertThat(ExternalLDAP.isInteractiveEnabled()).isTrue();
        ExternalLDAP.setInteractiveEnabled(false);
        assertThat(ExternalLDAP.isInteractiveEnabled()).isFalse();
    }
}
