package es.upm.etsisi.fis.controller;

import es.upm.etsisi.fis.model.*;
import es.upm.etsisi.fis.view.GameView;

import java.util.Scanner;

/**
 * Controlador del flujo y lógica de combate de las partidas de Batalla Naval.
 */
public class GameController {
    private static GameController instance;
    private GameView view;
    private final Scanner scanner;

    private GameController(Scanner scanner) {
        this.scanner = (scanner != null) ? scanner : new Scanner(System.in);
        this.view = new GameView(this.scanner);
    }

    public static GameController getInstance(Scanner scanner) {
        if (instance == null) {
            instance = new GameController(scanner);
        }
        return instance;
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController(new Scanner(System.in));
        }
        return instance;
    }

    public void setView(GameView view) {
        if (view != null) {
            this.view = view;
        }
    }

    public void startMatch(IPlayer player1, IPlayer player2, IScore score, IMovement movement) {
        if (player1 == null || player2 == null) {
            view.printError("Players cannot be null");
            return;
        }

        Game game = new Game(player1, player2);
        view.printMessage("Match started between " + player1.getName() + " and " + player2.getName() + "!");

        while (!game.isFinished()) {
            IPlayer attacker = game.getCurrentAttackingPlayer();
            Board defendingBoard = game.getCurrentDefendingBoard();

            view.printMessage("--- Turn of " + attacker.getName() + " ---");

            int[] coords = attacker.takeTurn(defendingBoard.getFogOfWarView());
            if (coords == null || coords.length < 2) {
                view.printError("Invalid coordinates returned by player.");
                continue;
            }

            String shotResult = game.executeShot(coords[0], coords[1]);
            view.printMessage("Shot at [" + coords[0] + ", " + coords[1] + "]: " + shotResult);

            if (score != null && game.getWinner() == attacker) {
                score.setPoints(100);
            }
        }

        view.printMessage("Game over! Winner: " + game.getWinner().getName());
    }

    public void startSoloMatch(IPlayer player1, IScore score, IMovement movement) {
        AiPlayer aiPlayer = AiPlayerFactory.createAiPlayer("EASY");
        startMatch(player1, aiPlayer, score, movement);
    }

    public void executeCounterAttack(Game game) {
        if (game != null && !game.isFinished()) {
            IPlayer attacker = game.getCurrentAttackingPlayer();
            int[] shot = attacker.takeTurn(game.getCurrentDefendingBoard().getFogOfWarView());
            game.executeShot(shot[0], shot[1]);
        }
    }

    public void fireHeavyArtillery(Game game, IBattleship battleship) {
        if (battleship != null && battleship.getAvailableArtilleryShots() > 0 && game != null) {
            battleship.reduceArtilleryShots();
            view.printMessage("Heavy artillery barrage fired!");
        }
    }

    public void repairSubmarine(Game game, ISubmarine submarine) {
        if (submarine != null && !submarine.isRepaired()) {
            submarine.markRepaired();
            view.printMessage("Submarine hull repaired successfully!");
        }
    }

    public void deployPatrolRecon(Game game, IPatrolBoat patrolBoat) {
        if (patrolBoat != null) {
            patrolBoat.activateEvasion();
            view.printMessage("Patrol boat radar sweep deployed!");
        }
    }
}
