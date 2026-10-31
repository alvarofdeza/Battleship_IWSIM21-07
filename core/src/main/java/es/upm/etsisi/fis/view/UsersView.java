package es.upm.etsisi.fis.view;

import es.upm.etsisi.fis.model.IScore;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Vista de consola y cuadros de diálogo para interacción y estadísticas de usuarios.
 */
public class UsersView implements BaseView {
    private final Scanner scanner;

    public UsersView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showWelcome() {
        System.out.println("\n--- Welcome to Battleship Combat Platform ---\n");
    }

    public int showUserMenu() {
        String[] options = {"Play Game", "View Leaderboard", "Delete Account", "Sign Out"};
        return JOptionPane.showOptionDialog(
                null,
                "Select an option:",
                "Player Menu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);
    }

    public String confirmAccountDeletion() {
        System.out.print("Are you sure you want to delete your account? (y/n): ");
        return scanner.nextLine().trim().toLowerCase();
    }

    public int showScoresSubmenu() {
        String[] options = {"View My Top 10 Scores", "View Global Leaderboard (Admin)", "Back to Main Menu"};
        return JOptionPane.showOptionDialog(
                null,
                "Select a view:",
                "Scores & Leaderboards",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);
    }

    public void showUserTopScores(List<IScore> topScores) {
        System.out.println("\n========= My Top 10 High Scores =========");
        if (topScores.isEmpty()) {
            System.out.println("No scores recorded yet.\n");
            return;
        }

        System.out.printf("%-10s | %-10s\n", "Rank", "Points");
        System.out.println("-----------|-----------");
        for (int i = 0; i < topScores.size(); i++) {
            System.out.printf("%-10d | %-10d\n", i + 1, topScores.get(i).getPoints());
        }
        System.out.println();
    }

    public void showAllUsersScores(List<Map.Entry<String, Long>> allScores) {
        System.out.println("\n========= Global Leaderboard =========");
        if (allScores.isEmpty()) {
            System.out.println("No scores recorded yet.\n");
            return;
        }

        System.out.printf("%-10s | %-10s | %-20s\n", "Rank", "Points", "Player");
        System.out.println("-----------|------------|----------------------");
        for (int i = 0; i < allScores.size(); i++) {
            Map.Entry<String, Long> entry = allScores.get(i);
            System.out.printf("%-10d | %-10d | %-20s\n", i + 1, entry.getValue(), entry.getKey());
        }
        System.out.println();
    }

    public int[] requestCoordinates(int maxRows, int maxCols) {
        int row = -1, col = -1;
        System.out.println("\n--- Your Turn ---");
        while (true) {
            System.out.print("Enter row (0-" + (maxRows - 1) + "): ");
            String inputRow = scanner.nextLine().trim();
            try {
                row = Integer.parseInt(inputRow);
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        while (true) {
            System.out.print("Enter column (0-" + (maxCols - 1) + "): ");
            String inputCol = scanner.nextLine().trim();
            try {
                col = Integer.parseInt(inputCol);
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return new int[]{row, col};
    }

    public String selectAiDifficulty() {
        System.out.println("Select AI Difficulty (EASY, NORMAL, HARD):");
        return scanner.nextLine().trim();
    }

    @Override
    public void showSuccessMessage(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    @Override
    public void showErrorMessage(String message) {
        System.err.println("[ERROR] " + message + "\n");
    }
}
