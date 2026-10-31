package es.upm.etsisi.fis.view;

import es.upm.etsisi.fis.model.IGame;

import java.util.List;
import java.util.Scanner;

/**
 * Vista de consola para la renderización del estado de combate y cuadrículas de la partida.
 */
public class GameView {
    protected final Scanner scanner;

    public GameView(Scanner scanner) {
        this.scanner = (scanner != null) ? scanner : new Scanner(System.in);
    }

    public String selectAiDifficulty() {
        System.out.println("Select AI Difficulty (EASY, NORMAL, HARD): ");
        return scanner.nextLine().trim();
    }

    public String selectTurnAction() {
        System.out.println("Select action (1: Fire shot, 2: Special ship action, 3: Surrender): ");
        return scanner.nextLine().trim();
    }

    public void renderGame(IGame game) {
        if (game == null) return;

        System.out.println("\n================ BATTLE STATUS ================");
        System.out.println("Active Turn: " + game.getCurrentPlayerName());

        List<char[][]> boardViews = game.getBoardViews();
        if (boardViews != null && boardViews.size() >= 2) {
            System.out.println("\n--- Player 1 Grid ---");
            printGrid(boardViews.get(0));
            System.out.println("\n--- Player 2 Grid ---");
            printGrid(boardViews.get(1));
        }
        System.out.println("===============================================\n");
    }

    public void printGrid(char[][] grid) {
        if (grid == null) return;
        System.out.print("   ");
        for (int c = 0; c < grid[0].length; c++) {
            System.out.printf("%2d ", c);
        }
        System.out.println();

        for (int r = 0; r < grid.length; r++) {
            System.out.printf("%2d ", r);
            for (int c = 0; c < grid[r].length; c++) {
                char ch = grid[r][c];
                System.out.printf(" %c ", ch);
            }
            System.out.println();
        }
    }

    public void renderLegend() {
        System.out.println("Legend: '?' = Unexplored | 'M' = Miss | 'H' = Hit | 'S' = Sunk");
    }

    public void printMessage(String msg) {
        System.out.println("[INFO] " + msg);
    }

    public void printError(String err) {
        System.err.println("[ERROR] " + err);
    }

    public void pause() {
        try {
            Thread.sleep(400);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
