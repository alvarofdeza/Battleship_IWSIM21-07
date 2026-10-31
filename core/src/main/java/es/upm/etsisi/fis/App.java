package es.upm.etsisi.fis;

import es.upm.etsisi.fis.controller.AuthController;
import es.upm.etsisi.fis.controller.DataController;
import es.upm.etsisi.fis.controller.GameController;
import es.upm.etsisi.fis.controller.UserController;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Scanner;

/**
 * Punto de entrada principal para la ejecución de la aplicación Battleship.
 */
public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (
                SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
                Session session = sessionFactory.openSession()
        ) {
            DataController dataController = DataController.getInstance(session);
            UserController userController = UserController.getInstance(scanner);
            AuthController authController = AuthController.getInstance();
            GameController.getInstance(scanner);

            authController.startAuthProcess();

            if (authController.getCurrentUser() != null) {
                authController.getCurrentUser().setScanner(scanner);

                userController.startUserProcess();

                if (userController.getUserIds().contains(authController.getCurrentUser().getId())) {
                    dataController.saveOrUpdateUser(authController.getCurrentUser());
                }
            } else {
                System.out.println("Session not initiated. Exiting application.\n");
            }
        } finally {
            scanner.close();
        }
    }
}
