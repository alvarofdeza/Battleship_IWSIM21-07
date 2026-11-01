package es.upm.etsisi.fis.server.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import es.upm.etsisi.fis.server.dto.leaderboard.MatchScoreResponse;
import es.upm.etsisi.fis.server.dto.leaderboard.PlayerRankingResponse;
import es.upm.etsisi.fis.server.dto.leaderboard.ScoreResponse;
import es.upm.etsisi.fis.server.dto.leaderboard.UserStatsResponse;
import es.upm.etsisi.fis.server.entity.GameRecordEntity;
import es.upm.etsisi.fis.server.repository.GameRecordRepository;
import es.upm.etsisi.fis.server.repository.projection.PlayerRankingProjection;
import es.upm.etsisi.fis.server.repository.projection.UserStatsProjection;
import es.upm.etsisi.fis.server.util.NavalRankCalculator;
import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Servicio de negocio para la consulta de clasificaciones individuales, rankings globales y expedientes de oficiales.
 * Implementa cache en memoria con Caffeine (TTL 3 minutos) sobre el modelo definitivo server_games.
 */
@Service
public class LeaderboardService {

    private final GameRecordRepository gameRecordRepository;
    private final org.springframework.core.env.Environment environment;

    /**
     * Cache en memoria de las 10 mejores partidas (TTL 3 minutos).
     */
    private final Cache<String, List<MatchScoreResponse>> topMatchesCache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .maximumSize(50)
            .build();

    /**
     * Cache en memoria de los 10 mejores oficiales de la armada (TTL 3 minutos).
     */
    private final Cache<String, List<PlayerRankingResponse>> topPlayersCache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .maximumSize(50)
            .build();

    public LeaderboardService(GameRecordRepository gameRecordRepository,
                              org.springframework.core.env.Environment environment) {
        this.gameRecordRepository = gameRecordRepository;
        this.environment = environment;
    }

    /**
     * Invalida las caches en memoria del ranking cuando una partida concluye o es reclamada.
     */
    public void invalidateLeaderboardCache() {
        topMatchesCache.invalidateAll();
        topPlayersCache.invalidateAll();
    }

    /**
     * Si el escalafon se encuentra vacio (en perfiles dev/test), inicializa un record de referencia para el comandante fundador.
     */
    @PostConstruct
    public void seedInitialBenchmarkScore() {
        if (java.util.Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            return;
        }
        if (gameRecordRepository.count() == 0) {
            gameRecordRepository.save(new GameRecordEntity(
                    "init-001",
                    null,
                    "alvaro",
                    "AI Opponent",
                    "HARD",
                    94L,
                    22,
                    "VICTORY",
                    2150L
            ));
        }
    }

    /**
     * Consulta compatible con versiones anteriores del top 10 personal.
     */
    public List<ScoreResponse> getPersonalLeaderboard(String username) {
        return gameRecordRepository.findTop10ByPlayer1UsernameOrderByPointsDesc(username).stream()
                .map(g -> new ScoreResponse(0L, g.getPlayer1Username(), g.getPoints(), g.getId(), g.getCreatedAt()))
                .toList();
    }

    /**
     * Consulta compatible con versiones anteriores del top global.
     */
    public List<ScoreResponse> getGlobalLeaderboard() {
        return gameRecordRepository.findGlobalTopScores().stream()
                .map(g -> new ScoreResponse(0L, g.getPlayer1Username(), g.getPoints(), g.getId(), g.getCreatedAt()))
                .toList();
    }

    /**
     * Top 10 mejores partidas individuales disputadas en la plataforma (cacheadas con Caffeine).
     */
    public List<MatchScoreResponse> getTopMatches() {
        return topMatchesCache.get("TOP_MATCHES", k ->
                gameRecordRepository.findTopMatchesRanking(PageRequest.of(0, 10)).stream()
                        .map(this::mapToMatchScoreResponse)
                        .toList()
        );
    }

    /**
     * Top 10 oficiales con mayor numero de puntos acumulados en su carrera (cacheados con Caffeine).
     */
    public List<PlayerRankingResponse> getTopPlayers() {
        return topPlayersCache.get("TOP_PLAYERS", k -> {
            List<PlayerRankingProjection> projections = gameRecordRepository.findTopPlayersRanking(PageRequest.of(0, 10));
            return projections.stream()
                    .map(p -> {
                        long points = p.getTotalPoints() != null ? p.getTotalPoints() : 0L;
                        long games = p.getTotalGames() != null ? p.getTotalGames() : 0L;
                        long wins = p.getTotalWins() != null ? p.getTotalWins() : 0L;
                        String rank = NavalRankCalculator.calculateRank(points);
                        return new PlayerRankingResponse(p.getUsername(), points, games, wins, rank);
                    })
                    .toList();
        });
    }

    /**
     * Top 10 mejores partidas disputadas por el oficial autenticado.
     */
    public List<MatchScoreResponse> getUserMatches(String username) {
        return gameRecordRepository.findTop10ByPlayer1UsernameOrderByPointsDesc(username).stream()
                .map(this::mapToMatchScoreResponse)
                .toList();
    }

    /**
     * Ultimas 10 partidas cronologicas disputadas por el oficial autenticado.
     */
    public List<MatchScoreResponse> getUserRecentMatches(String username) {
        return gameRecordRepository.findTop10ByPlayer1UsernameOrderByCreatedAtDesc(username).stream()
                .map(this::mapToMatchScoreResponse)
                .toList();
    }

    /**
     * Estadisticas agregadas de carrera del oficial para el expediente naval.
     */
    public UserStatsResponse getUserStats(String username) {
        UserStatsProjection proj = gameRecordRepository.getUserCareerStats(username);
        long totalPoints = proj.getTotalPoints() != null ? proj.getTotalPoints() : 0L;
        long totalGames = proj.getTotalGames() != null ? proj.getTotalGames() : 0L;
        long totalWins = proj.getTotalWins() != null ? proj.getTotalWins() : 0L;
        long totalLosses = proj.getTotalLosses() != null ? proj.getTotalLosses() : 0L;

        double winRatio = totalGames > 0 ? (double) totalWins / totalGames : 0.0;
        String navalRank = NavalRankCalculator.calculateRank(totalPoints);

        return new UserStatsResponse(username, totalPoints, totalGames, totalWins, totalLosses, winRatio, navalRank);
    }

    private MatchScoreResponse mapToMatchScoreResponse(GameRecordEntity entity) {
        long duration = entity.getDurationSeconds() != null ? entity.getDurationSeconds() : 0L;
        int moves = entity.getMovesCount() != null ? entity.getMovesCount() : 0;
        String result = entity.getResult() != null ? entity.getResult() : "VICTORY";
        long points = entity.getPoints() != null ? entity.getPoints() : 0L;
        String difficulty = entity.getDifficulty() != null ? entity.getDifficulty() : "NORMAL";

        return new MatchScoreResponse(
                entity.getId(),
                entity.getPlayer1Username(),
                points,
                difficulty,
                duration,
                moves,
                result,
                entity.getCreatedAt()
        );
    }
}
