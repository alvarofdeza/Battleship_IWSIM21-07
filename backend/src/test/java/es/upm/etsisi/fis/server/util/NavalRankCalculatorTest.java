package es.upm.etsisi.fis.server.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Suite de pruebas unitarias para el calculo determinista de rangos navales.
 */
class NavalRankCalculatorTest {

    @Test
    @DisplayName("Puntos menores o iguales a 2500 asignan rango de Naval Cadet")
    void testCadeteNaval() {
        assertEquals("Naval Cadet", NavalRankCalculator.calculateRank(0));
        assertEquals("Naval Cadet", NavalRankCalculator.calculateRank(1000));
        assertEquals("Naval Cadet", NavalRankCalculator.calculateRank(2500));
        assertEquals("Naval Cadet", NavalRankCalculator.calculateRank(-50));
    }

    @Test
    @DisplayName("Puntos entre 2501 y 7500 asignan rango de Ensign")
    void testAlferezDeFragata() {
        assertEquals("Ensign", NavalRankCalculator.calculateRank(2501));
        assertEquals("Ensign", NavalRankCalculator.calculateRank(5000));
        assertEquals("Ensign", NavalRankCalculator.calculateRank(7500));
    }

    @Test
    @DisplayName("Puntos entre 7501 y 15000 asignan rango de Lieutenant")
    void testTenienteDeNavio() {
        assertEquals("Lieutenant", NavalRankCalculator.calculateRank(7501));
        assertEquals("Lieutenant", NavalRankCalculator.calculateRank(10000));
        assertEquals("Lieutenant", NavalRankCalculator.calculateRank(15000));
    }

    @Test
    @DisplayName("Puntos entre 15001 y 30000 asignan rango de Captain")
    void testCapitanDeNavio() {
        assertEquals("Captain", NavalRankCalculator.calculateRank(15001));
        assertEquals("Captain", NavalRankCalculator.calculateRank(25000));
        assertEquals("Captain", NavalRankCalculator.calculateRank(30000));
    }

    @Test
    @DisplayName("Puntos superiores a 30000 asignan rango de Fleet Admiral")
    void testAlmiranteDeLaFlota() {
        assertEquals("Fleet Admiral", NavalRankCalculator.calculateRank(30001));
        assertEquals("Fleet Admiral", NavalRankCalculator.calculateRank(100000));
    }
}
