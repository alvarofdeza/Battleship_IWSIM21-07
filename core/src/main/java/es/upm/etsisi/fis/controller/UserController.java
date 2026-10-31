package es.upm.etsisi.fis.controller;

import es.upm.etsisi.fis.model.*;
import es.upm.etsisi.fis.view.UsersView;

import java.util.*;

/**
 * Controlador para la gestión de acciones de usuario, partidas y visualización de puntuaciones.
 */
public class UserController {
    private static UserController instance;
    private final Map<String, User> users;
    private final UsersView view;

    private UserController(Scanner scanner) {
        this.view = new UsersView(scanner);
        this.users = loadUsersFromData();
    }

    public static UserController getInstance(Scanner scanner) {
        if (instance == null) {
            instance = new UserController(scanner);
        }
        return instance;
    }

    public static UserController getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UserController not yet initialized with Scanner.");
        }
        return instance;
    }

    public void startUserProcess() {
        view.showWelcome();
        boolean exit = false;
        while (!exit) {
            int selected = view.showUserMenu();

            switch (selected) {
                case 0: // Play Game
                    IMovement movement = new Movement(0, 0, null, 0);
                    IScore score = new Score(0, null);
                    User currentUser = getUser(AuthController.getInstance().getCurrentUser().getId());
                    AiPlayer aiOpponent = AiPlayerFactory.createAiPlayer(promptAiDifficulty());
                    GameController.getInstance().startMatch(currentUser, aiOpponent, score, movement);
                    exit = true;
                    break;
                case 1: // View Scores
                    startScoresProcess();
                    break;
                case 2: // Delete Account
                    if ("y".equalsIgnoreCase(view.confirmAccountDeletion()) || "s".equalsIgnoreCase(view.confirmAccountDeletion())) {
                        User userToDelete = getUser(AuthController.getInstance().getCurrentUser().getId());
                        DataController.getInstance().deleteUser(userToDelete);
                        users.remove(AuthController.getInstance().getCurrentUser().getId());
                        view.showSuccessMessage("Your account has been deleted successfully.");
                        exit = true;
                    } else {
                        view.showErrorMessage("Operation cancelled.");
                    }
                    break;
                case 3: // Sign Out
                default:
                    exit = true;
                    break;
            }
        }
    }

    private String promptAiDifficulty() {
        List<String> validLevels = List.of("EASY", "NORMAL", "HARD", "FACIL", "DIFICIL");
        boolean valid = false;
        String level = "EASY";
        while (!valid) {
            level = view.selectAiDifficulty().toUpperCase();
            if (validLevels.contains(level)) {
                valid = true;
            } else {
                view.showErrorMessage("Invalid difficulty level. Please choose EASY, NORMAL, or HARD.");
            }
        }
        return level;
    }

    private void startScoresProcess() {
        boolean exit = false;
        while (!exit) {
            int selected = view.showScoresSubmenu();
            switch (selected) {
                case 0: // Top 10 personal
                    view.showUserTopScores(getRankedUserScores());
                    break;
                case 1: // Global (Admin)
                    if (getUser(AuthController.getInstance().getCurrentUser().getId()).isAdmin()) {
                        view.showAllUsersScores(getRankedGlobalScores());
                    } else {
                        view.showErrorMessage("Access denied. Administrator privileges required.");
                    }
                    break;
                case 2: // Back
                default:
                    exit = true;
                    break;
            }
        }
    }

    public List<Map.Entry<String, Long>> getRankedGlobalScores() {
        return users.values().stream()
                .flatMap(user -> user.getScores().stream()
                        .map(score -> Map.entry(user.getName(), score.getPoints())))
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .toList();
    }

    public List<IScore> getRankedUserScores() {
        return getUser(AuthController.getInstance().getCurrentUser().getId()).getScores().stream()
                .sorted(Comparator.comparingLong(IScore::getPoints).reversed())
                .limit(10)
                .toList();
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public List<String> getUserIds() {
        return new ArrayList<>(users.keySet());
    }

    private Map<String, User> loadUsersFromData() {
        return DataController.getInstance().loadUsers();
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
        DataController.getInstance().saveOrUpdateUser(user);
        DataController.getInstance().initializeUserRelations(user);
    }

    public void deleteUser(User user) {
        users.remove(user.getId());
    }

    public int[] requestUserTurn(char[][] enemyBoardView) {
        if (enemyBoardView == null || enemyBoardView.length == 0) {
            throw new IllegalArgumentException("Invalid enemy board view");
        }
        int row = -1, col = -1;
        boolean validCell = false;
        do {
            int[] coords = view.requestCoordinates(enemyBoardView.length, enemyBoardView[0].length);
            row = coords[0];
            col = coords[1];

            if (row < 0 || row >= enemyBoardView.length || col < 0 || col >= enemyBoardView[0].length) {
                view.showErrorMessage("Coordinates out of bounds!");
                continue;
            }

            if (enemyBoardView[row][col] == '?') {
                validCell = true;
            } else {
                view.showErrorMessage("That cell has already been targeted! Choose another.");
            }
        } while (!validCell);

        return new int[]{row, col};
    }
}
