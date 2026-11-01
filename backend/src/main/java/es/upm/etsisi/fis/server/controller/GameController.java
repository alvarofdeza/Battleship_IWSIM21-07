package es.upm.etsisi.fis.server.controller;

import es.upm.etsisi.fis.server.dto.game.*;
import es.upm.etsisi.fis.server.dto.leaderboard.MatchScoreResponse;
import es.upm.etsisi.fis.server.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la creacion, consulta, disparo de ataques y reclamacion de partidas en modo solitario e invitado.
 */
@RestController
@RequestMapping("/api/v1/games")
@Tag(name = "Battleship Games", description = "Game orchestration and tactical combat endpoints against Artificial Intelligence")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    @Operation(summary = "Create game against AI", description = "Initializes a new match against AI bot at chosen difficulty. Supports guest and authenticated modes.")
    public ResponseEntity<GameSummaryResponse> createGame(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody(required = false) CreateGameRequest request
    ) {
        String difficulty = (request != null && request.getAiDifficulty() != null) ? request.getAiDifficulty() : "NORMAL";
        String username = (userDetails != null) ? userDetails.getUsername() : "Guest";
        boolean isGuest = (userDetails == null);
        GameSummaryResponse response = gameService.createSoloGame(username, difficulty, isGuest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "Get game status", description = "Returns player board with ships visible and enemy board under fog of war.")
    public ResponseEntity<GameSummaryResponse> getGame(
            @PathVariable("gameId") String gameId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = (userDetails != null) ? userDetails.getUsername() : "Guest";
        GameSummaryResponse summary = gameService.getGameSummary(gameId, username);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/{gameId}/shots")
    @Operation(summary = "Execute tactical shot", description = "Submits attack coordinates [row, col]. If match continues, AI executes synchronized counter-attack.")
    public ResponseEntity<ShotResponse> fireShot(
            @PathVariable("gameId") String gameId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FireShotRequest request
    ) {
        String username = (userDetails != null) ? userDetails.getUsername() : "Guest";
        ShotResponse shotResponse = gameService.processShot(gameId, username, request.getRow(), request.getCol());
        return ResponseEntity.ok(shotResponse);
    }

    @PostMapping("/{gameId}/claim")
    @Operation(summary = "Claim guest match", description = "Associates completed guest match to authenticated officer account and records points.")
    public ResponseEntity<MatchScoreResponse> claimGame(
            @PathVariable("gameId") String gameId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        MatchScoreResponse response = gameService.claimGame(gameId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
