package es.upm.etsisi.fis.model.algorithm;

/**
 * Interfaz del patrón Strategy para algoritmos de toma de decisiones de la Inteligencia Artificial.
 */
public interface AiAlgorithm {
    /**
     * Calcula las coordenadas de la casilla para el próximo disparo.
     * @param enemyBoardView Matriz de 10x10 con '?' (desconocido), 'M' (fallo), 'H' (impacto) o 'S' (hundido).
     * @return Array de dos enteros [fila, columna] seleccionados.
     */
    int[] getNextAttack(char[][] enemyBoardView);
}
