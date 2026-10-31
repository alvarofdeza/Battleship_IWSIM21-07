package es.upm.etsisi.fis.server.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Suite de pruebas unitarias para el calculo determinista de rangos navales.
 */
class NavalRankCalculatorTest {

    @Test
    @DisplayName("Puntos menores o iguales a 2500 asignan rango de Cadete Naval")
    void testCadeteNaval() {
        assertEquals("Cadete Naval", NavalRankCalculator.calculateRank(0));
        assertEquals("Cadete Naval", NavalRankCalculator.calculateRank(1000));
        assertEquals("Cadete Naval", NavalRankCalculator.calculateRank(2500));
        assertEquals("Cadete Naval", NavalRankCalculator.calculateRank(-50));
    }

    @Test
    @DisplayName("Puntos entre 2501 y 7500 asignan rango de Alferez de Fragata")
    void testAlferezDeFragata() {
        assertEquals("Alferez de Fragata", NavalRankCalculator.calculateRank(2501));
        assertEquals("Alferez de Fragata", NavalRankCalculator.calculateRank(5000));
        assertEquals("Alferez de Fragata", NavalRankCalculator.calculateRank(7500));
    }

    @Test
    @DisplayName("Puntos entre 7501 y 15000 asignan rango de Teniente de Navio")
    void testTenienteDeNavio() {
        assertEquals("Teniente de Navio", NavalRankCalculator.calculateRank(7501));
        assertEquals("Teniente de Navio", NavalRankCalculator.calculateRank(10000));
        assertEquals("Teniente de Navio", NavalRankCalculator.calculateRank(15000));
    }

    @Test
    @DisplayName("Puntos entre 15001 y 30000 asignan rango de Capitan de Navio")
    void testCapitanDeNavio() {
        assertEquals("Capitan de Navio", NavalRankCalculator.calculateRank(15001));
        assertEquals("Capitan de Navio", NavalRankCalculator.calculateRank(25000));
        assertEquals("Capitan de Navio", NavalRankCalculator.calculateRank(30000));
    }

    @Test
    @DisplayName("Puntos superiores a 30000 asignan rango de Almirante de la Flota")
    void testAlmiranteDeLaFlota() {
        assertEquals("Almirante de la Flota", NavalRankCalculator.calculateRank(30001));
        assertEquals("Almirante de la Flota", NavalRankCalculator.calculateRank(100000));
    }
}
