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
@Tag(name = "Leaderboard", description = "Endpoints para la obtencion de records, escalafones y expedientes de oficiales")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/matches")
    @Operation(summary = "Top 10 mejores partidas", description = "Devuelve el ranking de las 10 mejores partidas individuales disputadas.")
    public ResponseEntity<List<MatchScoreResponse>> getTopMatches() {
        return ResponseEntity.ok(leaderboardService.getTopMatches());
    }

    @GetMapping("/players")
    @Operation(summary = "Top 10 mejores oficiales", description = "Devuelve el ranking de los 10 oficiales con mayor suma de puntos acumulados.")
    public ResponseEntity<List<PlayerRankingResponse>> getTopPlayers() {
        return ResponseEntity.ok(leaderboardService.getTopPlayers());
    }

    @GetMapping("/me/matches")
    @Operation(summary = "Top 10 partidas personales", description = "Devuelve las 10 mejores partidas del oficial autenticado.")
    public ResponseEntity<List<MatchScoreResponse>> getMyBestMatches(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(leaderboardService.getUserMatches(userDetails.getUsername()));
    }

    @GetMapping("/me/recent")
    @Operation(summary = "Ultimas 10 partidas del oficial", description = "Devuelve el historial reciente de las ultimas 10 partidas disputadas por el oficial autenticado.")
    public ResponseEntity<List<MatchScoreResponse>> getMyRecentMatches(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(leaderboardService.getUserRecentMatches(userDetails.getUsername()));
    }

    @GetMapping("/me/stats")
    @Operation(summary = "Expediente militar del oficial", description = "Devuelve el balance de carrera, ratio de victorias y rango naval del oficial autenticado.")
    public ResponseEntity<UserStatsResponse> getMyStats(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(leaderboardService.getUserStats(userDetails.getUsername()));
    }

    @GetMapping("/personal")
    @Operation(summary = "Top 10 puntuaciones personales (Legacy)", description = "Devuelve las 10 mejores partidas del usuario autenticado en formato basico.")
    public ResponseEntity<List<ScoreResponse>> getPersonalScores(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(leaderboardService.getPersonalLeaderboard(userDetails.getUsername()));
    }

    @GetMapping("/global")
    @Operation(summary = "Clasificacion global (Legacy)", description = "Devuelve el ranking global en formato basico.")
    public ResponseEntity<List<ScoreResponse>> getGlobalScores() {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard());
    }
}
