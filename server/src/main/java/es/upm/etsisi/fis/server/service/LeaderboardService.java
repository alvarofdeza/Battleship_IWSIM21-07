package es.upm.etsisi.fis.server.service;

import es.upm.etsisi.fis.server.dto.leaderboard.ScoreResponse;
import es.upm.etsisi.fis.server.repository.LeaderboardScoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de negocio para la consulta de clasificaciones individuales y globales.
 */
@Service
public class LeaderboardService {

    private final LeaderboardScoreRepository leaderboardScoreRepository;

    public LeaderboardService(LeaderboardScoreRepository leaderboardScoreRepository) {
        this.leaderboardScoreRepository = leaderboardScoreRepository;
    }

    public List<ScoreResponse> getPersonalLeaderboard(String username) {
        return leaderboardScoreRepository.findTop10ByUsernameOrderByPointsDesc(username).stream()
                .map(s -> new ScoreResponse(s.getId(), s.getUsername(), s.getPoints(), s.getGameId(), s.getRecordedAt()))
                .toList();
    }

    public List<ScoreResponse> getGlobalLeaderboard() {
        return leaderboardScoreRepository.findGlobalTopScores().stream()
                .map(s -> new ScoreResponse(s.getId(), s.getUsername(), s.getPoints(), s.getGameId(), s.getRecordedAt()))
                .toList();
    }
}
