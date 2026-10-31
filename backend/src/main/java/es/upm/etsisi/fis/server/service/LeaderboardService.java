package es.upm.etsisi.fis.server.service;

import es.upm.etsisi.fis.server.dto.leaderboard.MatchScoreResponse;
import es.upm.etsisi.fis.server.dto.leaderboard.PlayerRankingResponse;
import es.upm.etsisi.fis.server.dto.leaderboard.ScoreResponse;
import es.upm.etsisi.fis.server.dto.leaderboard.UserStatsResponse;
import es.upm.etsisi.fis.server.entity.LeaderboardScoreEntity;
import es.upm.etsisi.fis.server.repository.LeaderboardScoreRepository;
import es.upm.etsisi.fis.server.repository.projection.PlayerRankingProjection;
import es.upm.etsisi.fis.server.repository.projection.UserStatsProjection;
import es.upm.etsisi.fis.server.util.NavalRankCalculator;
import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de negocio para la consulta de clasificaciones individuales, rankings globales y expedientes de oficiales.
 */
@Service
public class LeaderboardService {

    private final LeaderboardScoreRepository leaderboardScoreRepository;
    private final org.springframework.core.env.Environment environment;

    public LeaderboardService(LeaderboardScoreRepository leaderboardScoreRepository,
                              org.springframework.core.env.Environment environment) {
        this.leaderboardScoreRepository = leaderboardScoreRepository;
        this.environment = environment;
    }

    /**
     * Si el escalafon se encuentra vacio (en perfiles dev/test), inicializa un record de referencia para el comandante fundador.
     */
    @PostConstruct
    public void seedInitialBenchmarkScore() {
        if (java.util.Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            return;
        }
        if (leaderboardScoreRepository.count() == 0) {
            leaderboardScoreRepository.save(new LeaderboardScoreEntity(
                    "alvaro",
                    2150L,
                    "init-001",
                    "HARD",
                    94,
                    22,
                    "VICTORY"
            ));
        }
    }

    /**
     * Consulta compatible con versiones anteriores del top 10 personal.
     */
    public List<ScoreResponse> getPersonalLeaderboard(String username) {
        return leaderboardScoreRepository.findTop10ByUsernameOrderByPointsDesc(username).stream()
                .map(s -> new ScoreResponse(s.getId(), s.getUsername(), s.getPoints(), s.getGameId(), s.getRecordedAt()))
                .toList();
    }

    /**
     * Consulta compatible con versiones anteriores del top global.
     */
    public List<ScoreResponse> getGlobalLeaderboard() {
        return leaderboardScoreRepository.findGlobalTopScores().stream()
                .map(s -> new ScoreResponse(s.getId(), s.getUsername(), s.getPoints(), s.getGameId(), s.getRecordedAt()))
                .toList();
    }

    /**
     * Top 10 mejores partidas individuales disputadas en la plataforma.
     */
    public List<MatchScoreResponse> getTopMatches() {
        return leaderboardScoreRepository.findTop10ByOrderByPointsDesc().stream()
                .map(this::mapToMatchScoreResponse)
                .toList();
    }

    /**
     * Top 10 oficiales con mayor numero de puntos acumulados en su carrera.
     */
    public List<PlayerRankingResponse> getTopPlayers() {
        List<PlayerRankingProjection> projections = leaderboardScoreRepository.findTopPlayersRanking(PageRequest.of(0, 10));
        return projections.stream()
                .map(p -> {
                    long points = p.getTotalPoints() != null ? p.getTotalPoints() : 0L;
                    long games = p.getTotalGames() != null ? p.getTotalGames() : 0L;
                    long wins = p.getTotalWins() != null ? p.getTotalWins() : 0L;
                    String rank = NavalRankCalculator.calculateRank(points);
                    return new PlayerRankingResponse(p.getUsername(), points, games, wins, rank);
                })
                .toList();
    }

    /**
     * Top 10 mejores partidas disputadas por el oficial autenticado.
     */
    public List<MatchScoreResponse> getUserMatches(String username) {
        return leaderboardScoreRepository.findTop10ByUsernameOrderByPointsDesc(username).stream()
                .map(this::mapToMatchScoreResponse)
                .toList();
    }

    /**
     * Ultimas 10 partidas cronologicas disputadas por el oficial autenticado.
     */
    public List<MatchScoreResponse> getUserRecentMatches(String username) {
        return leaderboardScoreRepository.findTop10ByUsernameOrderByRecordedAtDesc(username).stream()
                .map(this::mapToMatchScoreResponse)
                .toList();
    }

    /**
     * Expediente consolidado y estadisticas de carrera del oficial.
     */
    public UserStatsResponse getUserStats(String username) {
        UserStatsProjection stats = leaderboardScoreRepository.getUserCareerStats(username);
        long totalPoints = stats != null && stats.getTotalPoints() != null ? stats.getTotalPoints() : 0L;
        long totalGames = stats != null && stats.getTotalGames() != null ? stats.getTotalGames() : 0L;
        long totalWins = stats != null && stats.getTotalWins() != null ? stats.getTotalWins() : 0L;
        long totalLosses = stats != null && stats.getTotalLosses() != null ? stats.getTotalLosses() : 0L;

        double winRate = totalGames > 0 ? ((double) totalWins / totalGames) * 100.0 : 0.0;
        // Redondear a un decimal
        winRate = Math.round(winRate * 10.0) / 10.0;

        String rank = NavalRankCalculator.calculateRank(totalPoints);
        return new UserStatsResponse(username, totalPoints, totalGames, totalWins, totalLosses, winRate, rank);
    }

    private MatchScoreResponse mapToMatchScoreResponse(LeaderboardScoreEntity entity) {
        return new MatchScoreResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getPoints(),
                entity.getGameId(),
                entity.getDifficulty() != null ? entity.getDifficulty() : "EASY",
                entity.getDurationSeconds(),
                entity.getMovesCount(),
                entity.getResult() != null ? entity.getResult() : "VICTORY",
                entity.getRecordedAt()
        );
    }
}
