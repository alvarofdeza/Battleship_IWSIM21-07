package es.upm.etsisi.fis.model.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Algoritmo Experto (Probability Density Heatmap / Matriz de Calor Probabilística):
 *
 * Simula todas las proyecciones posibles (horizontales y verticales) de los barcos restantes.
 * Descarta segmentos que colisionen con fallos ('M') o barcos hundidos ('S').
 * Asigna una ponderación x50 a configuraciones que contengan impactos pendientes ('H').
 * Dispara sobre la casilla inexplorada ('?') con mayor densidad de probabilidad acumulada.
 */
public class ExpertAlgorithm implements AiAlgorithm {
    private final Random random;
    private static final int[] SHIP_LENGTHS = {5, 4, 3, 2};

    public ExpertAlgorithm() {
        this.random = new Random();
    }

    public ExpertAlgorithm(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public int[] getNextAttack(char[][] enemyBoardView) {
        if (enemyBoardView == null || enemyBoardView.length == 0) {
            return new int[]{0, 0};
        }

        int rows = enemyBoardView.length;
        int cols = enemyBoardView[0].length;
        int[][] heatmap = new int[rows][cols];

        boolean hasPendingHits = false;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (enemyBoardView[r][c] == 'H') {
                    hasPendingHits = true;
                    break;
                }
            }
            if (hasPendingHits) break;
        }

        // Evaluar proyecciones de todos los tamaños de naves
        for (int len : SHIP_LENGTHS) {
            // Proyecciones horizontales
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c <= cols - len; c++) {
                    evaluateSegment(enemyBoardView, heatmap, r, c, len, true, hasPendingHits);
                }
            }

            // Proyecciones verticales
            for (int r = 0; r <= rows - len; r++) {
                for (int c = 0; c < cols; c++) {
                    evaluateSegment(enemyBoardView, heatmap, r, c, len, false, hasPendingHits);
                }
            }
        }

        // Identificar casilla no atacada ('?') con el valor más alto en el heatmap
        int maxValue = -1;
        List<int[]> bestCoordinates = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (enemyBoardView[r][c] == '?') {
                    int val = heatmap[r][c];
                    if (val > maxValue) {
                        maxValue = val;
                        bestCoordinates.clear();
                        bestCoordinates.add(new int[]{r, c});
                    } else if (val == maxValue) {
                        bestCoordinates.add(new int[]{r, c});
                    }
                }
            }
        }

        if (!bestCoordinates.isEmpty()) {
            return bestCoordinates.get(random.nextInt(bestCoordinates.size()));
        }

        // Fallback defensivo a la primera casilla disponible
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (enemyBoardView[r][c] == '?') {
                    return new int[]{r, c};
                }
            }
        }

        return new int[]{0, 0};
    }

    private void evaluateSegment(char[][] board, int[][] heatmap, int startR, int startC,
                                 int len, boolean horizontal, boolean targetMode) {
        int hitsContained = 0;

        for (int i = 0; i < len; i++) {
            int r = horizontal ? startR : startR + i;
            int c = horizontal ? startC + i : startC;
            char cell = board[r][c];

            // Si choca con agua/fallo ('M') o con barco ya hundido ('S'), es inviable
            if (cell == 'M' || cell == 'S') {
                return;
            }
            if (cell == 'H') {
                hitsContained++;
            }
        }

        if (targetMode && hitsContained == 0) {
            return;
        }

        int weight = (hitsContained > 0) ? (50 * hitsContained) : 1;

        for (int i = 0; i < len; i++) {
            int r = horizontal ? startR : startR + i;
            int c = horizontal ? startC + i : startC;
            if (board[r][c] == '?') {
                heatmap[r][c] += weight;
            }
        }
    }
}
