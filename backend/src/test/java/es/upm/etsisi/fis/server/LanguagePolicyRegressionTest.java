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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Suite de pruebas automatizadas para garantizar el cumplimiento estricto de las politicas linguisticas
 * y de configuracion de la plataforma FleetGrid.
 */
class LanguagePolicyRegressionTest {

    private static final Pattern STRING_LITERAL_PATTERN = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"");
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
                        String strippedCode = stripComments(code);

                        Matcher matcher = STRING_LITERAL_PATTERN.matcher(strippedCode);
                        while (matcher.find()) {
                            String literal = matcher.group();
                            if (ACCENTED_CHAR_PATTERN.matcher(literal).find()) {
                                violations.add(javaFile.getFileName() + " contiene tildes/eñes en literal: " + literal);
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
                        String strippedCode = stripComments(code);

                        Matcher matcher = STRING_LITERAL_PATTERN.matcher(strippedCode);
                        while (matcher.find()) {
                            String literal = matcher.group();
                            for (Pattern pattern : FORBIDDEN_SPANISH_PATTERNS) {
                                if (pattern.matcher(literal).find()) {
                                    violations.add(javaFile.getFileName() + " contiene palabra en espanol en literal: " + literal);
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
     * Elimina comentarios de una linea y de bloque para inspeccionar unicamente tokens de codigo.
     */
    private String stripComments(String source) {
        // Eliminar comentarios de bloque /* ... */
        String withoutBlockComments = source.replaceAll("/\\*[\\s\\S]*?\\*/", "");
        // Eliminar comentarios de linea // ...
        return withoutBlockComments.replaceAll("//.*", "");
    }
}
