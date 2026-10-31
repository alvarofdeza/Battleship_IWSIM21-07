package es.upm.etsisi.fis.server.service;

import es.upm.etsisi.fis.model.*;
import es.upm.etsisi.fis.server.dto.game.GameSummaryResponse;
import es.upm.etsisi.fis.server.dto.game.MoveHistoryDto;
import es.upm.etsisi.fis.server.dto.game.ShotResponse;
import es.upm.etsisi.fis.server.entity.GameRecordEntity;
import es.upm.etsisi.fis.server.entity.LeaderboardScoreEntity;
import es.upm.etsisi.fis.server.entity.MoveRecordEntity;
import es.upm.etsisi.fis.server.exception.ResourceNotFoundException;
import es.upm.etsisi.fis.server.repository.GameRecordRepository;
import es.upm.etsisi.fis.server.repository.LeaderboardScoreRepository;
import es.upm.etsisi.fis.server.repository.MoveRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de orquestacion de partidas, conexion con el motor de dominio y trazabilidad en base de datos.
 */
@Service
public class GameService {

    private final GameRecordRepository gameRecordRepository;
    private final MoveRecordRepository moveRecordRepository;
    private final LeaderboardScoreRepository leaderboardScoreRepository;

    private final Map<String, Game> activeSessions = new ConcurrentHashMap<>();

    public GameService(GameRecordRepository gameRecordRepository,
                       MoveRecordRepository moveRecordRepository,
                       LeaderboardScoreRepository leaderboardScoreRepository) {
        this.gameRecordRepository = gameRecordRepository;
        this.moveRecordRepository = moveRecordRepository;
        this.leaderboardScoreRepository = leaderboardScoreRepository;
    }

    @Transactional
    public GameSummaryResponse createSoloGame(String username, String difficulty) {
        String gameId = UUID.randomUUID().toString().substring(0, 8);

        IPlayer humanPlayer = new User(username, username, false);
        AiPlayer aiOpponent = AiPlayerFactory.createAiPlayer(difficulty);

        long numericId = Math.abs((long) gameId.hashCode());
        Game domainGame = new Game(numericId, humanPlayer, aiOpponent);

        activeSessions.put(gameId, domainGame);

        GameRecordEntity gameRecord = new GameRecordEntity(
                gameId,
                username,
                aiOpponent.getName(),
                "SOLO_AI",
                difficulty.toUpperCase(),
                "IN_PROGRESS"
        );
        gameRecordRepository.save(gameRecord);

        return buildGameSummary(gameId, domainGame, username);
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

        if (!domainGame.getCurrentPlayerName().equals(username)) {
            throw new IllegalStateException("It is not your turn");
        }

        // 1. Ejecutar disparo del jugador humano
        String playerResult = domainGame.executeShot(row, col);

        if ("Already attacked".equals(playerResult) || "Coordinates out of bounds".equals(playerResult)) {
            throw new IllegalArgumentException(playerResult);
        }

        moveRecordRepository.save(new MoveRecordEntity(gameId, username, row, col, playerResult));

        ShotResponse response = new ShotResponse();
        response.setGameId(gameId);
        response.setPlayerRow(row);
        response.setPlayerCol(col);
        response.setPlayerShotResult(playerResult);

        // Comprobar si el disparo humano sentencio la partida
        if (domainGame.isFinished()) {
            finalizeGame(gameId, domainGame);
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
            moveRecordRepository.save(new MoveRecordEntity(gameId, aiPlayer.getName(), aiCoords[0], aiCoords[1], aiResult));

            response.setAiCounterAttacked(true);
            response.setAiRow(aiCoords[0]);
            response.setAiCol(aiCoords[1]);
            response.setAiShotResult(aiResult);

            if (domainGame.isFinished()) {
                finalizeGame(gameId, domainGame);
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

    private void finalizeGame(String gameId, Game domainGame) {
        GameRecordEntity record = gameRecordRepository.findById(gameId).orElse(null);
        if (record != null) {
            record.setStatus("FINISHED");
            record.setWinnerUsername(domainGame.getWinner().getName());
            record.setFinishedAt(LocalDateTime.now());
            gameRecordRepository.save(record);
        }

        // Si el ganador es el jugador humano, persistir puntuacion en el leaderboard
        if (domainGame.getWinner().getName().equals(domainGame.getPlayer1().getName())) {
            int movesCount = domainGame.getPlayer1().getMovements().size();
            long points = Math.max(20, 100 - (movesCount * 2L));
            leaderboardScoreRepository.save(new LeaderboardScoreEntity(domainGame.getPlayer1().getName(), points, gameId));
        }
    }

    public List<MoveHistoryDto> getGameHistory(String gameId) {
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
