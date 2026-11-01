package es.upm.etsisi.fis.server.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import es.upm.etsisi.fis.model.*;
import es.upm.etsisi.fis.server.dto.game.GameSummaryResponse;
import es.upm.etsisi.fis.server.dto.game.MoveHistoryDto;
import es.upm.etsisi.fis.server.dto.game.ShotResponse;
import es.upm.etsisi.fis.server.dto.leaderboard.MatchScoreResponse;
import es.upm.etsisi.fis.server.entity.GameRecordEntity;
import es.upm.etsisi.fis.server.entity.UserEntity;
import es.upm.etsisi.fis.server.exception.ResourceNotFoundException;
import es.upm.etsisi.fis.server.repository.GameRecordRepository;
import es.upm.etsisi.fis.server.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Servicio de orquestacion de partidas navales, conexion con el motor de dominio puro,
 * modo invitado y persistencia optimizada en el modelo definitivo de dos tablas.
 *
 * Presupuesto de base de datos blindado:
 * - Durante la partida (creacion y disparos intermedios): 0 consultas y 0 inserts a PostgreSQL.
 * - Al finalizar la partida: exactamente 1 INSERT en server_games (si es oficial) o en memoria (si es invitado).
 * - Sesiones de juego en memoria gestionadas con Caffeine (TTL 1 hora, maximo 200 sesiones simultaneas).
 */
@Service
public class GameService {

    /**
     * Metadatos en memoria para sesiones de juego activas o pendientes de reclamacion.
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

        public MoveHistoryDto addMove(String playerUsername, int row, int col, String result) {
            long moveId = inMemoryMoves.size() + 1L;
            MoveHistoryDto move = new MoveHistoryDto(moveId, gameId, playerUsername, row, col, result, LocalDateTime.now());
            inMemoryMoves.add(move);
            return move;
        }
    }

    private final GameRecordRepository gameRecordRepository;
    private final UserRepository userRepository;
    private final LeaderboardService leaderboardService;

    /**
     * Cache en memoria de sesiones activas (TTL 1 hora, maximo 200 sesiones concurrentes).
     */
    private final Cache<String, Game> activeSessions = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(200)
            .build();

    /**
     * Cache en memoria de metadatos de sesion (TTL 1 hora, maximo 200 sesiones concurrentes).
     */
    private final Cache<String, GameSessionMetadata> sessionMetadata = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(200)
            .build();

    public GameService(GameRecordRepository gameRecordRepository,
                       UserRepository userRepository,
                       LeaderboardService leaderboardService) {
        this.gameRecordRepository = gameRecordRepository;
        this.userRepository = userRepository;
        this.leaderboardService = leaderboardService;
    }

    public GameSummaryResponse createSoloGame(String username, String difficulty) {
        return createSoloGame(username, difficulty, false);
    }

    /**
     * Crea una nueva sesion de combate contra la IA.
     * Cero consultas e inserciones en base de datos: la sesion reside exclusivamente en memoria.
     */
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

        return buildGameSummary(gameId, domainGame, effectiveUsername);
    }

    public GameSummaryResponse getGameSummary(String gameId, String username) {
        Game domainGame = activeSessions.getIfPresent(gameId);
        if (domainGame == null) {
            GameRecordEntity record = gameRecordRepository.findById(gameId)
                    .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + gameId));

            GameSummaryResponse response = new GameSummaryResponse();
            response.setGameId(record.getId());
            response.setPlayer1(record.getPlayer1Username());
            response.setPlayer2(record.getPlayer2Username());
            response.setStatus("FINISHED");
            response.setFinished(true);
            response.setWinner(record.getWinnerUsername());
            return response;
        }

        return buildGameSummary(gameId, domainGame, username);
    }

    /**
     * Procesa un disparo de artilleria naval y sincroniza el contraataque de la IA.
     * Devuelve en una sola respuesta integral el estado completo del combate (1 Disparo = 1 Peticion).
     */
    @Transactional
    public ShotResponse processShot(String gameId, String username, int row, int col) {
        Game domainGame = activeSessions.getIfPresent(gameId);
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

        GameSessionMetadata metadata = sessionMetadata.getIfPresent(gameId);
        boolean isGuest = metadata != null && metadata.isGuest();

        List<MoveHistoryDto> newLogs = new ArrayList<>();

        // 1. Ejecutar disparo del jugador humano (en memoria)
        String playerResult = domainGame.executeShot(row, col);

        if ("Already attacked".equals(playerResult) || "Coordinates out of bounds".equals(playerResult)) {
            throw new IllegalArgumentException(playerResult);
        }

        if (metadata != null) {
            MoveHistoryDto playerMove = metadata.addMove(effectiveUsername, row, col, playerResult);
            newLogs.add(playerMove);
        }

        ShotResponse response = new ShotResponse();
        response.setGameId(gameId);
        response.setPlayerRow(row);
        response.setPlayerCol(col);
        response.setPlayerShotResult(playerResult);

        // 2. Ejecutar contraataque de la IA si la partida no ha concluido
        if (!domainGame.isFinished()) {
            IPlayer opponent = domainGame.getPlayer2();
            if (opponent instanceof AiPlayer aiPlayer) {
                Board playerBoard = domainGame.getBoard1();
                int[] aiCoords = aiPlayer.takeTurn(playerBoard.getFogOfWarView());
                if (aiCoords != null) {
                    String aiResult = domainGame.executeShot(aiCoords[0], aiCoords[1]);
                    response.setAiCounterAttacked(true);
                    response.setAiRow(aiCoords[0]);
                    response.setAiCol(aiCoords[1]);
                    response.setAiShotResult(aiResult);

                    if (metadata != null) {
                        MoveHistoryDto aiMove = metadata.addMove(aiPlayer.getName(), aiCoords[0], aiCoords[1], aiResult);
                        newLogs.add(aiMove);
                    }
                }
            }
        }

        // 3. Evaluar estado de fin de batalla naval
        boolean isFinished = domainGame.isFinished();
        response.setFinished(isFinished);
        response.setWinner(domainGame.getWinner() != null ? domainGame.getWinner().getName() : null);
        response.setCurrentTurn(domainGame.getCurrentPlayerName());

        // Inyectar estado de tableros y logs para resolver en un unico viaje de red
        response.setMyFleetGrid(domainGame.getBoard1().getOwnerView());
        response.setEnemyFogGrid(domainGame.getBoard2().getFogOfWarView());
        response.setNewLogs(newLogs);

        if (isFinished) {
            finalizeGameSession(gameId, domainGame, metadata, isGuest, response);
        }

        return response;
    }

    private void finalizeGameSession(String gameId, Game domainGame, GameSessionMetadata metadata,
                                     boolean isGuest, ShotResponse response) {
        LocalDateTime createdAt = metadata != null ? metadata.getCreatedAt() : LocalDateTime.now();
        LocalDateTime finishedAt = LocalDateTime.now();
        long durationSeconds = Math.max(1, Duration.between(createdAt, finishedAt).getSeconds());

        String humanUsername = metadata != null ? metadata.getUsername() : "Guest";
        String difficulty = metadata != null ? metadata.getDifficulty() : "NORMAL";

        int humanMoves = 0;
        int enemyShipsDestroyed = 0;
        int enemyHullPointsDestroyed = 0;

        if (domainGame.getBoard2() != null && domainGame.getBoard2().getShips() != null) {
            for (Ship ship : domainGame.getBoard2().getShips()) {
                if (!ship.isAlive()) {
                    enemyShipsDestroyed++;
                    enemyHullPointsDestroyed += getShipPointsValue(ship);
                }
            }
        }

        if (metadata != null) {
            for (MoveHistoryDto move : metadata.getInMemoryMoves()) {
                if (humanUsername.equals(move.getPlayerUsername())) {
                    humanMoves++;
                }
            }
        }
        if (humanMoves == 0) humanMoves = 1;

        boolean humanWon = domainGame.getWinner() != null && domainGame.getWinner().getName().equals(humanUsername);

        long points;
        double diffMultiplier = switch (difficulty.toUpperCase()) {
            case "HARD" -> 1.5;
            case "NORMAL" -> 1.0;
            case "EASY" -> 0.75;
            default -> 1.0;
        };

        if (humanWon) {
            long bonusShots = Math.max(0, 100 - humanMoves) * 10L;
            long bonusTime = Math.max(0, 300 - durationSeconds) * 2L;
            points = Math.round(((1000 + bonusShots) * diffMultiplier) + bonusTime);
        } else {
            // Puntos de consolacion anti-farming en derrota (minimo 4 movimientos y destruccion mutua)
            if (humanMoves >= 4 && enemyShipsDestroyed > 0) {
                points = Math.round(enemyHullPointsDestroyed * diffMultiplier);
            } else {
                points = 0L;
            }
        }

        String resultString = humanWon ? "VICTORY" : "DEFEAT";

        if (metadata != null) {
            metadata.setFinished(true);
            metadata.setFinalPoints(points);
            metadata.setFinalMovesCount(humanMoves);
            metadata.setFinalDurationSeconds(durationSeconds);
            metadata.setFinalResult(resultString);
            metadata.setWinnerUsername(domainGame.getWinner() != null ? domainGame.getWinner().getName() : null);
        }

        // Si el oficial no es invitado, persistir inmediatamente (exactamente 1 INSERT)
        if (!isGuest && !"Guest".equalsIgnoreCase(humanUsername)) {
            Long userId = null;
            var userOpt = userRepository.findByUsername(humanUsername);
            if (userOpt.isPresent()) {
                userId = userOpt.get().getId();
            }

            GameRecordEntity gameRecord = new GameRecordEntity(
                    gameId,
                    userId,
                    humanUsername,
                    domainGame.getPlayer2().getName(),
                    difficulty,
                    durationSeconds,
                    humanMoves,
                    resultString,
                    points
            );
            gameRecord.setCreatedAt(createdAt);
            gameRecordRepository.save(gameRecord);

            leaderboardService.invalidateLeaderboardCache();
        }

        if (response != null) {
            response.setPoints(points);
            response.setMovesCount(humanMoves);
            response.setDurationSeconds(durationSeconds);
        }
    }

    /**
     * Reclama los puntos de una partida jugada en modo invitado y los consolida en la cuenta del oficial.
     * Ejecuta exactamente 1 INSERT en server_games e invalida la cache de clasificaciones.
     */
    @Transactional
    public MatchScoreResponse claimGame(String gameId, String authenticatedUsername) {
        GameSessionMetadata metadata = sessionMetadata.getIfPresent(gameId);
        if (metadata == null) {
            throw new ResourceNotFoundException("Active or recent game session not found for ID: " + gameId);
        }

        if (!metadata.isFinished()) {
            throw new IllegalStateException("Game is still in progress. Complete the battle before claiming points.");
        }

        if (metadata.isClaimed() || gameRecordRepository.existsById(gameId)) {
            throw new IllegalStateException("Game " + gameId + " has already been claimed.");
        }

        metadata.setClaimed(true);
        metadata.setUsername(authenticatedUsername);

        String winner = "Guest".equalsIgnoreCase(metadata.getWinnerUsername()) ?
                authenticatedUsername : metadata.getWinnerUsername();

        Long userId = null;
        var userOpt = userRepository.findByUsername(authenticatedUsername);
        if (userOpt.isPresent()) {
            userId = userOpt.get().getId();
        }

        GameRecordEntity gameRecord = new GameRecordEntity(
                gameId,
                userId,
                authenticatedUsername,
                "AI-" + metadata.getDifficulty(),
                metadata.getDifficulty(),
                metadata.getFinalDurationSeconds(),
                metadata.getFinalMovesCount(),
                metadata.getFinalResult(),
                metadata.getFinalPoints()
        );
        gameRecord.setCreatedAt(metadata.getCreatedAt());
        gameRecordRepository.save(gameRecord);

        leaderboardService.invalidateLeaderboardCache();

        return new MatchScoreResponse(
                gameId,
                authenticatedUsername,
                metadata.getFinalPoints(),
                metadata.getDifficulty(),
                metadata.getFinalDurationSeconds(),
                metadata.getFinalMovesCount(),
                metadata.getFinalResult(),
                metadata.getCreatedAt()
        );
    }

    public GameSessionMetadata getSessionMetadata(String gameId) {
        return sessionMetadata.getIfPresent(gameId);
    }

    private int getShipPointsValue(Ship ship) {
        if (ship == null) return 0;
        return switch (ship.getLength()) {
            case 5 -> 250; // Carrier
            case 4 -> 200; // Battleship
            case 3 -> 150; // Submarine
            case 2 -> 100; // Patrol Boat
            default -> ship.getLength() * 40;
        };
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
