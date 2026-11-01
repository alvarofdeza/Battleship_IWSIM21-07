package es.upm.etsisi.fis.server.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Filtro HTTP en memoria con algoritmo Token Bucket (Bucket4j + Caffeine) para mitigar
 * ataques de denegacion de servicio (DoS) y fuerza bruta en endpoints publicos sin tocar la base de datos.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    @Value("${ratelimit.enabled:true}")
    private boolean rateLimitEnabled;

    /**
     * Cache en memoria para almacenar cubos de tokens por direccion IP con desalojo automatico a los 10 minutos.
     */
    private final Cache<String, Bucket> authBuckets = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .maximumSize(5000)
            .build();

    private final Cache<String, Bucket> generalBuckets = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .maximumSize(5000)
            .build();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }
        String path = request.getRequestURI();

        // El endpoint /healthz debe ser ultraligero y queda exento para UptimeRobot
        if ("/healthz".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = resolveClientIp(request);

        if (path.startsWith("/api/v1/auth/")) {
            Bucket bucket = authBuckets.get(clientIp, k -> createAuthBucket());
            if (bucket != null && !bucket.tryConsume(1)) {
                sendRateLimitExceeded(response, "Authentication rate limit exceeded. Please wait 1 minute before retrying.");
                return;
            }
        } else if (path.startsWith("/api/v1/")) {
            Bucket bucket = generalBuckets.get(clientIp, k -> createGeneralBucket());
            if (bucket != null && !bucket.tryConsume(1)) {
                sendRateLimitExceeded(response, "API rate limit exceeded. Too many tactical commands dispatched.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Bucket createAuthBucket() {
        // 15 intentos por minuto para autenticacion / OTP
        return Bucket.builder()
                .addLimit(Bandwidth.classic(15, Refill.greedy(15, Duration.ofMinutes(1))))
                .build();
    }

    private Bucket createGeneralBucket() {
        // 120 peticiones por minuto para navegacion / juego
        return Bucket.builder()
                .addLimit(Bandwidth.classic(120, Refill.greedy(120, Duration.ofMinutes(1))))
                .build();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
    }

    private void sendRateLimitExceeded(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"Too Many Requests\",\"message\":\"" + message + "\"}");
    }
}
