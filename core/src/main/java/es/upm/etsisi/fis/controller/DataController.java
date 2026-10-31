package es.upm.etsisi.fis.controller;

import es.upm.etsisi.fis.model.User;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de persistencia y transacciones con Hibernate ORM.
 */
public class DataController {
    private static DataController instance;
    private final Session session;

    private DataController(Session session) {
        this.session = session;
    }

    public static DataController getInstance(Session session) {
        if (instance == null) {
            instance = new DataController(session);
        }
        return instance;
    }

    public static DataController getInstance() {
        if (instance == null) {
            throw new RuntimeException("DataController must be initialized first using getInstance(Session session)");
        }
        return instance;
    }

    public static void setInstance(DataController mockInstance) {
        instance = mockInstance;
    }

    /**
     * Carga todos los usuarios almacenados en la base de datos relacional.
     * @return Mapa de identificador a entidad User.
     */
    public Map<String, User> loadUsers() {
        Transaction tx = session.beginTransaction();
        List<User> userList = session.createQuery("FROM User", User.class).list();
        tx.commit();
        Map<String, User> users = new HashMap<>();
        for (User user : userList) {
            users.put(user.getId(), user);
        }
        return users;
    }

    /**
     * Guarda o actualiza el estado de un usuario en la base de datos.
     * @param user Entidad usuario a persistir.
     */
    public void saveOrUpdateUser(User user) {
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(user);
        tx.commit();
    }

    /**
     * Elimina un usuario de la base de datos.
     * @param user Entidad usuario a suprimir.
     */
    public void deleteUser(User user) {
        Transaction tx = session.beginTransaction();
        session.delete(user);
        tx.commit();
    }

    /**
     * Inicializa las relaciones perezosas (Lazy) de movimientos y puntuaciones de un usuario.
     * @param user Entidad usuario a sincronizar.
     */
    public void initializeUserRelations(User user) {
        Transaction tx = session.beginTransaction();
        session.refresh(user);
        Hibernate.initialize(user.getMovements());
        Hibernate.initialize(user.getScores());
        tx.commit();
    }

    /**
     * Inicializa un mock en memoria para pruebas unitarias sin conexión a base de datos.
     */
    public static void initForTest() {
        instance = new DataController(null) {
            @Override
            public Map<String, User> loadUsers() {
                return new HashMap<>();
            }

            @Override
            public void saveOrUpdateUser(User user) {}

            @Override
            public void deleteUser(User user) {}

            @Override
            public void initializeUserRelations(User user) {}
        };
    }
}
