package es.upm.etsisi.fis.model;

import es.upm.etsisi.fis.model.algorithm.AiAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Jugador automático controlado por el motor de Inteligencia Artificial.
 */
public class AiPlayer implements IPlayer {
    private String name;
    private final Difficulty difficulty;
    private final AiAlgorithm algorithm;
    private final List<IMovement> movements;
    private final List<IScore> scores;

    public AiPlayer(String name, Difficulty difficulty, AiAlgorithm algorithm) {
        this.name = name;
        this.difficulty = difficulty;
        this.algorithm = algorithm;
        this.movements = new ArrayList<>();
        this.scores = new ArrayList<>();
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public AiAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public boolean acceptSpecialAction(ShipSpecialAction action, int remainingUses) {
        return remainingUses > 0;
    }

    @Override
    public int[] takeTurn(char[][] enemyBoardView) {
        return algorithm.getNextAttack(enemyBoardView);
    }

    @Override
    public void addMovement(IMovement movement) {
        if (movement != null) {
            this.movements.add(movement);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void addScore(IScore score) {
        if (score != null) {
            this.scores.add(score);
        }
    }

    @Override
    public List<IMovement> getMovements() {
        return new ArrayList<>(movements);
    }

    @Override
    public List<IScore> getScores() {
        return new ArrayList<>(scores);
    }

    @Override
    public String toString() {
        return "AiPlayer{" +
                "name='" + name + '\'' +
                ", difficulty=" + difficulty +
                '}';
    }
}
