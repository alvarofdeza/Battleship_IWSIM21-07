package es.upm.etsisi.fis.server.controller;

import es.upm.etsisi.fis.server.dto.leaderboard.ScoreResponse;
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
 * Controlador REST para la consulta de clasificaciones individuales y rankings mundiales.
 */
@RestController
@RequestMapping("/api/v1/leaderboard")
@Tag(name = "Leaderboard", description = "Endpoints para la obtencion de records y mejores puntuaciones de los jugadores")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/personal")
    @Operation(summary = "Top 10 puntuaciones personales", description = "Devuelve las 10 mejores partidas ganadas por el usuario autenticado.")
    public ResponseEntity<List<ScoreResponse>> getPersonalScores(@AuthenticationPrincipal UserDetails userDetails) {
        List<ScoreResponse> scores = leaderboardService.getPersonalLeaderboard(userDetails.getUsername());
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/global")
    @Operation(summary = "Clasificacion global", description = "Devuelve el ranking global de las mejores puntuaciones alcanzadas por todos los jugadores.")
    public ResponseEntity<List<ScoreResponse>> getGlobalScores() {
        List<ScoreResponse> scores = leaderboardService.getGlobalLeaderboard();
        return ResponseEntity.ok(scores);
    }
}
