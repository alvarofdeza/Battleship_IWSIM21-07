package es.upm.etsisi.fis.model.algorithm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite de pruebas unitarias para los algoritmos de Inteligencia Artificial (Strategy Pattern).
 */
class AlgorithmsTest {

    private char[][] createEmptyBoard() {
        char[][] board = new char[10][10];
        for (int r = 0; r < 10; r++) {
            Arrays.fill(board[r], '?');
        }
        return board;
    }

    @Test
    @DisplayName("BasicAlgorithm selecciona casillas válidas inexploradas")
    void testBasicAlgorithm() {
        BasicAlgorithm algorithm = new BasicAlgorithm(42L);
        char[][] board = createEmptyBoard();

        // Rellenar casi todo de fallos 'M' salvo 2 casillas
        for (int r = 0; r < 10; r++) {
            Arrays.fill(board[r], 'M');
        }
        board[3][4] = '?';
        board[7][8] = '?';

        int[] attack = algorithm.getNextAttack(board);
        assertThat(attack).isIn(new int[]{3, 4}, new int[]{7, 8});

        // Tablero nulo o vacío
        assertThat(algorithm.getNextAttack(null)).isEqualTo(new int[]{0, 0});
        assertThat(algorithm.getNextAttack(new char[0][0])).isEqualTo(new int[]{0, 0});
    }

    @Test
    @DisplayName("NormalAlgorithm aplica paridad en modo Caza y adyacencia en modo Objetivo")
    void testNormalAlgorithm() {
        NormalAlgorithm algorithm = new NormalAlgorithm(12345L);
        char[][] board = createEmptyBoard();

        // 1. Modo Caza: Paridad de ajedrez (r + c) % 2 == 0
        int[] huntAttack = algorithm.getNextAttack(board);
        assertThat((huntAttack[0] + huntAttack[1]) % 2).isZero();

        // 2. Modo Objetivo (un solo impacto en 4, 4)
        board[4][4] = 'H';
        int[] targetAttack = algorithm.getNextAttack(board);
        assertThat(targetAttack).isIn(
                new int[]{3, 4}, // Norte
                new int[]{5, 4}, // Sur
                new int[]{4, 3}, // Oeste
                new int[]{4, 5}  // Este
        );

        // 3. Modo Objetivo en Línea (dos impactos en 4,4 y 4,5 -> horizontal)
        board[4][5] = 'H';
        int[] lineAttack = algorithm.getNextAttack(board);
        assertThat(lineAttack).isIn(
                new int[]{4, 3},
                new int[]{4, 6}
        );
    }

    @Test
    @DisplayName("ExpertAlgorithm prioriza fuertemente celdas vecinas a impactos no resueltos")
    void testExpertAlgorithmWithHits() {
        ExpertAlgorithm expert = new ExpertAlgorithm(999L);
        char[][] board = createEmptyBoard();

        board[5][5] = 'H';

        int[] attack = expert.getNextAttack(board);
        assertThat(attack).isIn(
                new int[]{4, 5},
                new int[]{6, 5},
                new int[]{5, 4},
                new int[]{5, 6}
        );
    }

    @Test
    @DisplayName("ExpertAlgorithm en tablero despejado prefiere casillas centrales antes que esquinas")
    void testExpertAlgorithmEmptyBoard() {
        ExpertAlgorithm expert = new ExpertAlgorithm(42L);
        char[][] board = createEmptyBoard();

        int[] attack = expert.getNextAttack(board);
        // Las esquinas tienen menor densidad de probabilidad que el centro
        assertThat(attack).isNotEqualTo(new int[]{0, 0});
        assertThat(attack).isNotEqualTo(new int[]{0, 9});
        assertThat(attack).isNotEqualTo(new int[]{9, 0});
        assertThat(attack).isNotEqualTo(new int[]{9, 9});

        assertThat(attack[0]).isBetween(0, 9);
        assertThat(attack[1]).isBetween(0, 9);
    }

    @Test
    @DisplayName("ExpertAlgorithm nunca dispara sobre agua ('M') ni barcos hundidos ('S')")
    void testExpertAlgorithmIgnoresMissesAndSunk() {
        ExpertAlgorithm expert = new ExpertAlgorithm();
        char[][] board = createEmptyBoard();

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                if (r != 7 && c != 7) {
                    board[r][c] = 'M';
                }
            }
        }
        board[7][7] = '?';

        int[] attack = expert.getNextAttack(board);
        assertThat(attack).isEqualTo(new int[]{7, 7});
    }
}
