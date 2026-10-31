package es.upm.etsisi.fis.server.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Validador de politicas de nombres de usuario y callsigns militares.
 * Bloquea identificadores reservados del sistema (para evitar suplantacion de identidad)
 * y un lexico exhaustivo de terminos ofensivos, denigrantes o inapropiados en espanol e ingles.
 */
public final class UsernamePolicyValidator {

    private static final Set<String> RESERVED_NAMES;
    private static final Set<String> FORBIDDEN_WORDS;

    static {
        Set<String> reserved = new HashSet<>();
        // Nombres reservados del sistema, enrutamiento y roles de mando (Ingles y Espanol)
        Collections.addAll(reserved,
                "admin", "administrator", "administrador", "administracion", "administration",
                "root", "sysadmin", "system", "sistema", "null", "undefined", "nan",
                "guest", "invitado", "anonymous", "anonimo",
                "moderator", "moderador", "mod", "mods",
                "support", "soporte", "help", "ayuda", "atencion",
                "official", "oficial", "verified", "verificado",
                "battleship", "battleship-core", "battleship-backend",
                "fleetgrid", "fleetgrid-command", "fleetgrid-backend",
                "server", "servidor", "host", "localhost", "api", "endpoint", "swagger", "openapi",
                "bot", "ai", "ia", "cpu", "npc", "computer", "ordenador",
                "security", "seguridad", "auth", "login", "logout", "register", "signup",
                "owner", "dueno", "superuser", "superadmin", "staff",
                "dev", "developer", "desarrollador", "tester", "qa",
                "upm", "etsisi", "iwsym", "universidad",
                "navy", "armada", "marina", "fleet", "flota", "admiral", "almirante", "command", "mando",
                "leaderboard", "ranking", "profile", "dossier", "test", "testing"
        );
        RESERVED_NAMES = Collections.unmodifiableSet(reserved);

        Set<String> offensive = new HashSet<>();
        // Lexico de terminos ofensivos, malsonantes o de odio en ingles
        Collections.addAll(offensive,
                "fuck", "fucker", "fucking", "fuckhead", "shit", "bullshit", "bitch", "bitches",
                "asshole", "asswipe", "cunt", "dick", "dickhead", "pussy", "cock", "cocksucker",
                "motherfucker", "bastard", "slut", "whore", "nigger", "nigga", "faggot", "fag",
                "retard", "twat", "wanker", "prick", "blowjob", "nazi", "hitler", "gestapo",
                "holocaust", "fascist", "pedophile", "pedo", "rape", "rapist", "suicide",
                "terrorist", "jihad", "isis", "taliban", "kkk", "porn", "porno",
                "scam", "scammer", "phishing", "exploit", "trojan", "malware", "ransomware",
                "dumbass", "moron", "imbecile", "jackass"
        );
        // Lexico de terminos ofensivos, malsonantes o de odio en espanol
        Collections.addAll(offensive,
                "puta", "puto", "puton", "putero", "putita", "mierda", "mierdon", "cabron",
                "cabrona", "cabrones", "gilipollas", "gilipuertas", "subnormal", "retrasado",
                "mongolo", "maricon", "maricona", "marica", "mariconazo", "polla", "pollon",
                "cipote", "nabo", "coño", "coñazo", "chocho", "chochito", "pene", "vagina",
                "follar", "follador", "follada", "chingar", "chingada", "verga", "culiao",
                "pendejo", "pendeja", "zorra", "zorrilla", "perra", "guarra", "hijoputa",
                "hijadeputa", "hdp", "malparido", "carechimba", "gonorrea", "pedofilo",
                "pederasta", "violador", "violacion", "zoofilia", "necrofilia", "nazis",
                "franquista", "fascista", "yihad", "terrorista",
                "estafa", "estafador", "timo", "tramposo", "tarado", "cretino", "capullo",
                "imbecil", "estupido", "baboso"
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
