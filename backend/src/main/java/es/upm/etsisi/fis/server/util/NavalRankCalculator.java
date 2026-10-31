package es.upm.etsisi.fis.server.util;

/**
 * Calculador de rangos militares navales de la armada en funcion de los puntos acumulados de carrera.
 */
public final class NavalRankCalculator {

    private NavalRankCalculator() {}

    /**
     * Determina el rango militar honorifico segun los puntos acumulados.
     * @param totalPoints Puntos totales acumulados por el oficial.
     * @return Denominacion militar del rango alcanzado.
     */
    public static String calculateRank(long totalPoints) {
        if (totalPoints > 30000) {
            return "Fleet Admiral";
        }
        if (totalPoints > 15000) {
            return "Captain";
        }
        if (totalPoints > 7500) {
            return "Lieutenant";
        }
        if (totalPoints > 2500) {
            return "Ensign";
        }
        return "Naval Cadet";
    }
}
