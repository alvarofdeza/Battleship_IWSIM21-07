package es.upm.etsisi.fis.server.controller;

import es.upm.etsisi.fis.server.dto.game.*;
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
 * Controlador REST para la creacion, consulta y disparo de ataques en partidas de Batalla Naval.
 */
@RestController
@RequestMapping("/api/v1/games")
@Tag(name = "Battleship Games", description = "Endpoints de gestion y juego de partidas contra la Inteligencia Artificial")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    @Operation(summary = "Crear partida contra la IA", description = "Inicializa una nueva partida contra un bot configurado en la dificultad seleccionada (EASY, NORMAL, HARD).")
    public ResponseEntity<GameSummaryResponse> createGame(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody(required = false) CreateGameRequest request
    ) {
        String difficulty = (request != null && request.getAiDifficulty() != null) ? request.getAiDifficulty() : "NORMAL";
        GameSummaryResponse response = gameService.createSoloGame(userDetails.getUsername(), difficulty);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "Consultar estado de la partida", description = "Devuelve el tablero propio con barcos visibles y el tablero enemigo bajo niebla de guerra.")
    public ResponseEntity<GameSummaryResponse> getGame(
            @PathVariable("gameId") String gameId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        GameSummaryResponse summary = gameService.getGameSummary(gameId, userDetails.getUsername());
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/{gameId}/shots")
    @Operation(summary = "Efectuar un disparo", description = "Envia coordenadas [row, col] de ataque. Si la partida continua, la IA efectua su contraataque de forma sincronizada.")
    public ResponseEntity<ShotResponse> fireShot(
            @PathVariable("gameId") String gameId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FireShotRequest request
    ) {
        ShotResponse shotResponse = gameService.processShot(gameId, userDetails.getUsername(), request.getRow(), request.getCol());
        return ResponseEntity.ok(shotResponse);
    }

    @GetMapping("/{gameId}/history")
    @Operation(summary = "Historial de disparos de la partida", description = "Devuelve la lista cronologica de todos los ataques efectuados por ambos jugadores.")
    public ResponseEntity<List<MoveHistoryDto>> getGameHistory(@PathVariable("gameId") String gameId) {
        List<MoveHistoryDto> history = gameService.getGameHistory(gameId);
        return ResponseEntity.ok(history);
    }
}
