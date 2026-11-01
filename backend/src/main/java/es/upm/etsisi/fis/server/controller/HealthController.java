package es.upm.etsisi.fis.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Controlador para la verificacion de salud y disponibilidad operativa (Health Check).
 * Proporciona un punto de enlace publico para plataformas cloud (Render, Docker, Kubernetes)
 * y servicios de monitorizacion keep-alive (UptimeRobot).
 * Blindado al 100%: no inyecta DataSource, no accede a BD, no registra logs INFO y devuelve no-store.
 */
@RestController
@Tag(name = "Health Check", description = "System health check and availability verification")
public class HealthController {

    private static final String SERVICE_NAME = "battleship-backend";

    /**
     * Endpoint publico para validar la operatividad del backend sin despertar la base de datos.
     *
     * @return Respuesta con codigo HTTP 200 OK, JSON estatico y cabecera Cache-Control: no-store.
     */
    @GetMapping("/healthz")
    @Operation(summary = "Check backend availability status", description = "Lightweight ping endpoint for uptime monitoring and container probes.")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(Map.of(
                        "status", "UP",
                        "service", SERVICE_NAME,
                        "timestamp", Instant.now().toString()
                ));
    }
}
