package es.upm.etsisi.fis.server.dto.game;

/**
 * Peticion DTO para iniciar una nueva partida contra el motor de Inteligencia Artificial.
 */
public class CreateGameRequest {

    /**
     * Dificultad deseada de la IA: EASY, NORMAL o HARD.
     */
    private String aiDifficulty = "NORMAL";

    public CreateGameRequest() {}

    public CreateGameRequest(String aiDifficulty) {
        this.aiDifficulty = aiDifficulty;
    }

    public String getAiDifficulty() {
        return aiDifficulty;
    }

    public void setAiDifficulty(String aiDifficulty) {
        this.aiDifficulty = aiDifficulty;
    }
}
