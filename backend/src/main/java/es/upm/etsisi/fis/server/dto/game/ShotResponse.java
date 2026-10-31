package es.upm.etsisi.fis.server.dto.game;

/**
 * Respuesta DTO con el resultado del disparo del jugador y el contraataque correspondiente de la IA.
 */
public class ShotResponse {
    private String gameId;
    private int playerRow;
    private int playerCol;
    private String playerShotResult;

    private boolean aiCounterAttacked;
    private Integer aiRow;
    private Integer aiCol;
    private String aiShotResult;

    private boolean finished;
    private String winner;
    private String currentTurn;

    public ShotResponse() {}

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getPlayerRow() {
        return playerRow;
    }

    public void setPlayerRow(int playerRow) {
        this.playerRow = playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    public void setPlayerCol(int playerCol) {
        this.playerCol = playerCol;
    }

    public String getPlayerShotResult() {
        return playerShotResult;
    }

    public void setPlayerShotResult(String playerShotResult) {
        this.playerShotResult = playerShotResult;
    }

    public boolean isAiCounterAttacked() {
        return aiCounterAttacked;
    }

    public void setAiCounterAttacked(boolean aiCounterAttacked) {
        this.aiCounterAttacked = aiCounterAttacked;
    }

    public Integer getAiRow() {
        return aiRow;
    }

    public void setAiRow(Integer aiRow) {
        this.aiRow = aiRow;
    }

    public Integer getAiCol() {
        return aiCol;
    }

    public void setAiCol(Integer aiCol) {
        this.aiCol = aiCol;
    }

    public String getAiShotResult() {
        return aiShotResult;
    }

    public void setAiShotResult(String aiShotResult) {
        this.aiShotResult = aiShotResult;
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

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }
}
