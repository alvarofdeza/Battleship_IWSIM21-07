package es.upm.etsisi.fis.server.dto.game;

import java.util.List;

/**
 * Respuesta DTO integral para un disparo de combate naval (1 Disparo = 1 Peticion).
 * Provee en un unico viaje de red todo lo que la interfaz de usuario necesita para actualizar tableros,
 * logs cronologicos, estado de turnos y fin de combate, evitando llamadas adicionales a la API.
 */
public class ShotResponse {
    private String gameId;
    private int playerRow;
    private int playerCol;
    private String playerShotResult;
    private String playerSunkShip;

    private boolean aiCounterAttacked;
    private Integer aiRow;
    private Integer aiCol;
    private String aiShotResult;
    private String aiSunkShip;

    private boolean finished;
    private String winner;
    private String currentTurn;
    private Long points;
    private Integer movesCount;
    private Long durationSeconds;

    // Estado completo de los tableros para actualizacion inmediata sin viajes de red adicionales
    private char[][] myFleetGrid;
    private char[][] enemyFogGrid;
    private List<MoveHistoryDto> newLogs;

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

    public String getPlayerSunkShip() {
        return playerSunkShip;
    }

    public void setPlayerSunkShip(String playerSunkShip) {
        this.playerSunkShip = playerSunkShip;
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

    public String getAiSunkShip() {
        return aiSunkShip;
    }

    public void setAiSunkShip(String aiSunkShip) {
        this.aiSunkShip = aiSunkShip;
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

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Integer getMovesCount() {
        return movesCount;
    }

    public void setMovesCount(Integer movesCount) {
        this.movesCount = movesCount;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
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

    public List<MoveHistoryDto> getNewLogs() {
        return newLogs;
    }

    public void setNewLogs(List<MoveHistoryDto> newLogs) {
        this.newLogs = newLogs;
    }
}
