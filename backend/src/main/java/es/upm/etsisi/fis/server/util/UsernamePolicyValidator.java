package es.upm.etsisi.fis.server.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Validador de politicas de nombres de usuario y callsigns militares.
 * Bloquea identificadores reservados del sistema (para evitar suplantacion de identidad)
 * y terminos ofensivos, denigrantes o inapropiados en espanol e ingles.
 */
public final class UsernamePolicyValidator {

    private static final Set<String> RESERVED_NAMES;
    private static final Set<String> FORBIDDEN_WORDS;

    static {
        Set<String> reserved = new HashSet<>();
        // Nombres reservados del sistema y roles administrativos (Ingles y Espanol)
        Collections.addAll(reserved,
                "admin", "administrator", "administrador", "root", "system", "sistema",
                "null", "undefined", "guest", "invitado", "moderator", "moderador", "mod",
                "support", "soporte", "help", "ayuda", "official", "oficial", "battleship",
                "server", "servidor", "api", "bot", "ai", "security", "seguridad",
                "owner", "superuser", "staff", "dev", "developer", "desarrollador",
                "upm", "etsisi", "navy", "armada", "admiral", "almirante"
        );
        RESERVED_NAMES = Collections.unmodifiableSet(reserved);

        Set<String> offensive = new HashSet<>();
        // Terminos ofensivos, malsonantes o de odio (Ingles)
        Collections.addAll(offensive,
                "fuck", "shit", "bitch", "asshole", "cunt", "dick", "nigger", "nigga",
                "faggot", "whore", "slut", "nazi", "hitler", "rape", "rapist",
                "cock", "bastard", "pedophile"
        );
        // Terminos ofensivos, malsonantes o de odio (Espanol)
        Collections.addAll(offensive,
                "puta", "puto", "mierda", "cabron", "cabrona", "gilipollas", "maricon",
                "marica", "polla", "coño", "follar", "nazis", "pene", "zorra",
                "subnormal", "hijoputa", "pedofilo", "violador"
        );
        FORBIDDEN_WORDS = Collections.unmodifiableSet(offensive);
    }

    private UsernamePolicyValidator() {
        // Constructor privado para clase utilitaria
    }

    /**
     * Valida si un nombre de usuario cumple con la politica de nombres permitidos.
     *
     * @param username Nombre de usuario a evaluar.
     * @throws IllegalArgumentException Si el nombre de usuario es nulo, reservado o contiene terminos prohibidos.
     */
    public static void validate(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }

        String lower = username.trim().toLowerCase(Locale.ROOT);

        // 1. Comprobacion de nombres reservados del sistema (coincidencia exacta)
        if (RESERVED_NAMES.contains(lower)) {
            throw new IllegalArgumentException("Username '" + username + "' is reserved and cannot be used.");
        }

        // 2. Normalizacion para deteccion de variantes (leetspeak basico y separadores)
        String normalized = lower
                .replace("_", "")
                .replace("-", "")
                .replace("0", "o")
                .replace("1", "i")
                .replace("3", "e")
                .replace("4", "a")
                .replace("5", "s")
                .replace("7", "t");

        // 3. Comprobacion de contenido ofensivo o lenguaje denigrante
        for (String forbidden : FORBIDDEN_WORDS) {
            if (lower.contains(forbidden) || normalized.contains(forbidden)) {
                throw new IllegalArgumentException("Username contains inappropriate or prohibited terminology.");
            }
        }
    }

    /**
     * Comprueba de manera no bloqueante si un nombre de usuario esta permitido.
     *
     * @param username Nombre de usuario a verificar.
     * @return true si cumple con la politica; false en caso contrario.
     */
    public static boolean isAllowed(String username) {
        try {
            validate(username);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
