package es.upm.etsisi.fis.server.service;

import es.upm.etsisi.fis.model.*;
import es.upm.etsisi.fis.server.dto.game.GameSummaryResponse;
import es.upm.etsisi.fis.server.dto.game.MoveHistoryDto;
import es.upm.etsisi.fis.server.dto.game.ShotResponse;
import es.upm.etsisi.fis.server.dto.leaderboard.MatchScoreResponse;
import es.upm.etsisi.fis.server.entity.GameRecordEntity;
import es.upm.etsisi.fis.server.entity.LeaderboardScoreEntity;
import es.upm.etsisi.fis.server.entity.MoveRecordEntity;
import es.upm.etsisi.fis.server.exception.ResourceNotFoundException;
import es.upm.etsisi.fis.server.repository.GameRecordRepository;
import es.upm.etsisi.fis.server.repository.LeaderboardScoreRepository;
import es.upm.etsisi.fis.server.repository.MoveRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de orquestacion de partidas, conexion con el motor de dominio, modo invitado y trazabilidad en base de datos.
 */
@Service
public class GameService {

    /**
     * Metadatos en memoria para sesiones de juego activas o de invitados.
     */
    public static class GameSessionMetadata {
        private final String gameId;
        private String username;
        private final String difficulty;
        private final LocalDateTime createdAt;
        private final boolean guest;
        private boolean finished;
        private Long finalPoints;
        private Integer finalMovesCount;
        private Long finalDurationSeconds;
        private String finalResult;
        private String winnerUsername;
        private boolean claimed;
        private final List<MoveHistoryDto> inMemoryMoves = new java.util.concurrent.CopyOnWriteArrayList<>();

        public GameSessionMetadata(String gameId, String username, String difficulty, LocalDateTime createdAt, boolean guest) {
            this.gameId = gameId;
            this.username = username;
            this.difficulty = difficulty;
            this.createdAt = createdAt;
            this.guest = guest;
            this.finished = false;
            this.claimed = false;
        }

        public String getGameId() { return gameId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getDifficulty() { return difficulty; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public boolean isGuest() { return guest; }
        public boolean isFinished() { return finished; }
        public void setFinished(boolean finished) { this.finished = finished; }
        public Long getFinalPoints() { return finalPoints; }
        public void setFinalPoints(Long finalPoints) { this.finalPoints = finalPoints; }
        public Integer getFinalMovesCount() { return finalMovesCount; }
        public void setFinalMovesCount(Integer finalMovesCount) { this.finalMovesCount = finalMovesCount; }
        public Long getFinalDurationSeconds() { return finalDurationSeconds; }
        public void setFinalDurationSeconds(Long finalDurationSeconds) { this.finalDurationSeconds = finalDurationSeconds; }
        public String getFinalResult() { return finalResult; }
        public void setFinalResult(String finalResult) { this.finalResult = finalResult; }
        public String getWinnerUsername() { return winnerUsername; }
        public void setWinnerUsername(String winnerUsername) { this.winnerUsername = winnerUsername; }
        public boolean isClaimed() { return claimed; }
        public void setClaimed(boolean claimed) { this.claimed = claimed; }
        public List<MoveHistoryDto> getInMemoryMoves() { return inMemoryMoves; }
        public void addMove(String playerUsername, int row, int col, String result) {
            long moveId = inMemoryMoves.size() + 1L;
            inMemoryMoves.add(new MoveHistoryDto(moveId, gameId, playerUsername, row, col, result, LocalDateTime.now()));
        }
    }

    private final GameRecordRepository gameRecordRepository;
    private final MoveRecordRepository moveRecordRepository;
    private final LeaderboardScoreRepository leaderboardScoreRepository;

    private final Map<String, Game> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, GameSessionMetadata> sessionMetadata = new ConcurrentHashMap<>();

    public GameService(GameRecordRepository gameRecordRepository,
                       MoveRecordRepository moveRecordRepository,
                       LeaderboardScoreRepository leaderboardScoreRepository) {
        this.gameRecordRepository = gameRecordRepository;
        this.moveRecordRepository = moveRecordRepository;
        this.leaderboardScoreRepository = leaderboardScoreRepository;
    }

    @Transactional
    public GameSummaryResponse createSoloGame(String username, String difficulty) {
        return createSoloGame(username, difficulty, false);
    }

    @Transactional
    public GameSummaryResponse createSoloGame(String username, String difficulty, boolean isGuest) {
        String effectiveUsername = (username != null && !username.isBlank()) ? username : "Guest";
        String gameId = UUID.randomUUID().toString().substring(0, 8);

        IPlayer humanPlayer = new User(effectiveUsername, effectiveUsername, false);
        AiPlayer aiOpponent = AiPlayerFactory.createAiPlayer(difficulty);

        long numericId = Math.abs((long) gameId.hashCode());
        Game domainGame = new Game(numericId, humanPlayer, aiOpponent);

        activeSessions.put(gameId, domainGame);

        GameSessionMetadata metadata = new GameSessionMetadata(
                gameId,
                effectiveUsername,
                difficulty != null ? difficulty.toUpperCase() : "NORMAL",
                LocalDateTime.now(),
                isGuest
        );
        sessionMetadata.put(gameId, metadata);

        // Si no es invitado, persistir inmediatamente en base de datos
        if (!isGuest) {
            GameRecordEntity gameRecord = new GameRecordEntity(
                    gameId,
                    effectiveUsername,
                    aiOpponent.getName(),
                    "SOLO_AI",
                    metadata.getDifficulty(),
                    "IN_PROGRESS"
            );
            gameRecordRepository.save(gameRecord);
        }

        return buildGameSummary(gameId, domainGame, effectiveUsername);
    }

    public GameSummaryResponse getGameSummary(String gameId, String username) {
        Game domainGame = activeSessions.get(gameId);
        if (domainGame == null) {
            GameRecordEntity record = gameRecordRepository.findById(gameId)
                    .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + gameId));

            GameSummaryResponse response = new GameSummaryResponse();
            response.setGameId(record.getGameId());
            response.setPlayer1(record.getPlayer1Username());
            response.setPlayer2(record.getPlayer2Username());
            response.setStatus(record.getStatus());
            response.setFinished("FINISHED".equals(record.getStatus()));
            response.setWinner(record.getWinnerUsername());
            return response;
        }

        return buildGameSummary(gameId, domainGame, username);
    }

    @Transactional
    public ShotResponse processShot(String gameId, String username, int row, int col) {
        Game domainGame = activeSessions.get(gameId);
        if (domainGame == null) {
            throw new ResourceNotFoundException("Active game not found with ID: " + gameId);
        }

        if (domainGame.isFinished()) {
            throw new IllegalStateException("Game is already finished");
        }

        String effectiveUsername = (username != null && !username.isBlank()) ? username : "Guest";
        if (!domainGame.getCurrentPlayerName().equals(effectiveUsername) && !domainGame.getCurrentPlayerName().equals("Guest")) {
            throw new IllegalStateException("It is not your turn");
        }

        GameSessionMetadata metadata = sessionMetadata.get(gameId);
        boolean isGuest = metadata != null && metadata.isGuest();

        // 1. Ejecutar disparo del jugador humano
        String playerResult = domainGame.executeShot(row, col);

        if ("Already attacked".equals(playerResult) || "Coordinates out of bounds".equals(playerResult)) {
            throw new IllegalArgumentException(playerResult);
        }

        if (!isGuest) {
            moveRecordRepository.save(new MoveRecordEntity(gameId, effectiveUsername, row, col, playerResult));
        }
        if (metadata != null) {
            metadata.addMove(effectiveUsername, row, col, playerResult);
        }

        ShotResponse response = new ShotResponse();
        response.setGameId(gameId);
        response.setPlayerRow(row);
        response.setPlayerCol(col);
        response.setPlayerShotResult(playerResult);

        // Comprobar si el disparo humano sentencio la partida
        if (domainGame.isFinished()) {
            finalizeGame(gameId, domainGame, response);
            response.setFinished(true);
            response.setWinner(domainGame.getWinner().getName());
            response.setCurrentTurn(null);
            response.setAiCounterAttacked(false);
            return response;
        }

        // 2. Si la partida continua y el oponente es IA, realizar contraataque automatico
        IPlayer opponent = domainGame.getPlayer2();
        if (opponent instanceof AiPlayer aiPlayer) {
            Board playerBoard = domainGame.getBoard1();
            int[] aiCoords = aiPlayer.takeTurn(playerBoard.getFogOfWarView());

            String aiResult = domainGame.executeShot(aiCoords[0], aiCoords[1]);
            if (!isGuest) {
                moveRecordRepository.save(new MoveRecordEntity(gameId, aiPlayer.getName(), aiCoords[0], aiCoords[1], aiResult));
            }
            if (metadata != null) {
                metadata.addMove(aiPlayer.getName(), aiCoords[0], aiCoords[1], aiResult);
            }

            response.setAiCounterAttacked(true);
            response.setAiRow(aiCoords[0]);
            response.setAiCol(aiCoords[1]);
            response.setAiShotResult(aiResult);

            if (domainGame.isFinished()) {
                finalizeGame(gameId, domainGame, response);
                response.setFinished(true);
                response.setWinner(domainGame.getWinner().getName());
                response.setCurrentTurn(null);
                return response;
            }
        }

        response.setFinished(false);
        response.setCurrentTurn(domainGame.getCurrentPlayerName());
        return response;
    }

    private void finalizeGame(String gameId, Game domainGame, ShotResponse response) {
        LocalDateTime finishedAt = LocalDateTime.now();
        GameSessionMetadata metadata = sessionMetadata.get(gameId);
        boolean isGuest = metadata != null && metadata.isGuest();

        GameRecordEntity record = !isGuest ? gameRecordRepository.findById(gameId).orElse(null) : null;

        long durationSeconds = 1L;
        String difficulty = "EASY";
        LocalDateTime startedAt = (metadata != null) ? metadata.getCreatedAt() : (record != null ? record.getCreatedAt() : null);
        if (startedAt != null) {
            durationSeconds = Math.max(1L, Duration.between(startedAt, finishedAt).getSeconds());
        }
        if (metadata != null) {
            difficulty = metadata.getDifficulty();
        } else if (record != null && record.getAiDifficulty() != null) {
            difficulty = record.getAiDifficulty().toUpperCase();
        }

        double diffMult = calculateDifficultyMultiplier(difficulty);
        String humanUsername = domainGame.getPlayer1().getName();
        boolean isVictory = domainGame.getWinner() != null && domainGame.getWinner().getName().equals(humanUsername);
        int humanMoves = domainGame.getPlayer1().getMovements().size();

        long points = 0L;
        String resultString = isVictory ? "VICTORY" : "DEFEAT";

        if (isVictory) {
            long shotsBonus = Math.max(0L, (60 - humanMoves) * 25L);
            long timeBonus = Math.max(0L, (180 - durationSeconds) * 5L);
            points = Math.round(((1000 + shotsBonus) * diffMult) + timeBonus);
        } else {
            // Puntos de consolacion de honor en derrota completa (minimo 4 movimientos para evitar abandonos)
            if (humanMoves >= 4) {
                long consolationSum = 0L;
                for (Ship ship : domainGame.getBoard2().getShips()) {
                    if (!ship.isAlive()) {
                        consolationSum += (long) ship.getLength() * 50L;
                    }
                }
                points = Math.round(consolationSum * diffMult);
            }
        }

        if (metadata != null) {
            metadata.setFinished(true);
            metadata.setFinalPoints(points);
            metadata.setFinalMovesCount(humanMoves);
            metadata.setFinalDurationSeconds(durationSeconds);
            metadata.setFinalResult(resultString);
            metadata.setWinnerUsername(domainGame.getWinner() != null ? domainGame.getWinner().getName() : null);
        }

        if (!isGuest) {
            if (record != null) {
                record.setStatus("FINISHED");
                record.setWinnerUsername(domainGame.getWinner() != null ? domainGame.getWinner().getName() : null);
                record.setFinishedAt(finishedAt);
                record.setDurationSeconds(durationSeconds);
                record.setMovesCount(humanMoves);
                record.setPoints(points);
                gameRecordRepository.save(record);
            }

            LeaderboardScoreEntity scoreEntity = new LeaderboardScoreEntity(
                    humanUsername,
                    points,
                    gameId,
                    difficulty,
                    durationSeconds,
                    humanMoves,
                    resultString
            );
            leaderboardScoreRepository.save(scoreEntity);
        }

        if (response != null) {
            response.setPoints(points);
            response.setMovesCount(humanMoves);
            response.setDurationSeconds(durationSeconds);
        }
    }

    @Transactional
    public MatchScoreResponse claimGame(String gameId, String authenticatedUsername) {
        GameSessionMetadata metadata = sessionMetadata.get(gameId);
        if (metadata == null) {
            throw new ResourceNotFoundException("Active or recent game session not found for ID: " + gameId);
        }

        if (!metadata.isFinished()) {
            throw new IllegalStateException("Game is still in progress. Complete the battle before claiming points.");
        }

        if (metadata.isClaimed() || leaderboardScoreRepository.existsByGameId(gameId)) {
            throw new IllegalStateException("Game " + gameId + " has already been claimed.");
        }

        metadata.setClaimed(true);
        metadata.setUsername(authenticatedUsername);

        String winner = "Guest".equalsIgnoreCase(metadata.getWinnerUsername()) ?
                authenticatedUsername : metadata.getWinnerUsername();

        GameRecordEntity gameRecord = new GameRecordEntity(
                gameId,
                authenticatedUsername,
                "AI-" + metadata.getDifficulty(),
                "SOLO_AI",
                metadata.getDifficulty(),
                "FINISHED"
        );
        gameRecord.setWinnerUsername(winner);
        gameRecord.setCreatedAt(metadata.getCreatedAt());
        gameRecord.setFinishedAt(LocalDateTime.now());
        gameRecord.setDurationSeconds(metadata.getFinalDurationSeconds());
        gameRecord.setMovesCount(metadata.getFinalMovesCount());
        gameRecord.setPoints(metadata.getFinalPoints());
        gameRecordRepository.save(gameRecord);

        LeaderboardScoreEntity scoreEntity = new LeaderboardScoreEntity(
                authenticatedUsername,
                metadata.getFinalPoints(),
                gameId,
                metadata.getDifficulty(),
                metadata.getFinalDurationSeconds(),
                metadata.getFinalMovesCount(),
                metadata.getFinalResult()
        );
        leaderboardScoreRepository.save(scoreEntity);

        // Persistir movimientos acumulados en memoria al reclamar la partida
        for (MoveHistoryDto move : metadata.getInMemoryMoves()) {
            String mover = "Guest".equalsIgnoreCase(move.getPlayerUsername()) ? authenticatedUsername : move.getPlayerUsername();
            moveRecordRepository.save(new MoveRecordEntity(gameId, mover, move.getRow(), move.getCol(), move.getResult()));
        }

        return new MatchScoreResponse(
                scoreEntity.getId(),
                authenticatedUsername,
                scoreEntity.getPoints(),
                gameId,
                scoreEntity.getDifficulty(),
                scoreEntity.getDurationSeconds(),
                scoreEntity.getMovesCount(),
                scoreEntity.getResult(),
                scoreEntity.getRecordedAt()
        );
    }

    private double calculateDifficultyMultiplier(String difficulty) {
        if (difficulty == null) return 1.0;
        return switch (difficulty.toUpperCase()) {
            case "HARD", "EXPERT", "DIFICIL" -> 2.5;
            case "NORMAL", "MEDIUM" -> 1.5;
            default -> 1.0;
        };
    }

    public List<MoveHistoryDto> getGameHistory(String gameId) {
        GameSessionMetadata metadata = sessionMetadata.get(gameId);
        if (metadata != null && !metadata.getInMemoryMoves().isEmpty()) {
            return metadata.getInMemoryMoves();
        }
        return moveRecordRepository.findByGameIdOrderByTimestampAsc(gameId).stream()
                .map(m -> new MoveHistoryDto(m.getId(), m.getGameId(), m.getPlayerUsername(), m.getRow(), m.getCol(), m.getResult(), m.getTimestamp()))
                .toList();
    }

    private GameSummaryResponse buildGameSummary(String gameId, Game game, String username) {
        GameSummaryResponse response = new GameSummaryResponse();
        response.setGameId(gameId);
        response.setPlayer1(game.getPlayer1().getName());
        response.setPlayer2(game.getPlayer2().getName());
        response.setStatus(game.isFinished() ? "FINISHED" : "IN_PROGRESS");
        response.setCurrentTurn(game.getCurrentPlayerName());
        response.setFinished(game.isFinished());
        response.setWinner(game.getWinner() != null ? game.getWinner().getName() : null);

        // Cuadricula propia con barcos visibles
        response.setMyFleetGrid(game.getBoard1().getOwnerView());
        // Cuadricula enemiga con niebla de guerra
        response.setEnemyFogGrid(game.getBoard2().getFogOfWarView());

        return response;
    }
}
