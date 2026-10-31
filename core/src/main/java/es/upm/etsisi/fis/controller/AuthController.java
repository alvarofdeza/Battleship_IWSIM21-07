package es.upm.etsisi.fis.controller;

import es.upm.etsisi.fis.model.User;
import es.upm.etsisi.fis.view.AuthView;
import servidor.ExternalLDAP;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para los flujos de autenticación, validación de credenciales y registro de usuarios.
 */
public class AuthController {
    private static AuthController instance;
    private final AuthView view;
    private User currentUser;
    private final Set<String> blacklistedNames;

    private AuthController() {
        this.currentUser = null;
        this.view = new AuthView();
        this.blacklistedNames = loadBlacklistedNames();
    }

    public static AuthController getInstance() {
        if (instance == null) {
            instance = new AuthController();
        }
        return instance;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public void startAuthProcess() {
        view.showWelcomeMessage();

        boolean exit = false;
        while (!exit) {
            int selected = view.showAuthMenu();

            switch (selected) {
                case 0:
                    if (loginOrRegister()) exit = true;
                    break;
                case 1:
                case -1:
                default:
                    exit = true;
                    break;
            }
        }
    }

    private Set<String> loadBlacklistedNames() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("list_raw.txt")) {
            if (inputStream == null) {
                throw new RuntimeException("Resource file list_raw.txt not found in classpath");
            }
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return Arrays.stream(content.split("\n"))
                    .filter(line -> !line.trim().isEmpty() && !line.startsWith("#"))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException("Error reading blacklisted usernames list", e);
        }
    }

    private boolean isValidName(String name) {
        if (name == null || name.length() < 3 || name.length() > 10) {
            return false;
        }
        if (!name.matches("^[a-zA-Z0-9]+$")) {
            return false;
        }
        return !blacklistedNames.contains(name.toLowerCase());
    }

    private boolean loginOrRegister() {
        String id = ExternalLDAP.LoginLDAP();

        if (id != null) {
            for (User user : UserController.getInstance().getUsers()) {
                String userId = user.getId();
                if (userId.equals(id)) {
                    currentUser = user;
                    view.showSuccessMessage("Login successful. Welcome back, " + user.getName());
                    break;
                }
            }

            if (currentUser == null) {
                String name;
                boolean nameExists;

                do {
                    name = view.getName();

                    if (name == null) {
                        view.showErrorMessage("Registration cancelled by user.");
                        return false;
                    }

                    nameExists = false;

                    for (User user : UserController.getInstance().getUsers()) {
                        if (user.getName().equalsIgnoreCase(name)) {
                            nameExists = true;
                            break;
                        }
                    }

                    if (nameExists) {
                        view.showErrorMessage("Username is already taken. Please choose another.");
                    } else if (!isValidName(name)) {
                        view.showErrorMessage("Invalid username. Must be 3-10 alphanumeric characters and not blacklisted.");
                    }
                } while (nameExists || !isValidName(name));

                User user = new User(id, name, false);
                currentUser = user;
                UserController.getInstance().addUser(currentUser);
                view.showSuccessMessage("User registration successful.");
            }
            return true;
        } else {
            view.showErrorMessage("Authentication error or login cancelled.");
            return false;
        }
    }
}
