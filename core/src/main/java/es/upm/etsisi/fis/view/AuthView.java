package es.upm.etsisi.fis.view;

import javax.swing.*;

/**
 * Vista de interfaz para la autenticación y registro de usuarios.
 */
public class AuthView implements BaseView {

    public AuthView() {}

    public void showWelcomeMessage() {
        System.out.println("\n==========================================");
        System.out.println("  Battleship Platform - Authentication");
        System.out.println("==========================================\n");
    }

    public int showAuthMenu() {
        String[] options = {"Sign In / Register", "Exit"};
        return JOptionPane.showOptionDialog(
                null,
                "Select an option:",
                "Authentication Menu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);
    }

    public String getName() {
        return JOptionPane.showInputDialog(
                null,
                "Enter your username (3-10 alphanumeric characters):",
                "User Registration",
                JOptionPane.QUESTION_MESSAGE
        );
    }

    @Override
    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(null, "Success: " + message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, "Error: " + message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
