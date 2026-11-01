package es.upm.etsisi.fis.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Suite de pruebas automatizadas para garantizar el cumplimiento estricto de las politicas linguisticas
 * y de configuracion de la plataforma FleetGrid.
 */
class LanguagePolicyRegressionTest {

    private static final Pattern ACCENTED_CHAR_PATTERN = Pattern.compile("[áéíóúñÁÉÍÓÚÑ¿¡]");

    private static final Pattern[] FORBIDDEN_SPANISH_PATTERNS = {
            Pattern.compile("\\bexcepcion\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bpartida\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bcontrasena\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bdestinatario\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bmilitar\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\binvalido\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bdespachado\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bcorreo electronico\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\boficial:\\b", Pattern.CASE_INSENSITIVE)
    };

    @Test
    @DisplayName("Verifica que ningun archivo de configuracion ni plantilla por defecto apunta a gmail.com")
    void testNoDefaultConfigPointsToGmail() throws IOException {
        List<Path> configFiles = List.of(
                Paths.get("src/main/resources/application.yml"),
                Paths.get("src/main/resources/application-prod.yml"),
                Paths.get("../.env.example")
        );

        for (Path file : configFiles) {
            if (Files.exists(file)) {
                String content = Files.readString(file, StandardCharsets.UTF_8);
                assertTrue(!content.toLowerCase().contains("gmail.com"),
                        "El archivo de configuracion " + file + " contiene referencias no permitidas a gmail.com");
            }
        }
    }

    @Test
    @DisplayName("Verifica que ningun literal de cadena en codigo Java fuente contiene caracteres con tildes o eñes")
    void testNoCodeStringLiteralsContainSpanishAccents() throws IOException {
        List<Path> sourceRoots = List.of(
                Paths.get("src/main/java"),
                Paths.get("../core/src/main/java")
        );

        List<String> violations = new ArrayList<>();

        for (Path root : sourceRoots) {
            if (!Files.exists(root)) continue;

            try (Stream<Path> paths = Files.walk(root)) {
                paths.filter(p -> p.toString().endsWith(".java")).forEach(javaFile -> {
                    try {
                        String code = Files.readString(javaFile, StandardCharsets.UTF_8);
                        List<String> literals = extractStringLiterals(code);

                        for (String literal : literals) {
                            if (ACCENTED_CHAR_PATTERN.matcher(literal).find()) {
                                violations.add(javaFile.getFileName() + " contiene tildes/eñes en literal: \"" + literal + "\"");
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        assertTrue(violations.isEmpty(),
                "Se encontraron literales en codigo con tildes o caracteres especiales en espanol:\n" + String.join("\n", violations));
    }

    @Test
    @DisplayName("Verifica que ningun literal de cadena en codigo fuente contiene palabras espanolas comunes de logs o excepciones")
    void testNoCodeStringLiteralsContainCommonSpanishWords() throws IOException {
        List<Path> sourceRoots = List.of(
                Paths.get("src/main/java"),
                Paths.get("../core/src/main/java")
        );

        List<String> violations = new ArrayList<>();

        for (Path root : sourceRoots) {
            if (!Files.exists(root)) continue;

            try (Stream<Path> paths = Files.walk(root)) {
                paths.filter(p -> p.toString().endsWith(".java")).forEach(javaFile -> {
                    try {
                        String code = Files.readString(javaFile, StandardCharsets.UTF_8);
                        List<String> literals = extractStringLiterals(code);

                        for (String literal : literals) {
                            for (Pattern pattern : FORBIDDEN_SPANISH_PATTERNS) {
                                if (pattern.matcher(literal).find()) {
                                    violations.add(javaFile.getFileName() + " contiene palabra en espanol en literal: \"" + literal + "\"");
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        assertTrue(violations.isEmpty(),
                "Se encontraron palabras prohibidas en espanol dentro de literales de codigo:\n" + String.join("\n", violations));
    }

    /**
     * Extrae de forma iterativa y segura todos los literales de cadena de un archivo fuente Java,
     * ignorando comentarios de bloque y de linea sin utilizar expresiones regulares recursivas
     * que puedan provocar desbordamiento de pila (StackOverflowError).
     */
    private List<String> extractStringLiterals(String source) {
        List<String> literals = new ArrayList<>();
        int length = source.length();
        int i = 0;
        while (i < length) {
            char c = source.charAt(i);

            // Manejo de comentarios
            if (c == '/' && i + 1 < length) {
                char next = source.charAt(i + 1);
                if (next == '/') {
                    // Comentario de linea: saltar hasta el salto de linea
                    i += 2;
                    while (i < length && source.charAt(i) != '\n' && source.charAt(i) != '\r') {
                        i++;
                    }
                    continue;
                } else if (next == '*') {
                    // Comentario de bloque: saltar hasta */
                    i += 2;
                    while (i + 1 < length && !(source.charAt(i) == '*' && source.charAt(i + 1) == '/')) {
                        i++;
                    }
                    i += 2; // saltar los caracteres */
                    continue;
                }
            }

            // Manejo de literales de caracteres ('a', '\'', etc.) para no confundir comillas
            if (c == '\'') {
                i++;
                if (i < length && source.charAt(i) == '\\') {
                    i += 2; // saltar escape
                } else {
                    i++;
                }
                while (i < length && source.charAt(i) != '\'') {
                    i++;
                }
                if (i < length) i++; // saltar comilla de cierre
                continue;
            }

            // Manejo de literales de cadena ("...")
            if (c == '"') {
                StringBuilder sb = new StringBuilder();
                i++;
                boolean escaped = false;
                while (i < length) {
                    char ch = source.charAt(i);
                    if (escaped) {
                        sb.append(ch);
                        escaped = false;
                    } else if (ch == '\\') {
                        escaped = true;
                    } else if (ch == '"') {
                        i++; // fin de la cadena
                        break;
                    } else {
                        sb.append(ch);
                    }
                    i++;
                }
                literals.add(sb.toString());
                continue;
            }

            i++;
        }
        return literals;
    }
}
