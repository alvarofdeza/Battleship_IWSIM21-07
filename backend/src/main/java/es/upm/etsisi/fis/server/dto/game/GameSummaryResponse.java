package es.upm.etsisi.fis.server.dto.game;

/**
 * Respuesta DTO con el estado visual y logico completo de la partida para un jugador.
 */
public class GameSummaryResponse {
    private String gameId;
    private String player1;
    private String player2;
    private String status;
    private String currentTurn;
    private boolean finished;
    private String winner;
    private char[][] myFleetGrid;
    private char[][] enemyFogGrid;

    public GameSummaryResponse() {}

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public char[][] getMyFleetGrid() {
        return myFleetGrid;
    }

    public void setMyFleetGrid(char[][] myFleetGrid) {
        this.myFleetGrid = myFleetGrid;
    }

    public char[][] getEnemyFogGrid() {
        return enemyFogGrid;
    }

    public void setEnemyFogGrid(char[][] enemyFogGrid) {
        this.enemyFogGrid = enemyFogGrid;
    }
}
