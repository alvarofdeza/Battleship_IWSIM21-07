package es.upm.etsisi.fis.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio que orquesta el ciclo de vida y la alternancia de turnos de una partida.
 */
public class Game implements IGame {
    private final Long id;
    private final IPlayer player1;
    private final IPlayer player2;
    private final Board board1;
    private final Board board2;
    private int currentTurn; // 1 = player1, 2 = player2
    private boolean finished;
    private IPlayer winner;

    public Game(IPlayer player1, IPlayer player2) {
        this(System.currentTimeMillis(), player1, player2);
    }

    public Game(Long id, IPlayer player1, IPlayer player2) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.board1 = new Board(player1);
        this.board2 = new Board(player2);
        this.currentTurn = 1;
        this.finished = false;
        this.winner = null;

        this.board1.placeRandomFleet();
        this.board2.placeRandomFleet();
    }

    public Long getId() {
        return id;
    }

    public IPlayer getPlayer1() {
        return player1;
    }

    public IPlayer getPlayer2() {
        return player2;
    }

    public Board getBoard1() {
        return board1;
    }

    public Board getBoard2() {
        return board2;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public boolean isFinished() {
        return finished;
    }

    public IPlayer getWinner() {
        return winner;
    }

    public IPlayer getCurrentAttackingPlayer() {
        return currentTurn == 1 ? player1 : player2;
    }

    public IPlayer getCurrentDefendingPlayer() {
        return currentTurn == 1 ? player2 : player1;
    }

    public Board getCurrentDefendingBoard() {
        return currentTurn == 1 ? board2 : board1;
    }

    public Board getCurrentAttackingBoard() {
        return currentTurn == 1 ? board1 : board2;
    }

    /**
     * Ejecuta el disparo del turno actual sobre el tablero defensor.
     * @param row Fila objetivo.
     * @param col Columna objetivo.
     * @return Cadena con el resultado: "Miss", "Hit", "Sunk", "Victory for ..." o errores de validación.
     */
    public String executeShot(int row, int col) {
        if (finished) {
            return "Game is already finished";
        }

        Board defendingBoard = getCurrentDefendingBoard();
        String result = defendingBoard.receiveAttack(new int[]{row, col});

        if ("Already attacked".equals(result) || "Coordinates out of bounds".equals(result)) {
            return result;
        }

        // Registrar movimiento
        IMovement movement = new Movement(row, col, this.id, System.currentTimeMillis());
        getCurrentAttackingPlayer().addMovement(movement);

        // Comprobar condición de victoria
        if (!defendingBoard.hasSurvivingShips()) {
            this.finished = true;
            this.winner = getCurrentAttackingPlayer();
            assignFinalScore();
            return "Victory for " + winner.getName();
        }

        switchTurn();
        return result;
    }

    public void switchTurn() {
        this.currentTurn = (this.currentTurn == 1) ? 2 : 1;
    }

    private void assignFinalScore() {
        if (winner != null) {
            int movesCount = winner.getMovements().size();
            long points = Math.max(20, 100 - (movesCount * 2L));
            winner.addScore(new Score(points, this.id));
        }
    }

    @Override
    public String getCurrentPlayerName() {
        return getCurrentAttackingPlayer().getName();
    }

    @Override
    public String getOpponentPlayerName() {
        return getCurrentDefendingPlayer().getName();
    }

    @Override
    public List<char[][]> getBoardViews() {
        List<char[][]> views = new ArrayList<>();
        views.add(board1.getFogOfWarView());
        views.add(board2.getFogOfWarView());
        return views;
    }
}
