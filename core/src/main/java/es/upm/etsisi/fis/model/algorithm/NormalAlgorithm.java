package es.upm.etsisi.fis.model.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Algoritmo Normal (Hunt & Target):
 * - Modo Objetivo (Target): Cuando detecta impactos 'H' en naves aún no hundidas, busca vecinos
 *   ortogonales (N, S, E, O) o continúa la línea de fuego si hay impactos contiguos.
 * - Modo Caza (Hunt): Si no hay impactos pendientes, dispara siguiendo un patrón de paridad
 *   de tablero de ajedrez (row + col) % 2 == 0 para optimizar la búsqueda.
 */
public class NormalAlgorithm implements AiAlgorithm {
    private final Random random;

    public NormalAlgorithm() {
        this.random = new Random();
    }

    public NormalAlgorithm(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public int[] getNextAttack(char[][] enemyBoardView) {
        if (enemyBoardView == null || enemyBoardView.length == 0) {
            return new int[]{0, 0};
        }

        int rows = enemyBoardView.length;
        int cols = enemyBoardView[0].length;

        // 1. Identificar casillas impactadas ('H') pendientes de hundir
        List<int[]> pendingHits = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (enemyBoardView[r][c] == 'H') {
                    pendingHits.add(new int[]{r, c});
                }
            }
        }

        // Modo Objetivo (Target Mode)
        if (!pendingHits.isEmpty()) {
            int[] targetAttack = calculateTargetAttack(enemyBoardView, pendingHits);
            if (targetAttack != null) {
                return targetAttack;
            }
        }

        // Modo Caza (Hunt Mode con paridad de ajedrez)
        List<int[]> parityCandidates = new ArrayList<>();
        List<int[]> otherCandidates = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (enemyBoardView[r][c] == '?') {
                    if ((r + c) % 2 == 0) {
                        parityCandidates.add(new int[]{r, c});
                    } else {
                        otherCandidates.add(new int[]{r, c});
                    }
                }
            }
        }

        if (!parityCandidates.isEmpty()) {
            return parityCandidates.get(random.nextInt(parityCandidates.size()));
        }

        if (!otherCandidates.isEmpty()) {
            return otherCandidates.get(random.nextInt(otherCandidates.size()));
        }

        return new int[]{0, 0};
    }

    private int[] calculateTargetAttack(char[][] board, List<int[]> hits) {
        int rows = board.length;
        int cols = board[0].length;

        // Si hay 2 o más impactos, verificar alineación horizontal o vertical
        if (hits.size() >= 2) {
            boolean sameRow = true;
            boolean sameCol = true;
            int r0 = hits.get(0)[0];
            int c0 = hits.get(0)[1];

            for (int[] h : hits) {
                if (h[0] != r0) sameRow = false;
                if (h[1] != c0) sameCol = false;
            }

            if (sameRow) {
                int minCol = Integer.MAX_VALUE;
                int maxCol = Integer.MIN_VALUE;
                for (int[] h : hits) {
                    minCol = Math.min(minCol, h[1]);
                    maxCol = Math.max(maxCol, h[1]);
                }
                List<int[]> lineEnds = new ArrayList<>();
                if (minCol - 1 >= 0 && board[r0][minCol - 1] == '?') {
                    lineEnds.add(new int[]{r0, minCol - 1});
                }
                if (maxCol + 1 < cols && board[r0][maxCol + 1] == '?') {
                    lineEnds.add(new int[]{r0, maxCol + 1});
                }
                if (!lineEnds.isEmpty()) {
                    Collections.shuffle(lineEnds, random);
                    return lineEnds.get(0);
                }
            }

            if (sameCol) {
                int minRow = Integer.MAX_VALUE;
                int maxRow = Integer.MIN_VALUE;
                for (int[] h : hits) {
                    minRow = Math.min(minRow, h[0]);
                    maxRow = Math.max(maxRow, h[0]);
                }
                List<int[]> lineEnds = new ArrayList<>();
                if (minRow - 1 >= 0 && board[minRow - 1][c0] == '?') {
                    lineEnds.add(new int[]{minRow - 1, c0});
                }
                if (maxRow + 1 < rows && board[maxRow + 1][c0] == '?') {
                    lineEnds.add(new int[]{maxRow + 1, c0});
                }
                if (!lineEnds.isEmpty()) {
                    Collections.shuffle(lineEnds, random);
                    return lineEnds.get(0);
                }
            }
        }

        // Si es un único impacto o no se puede extender la línea, explorar vecinos adyacentes
        List<int[]> neighbors = new ArrayList<>();
        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] h : hits) {
            for (int[] d : deltas) {
                int nr = h[0] + d[0];
                int nc = h[1] + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && board[nr][nc] == '?') {
                    neighbors.add(new int[]{nr, nc});
                }
            }
        }

        if (!neighbors.isEmpty()) {
            return neighbors.get(random.nextInt(neighbors.size()));
        }

        return null;
    }
}
