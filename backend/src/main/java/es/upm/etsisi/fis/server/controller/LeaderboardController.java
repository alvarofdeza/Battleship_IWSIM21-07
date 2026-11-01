package es.upm.etsisi.fis.server.controller;

import es.upm.etsisi.fis.server.dto.leaderboard.MatchScoreResponse;
import es.upm.etsisi.fis.server.dto.leaderboard.PlayerRankingResponse;
import es.upm.etsisi.fis.server.dto.leaderboard.ScoreResponse;
import es.upm.etsisi.fis.server.dto.leaderboard.UserStatsResponse;
import es.upm.etsisi.fis.server.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para la consulta de clasificaciones individuales, rankings globales y expedientes de oficiales.
 */
@RestController
@RequestMapping("/api/v1/leaderboard")
@Tag(name = "Leaderboard", description = "Endpoints for Admiralty Roll rankings, high scores, and officer service records")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/matches")
    @Operation(summary = "Top 10 matches", description = "Returns top 10 individual matches sorted by score.")
    public ResponseEntity<List<MatchScoreResponse>> getTopMatches() {
        return ResponseEntity.ok(leaderboardService.getTopMatches());
    }

    @GetMapping("/players")
    @Operation(summary = "Top 10 officers", description = "Returns top 10 officers ordered by total career points.")
    public ResponseEntity<List<PlayerRankingResponse>> getTopPlayers() {
        return ResponseEntity.ok(leaderboardService.getTopPlayers());
    }

    @GetMapping("/me/matches")
    @Operation(summary = "Officer top 10 matches", description = "Returns the top 10 personal matches of the authenticated officer.")
    public ResponseEntity<List<MatchScoreResponse>> getMyBestMatches(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(leaderboardService.getUserMatches(userDetails.getUsername()));
    }

    @GetMapping("/me/recent")
    @Operation(summary = "Officer recent matches", description = "Returns the 10 most recent matches played by the authenticated officer.")
    public ResponseEntity<List<MatchScoreResponse>> getMyRecentMatches(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(leaderboardService.getUserRecentMatches(userDetails.getUsername()));
    }

    @GetMapping("/me/stats")
    @Operation(summary = "Officer service record", description = "Returns career stats, win ratio, and naval rank for the authenticated officer.")
    public ResponseEntity<UserStatsResponse> getMyStats(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(leaderboardService.getUserStats(userDetails.getUsername()));
    }

    @GetMapping("/personal")
    @Operation(summary = "Personal high scores (Legacy)", description = "Returns personal top scores in legacy compatibility format.")
    public ResponseEntity<List<ScoreResponse>> getPersonalScores(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(leaderboardService.getPersonalLeaderboard(userDetails.getUsername()));
    }

    @GetMapping("/global")
    @Operation(summary = "Global leaderboard (Legacy)", description = "Returns global leaderboard in legacy compatibility format.")
    public ResponseEntity<List<ScoreResponse>> getGlobalScores() {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard());
    }
}
