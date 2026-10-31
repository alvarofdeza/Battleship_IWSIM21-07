package es.upm.etsisi.fis.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Controlador para la verificacion de salud y disponibilidad operativa (Health Check).
 * Proporciona un punto de enlace publico para plataformas cloud (Render, Docker, Kubernetes)
 * y servicios de monitorizacion keep-alive (UptimeRobot).
 */
@RestController
@Tag(name = "Health Check", description = "Verificacion del estado y disponibilidad del servidor")
public class HealthController {

    /**
     * Endpoint publico para validar la operatividad del backend.
     *
     * @return Respuesta con codigo HTTP 200 OK y metadatos de estado del servicio.
     */
    @GetMapping("/healthz")
    @Operation(summary = "Comprueba el estado de disponibilidad del backend")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "battleship-backend",
                "timestamp", Instant.now().toString()
        ));
    }
}
