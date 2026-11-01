package es.upm.etsisi.fis.server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio de generacion, firma criptografica y validacion de JSON Web Tokens (JWT).
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms:86400000}")
    private long jwtExpiration;

    private final Environment environment;

    public JwtService() {
        this.environment = null;
    }

    public JwtService(Environment environment) {
        this.environment = environment;
    }

    public JwtService(String secretKey, long jwtExpiration, Environment environment) {
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
        this.environment = environment;
    }

    @PostConstruct
    public void validateJwtSecret() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException("CRITICAL SECURITY ERROR: JWT secret must not be blank.");
        }
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("CRITICAL SECURITY ERROR: JWT secret must be at least 256 bits (32 bytes). Current length: " + (keyBytes.length * 8) + " bits.");
        }
        if (environment != null && Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            List<String> insecureSecrets = List.of(
                    "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
                    "secret",
                    "default",
                    "changeme",
                    "change-me",
                    "your-secret-key-here"
            );
            for (String insecure : insecureSecrets) {
                if (secretKey.equalsIgnoreCase(insecure) || secretKey.toLowerCase().contains("changeme")) {
                    throw new IllegalStateException("CRITICAL SECURITY VIOLATION: Default or weak development JWT secret detected in production profile! Configure a high-entropy secret via JWT_SECRET.");
                }
            }
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
