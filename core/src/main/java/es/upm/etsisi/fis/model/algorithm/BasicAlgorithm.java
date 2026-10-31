package es.upm.etsisi.fis.model.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Algoritmo Fácil: Selección pseudoaleatoria uniforme entre todas las celdas inexploradas ('?').
 */
public class BasicAlgorithm implements AiAlgorithm {
    private final Random random;

    public BasicAlgorithm() {
        this.random = new Random();
    }

    public BasicAlgorithm(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public int[] getNextAttack(char[][] enemyBoardView) {
        if (enemyBoardView == null || enemyBoardView.length == 0) {
            return new int[]{0, 0};
        }

        List<int[]> candidates = new ArrayList<>();
        int rows = enemyBoardView.length;
        int cols = enemyBoardView[0].length;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (enemyBoardView[r][c] == '?') {
                    candidates.add(new int[]{r, c});
                }
            }
        }

        if (candidates.isEmpty()) {
            return new int[]{0, 0};
        }

        return candidates.get(random.nextInt(candidates.size()));
    }
}
