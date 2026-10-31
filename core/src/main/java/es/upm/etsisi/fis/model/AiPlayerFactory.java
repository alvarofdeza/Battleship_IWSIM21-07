package es.upm.etsisi.fis.model;

import es.upm.etsisi.fis.model.algorithm.BasicAlgorithm;
import es.upm.etsisi.fis.model.algorithm.ExpertAlgorithm;
import es.upm.etsisi.fis.model.algorithm.NormalAlgorithm;

/**
 * Factoría para la instanciación de jugadores de inteligencia artificial según el nivel de dificultad.
 */
public class AiPlayerFactory {

    public AiPlayerFactory() {}

    /**
     * Construye un jugador bot con el algoritmo correspondiente a la dificultad solicitada.
     * @param difficultyStr Cadena que indica el nivel ("EASY", "NORMAL", "HARD" o equivalentes en mayúsculas).
     * @return Instancia configurada de AiPlayer.
     */
    public static AiPlayer createAiPlayer(String difficultyStr) {
        if (difficultyStr == null) {
            difficultyStr = "EASY";
        }

        switch (difficultyStr.trim().toUpperCase()) {
            case "HARD":
            case "EXPERT":
            case "DIFICIL":
                return new AiPlayer("AI-Expert", Difficulty.HARD, new ExpertAlgorithm());
            case "NORMAL":
            case "MEDIUM":
                return new AiPlayer("AI-Normal", Difficulty.NORMAL, new NormalAlgorithm());
            case "EASY":
            case "FACIL":
            default:
                return new AiPlayer("AI-Easy", Difficulty.EASY, new BasicAlgorithm());
        }
    }
}
