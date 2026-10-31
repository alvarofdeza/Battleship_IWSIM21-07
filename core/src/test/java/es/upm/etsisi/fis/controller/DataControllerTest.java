package es.upm.etsisi.fis.controller;

import es.upm.etsisi.fis.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de pruebas unitarias para DataController.
 */
class DataControllerTest {

    private DataController controller;

    @BeforeEach
    void setUp() {
        DataController.initForTest();
        controller = DataController.getInstance();
    }

    @Test
    @DisplayName("El Singleton devuelve la misma instancia")
    void testSingletonReturnsSameInstance() {
        DataController second = DataController.getInstance();
        assertSame(controller, second);
    }

    @Test
    @DisplayName("loadUsers devuelve mapa vacío en entorno de test mockeado")
    void testLoadUsersReturnsEmptyMap() {
        Map<String, User> users = controller.loadUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("Operaciones saveOrUpdate, delete e initializeUserRelations no lanzan excepciones")
    void testSaveOrUpdateAndDeleteDoesNotThrow() {
        User user = new User("id1", "testuser", false);
        assertDoesNotThrow(() -> controller.saveOrUpdateUser(user));
        assertDoesNotThrow(() -> controller.deleteUser(user));
        assertDoesNotThrow(() -> controller.initializeUserRelations(user));
    }
}
