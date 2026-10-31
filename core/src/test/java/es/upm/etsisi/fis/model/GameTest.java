package es.upm.etsisi.fis.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite de pruebas unitarias para el ciclo de vida de la partida, alternancia de turnos y victoria.
 */
class GameTest {

    @Test
    @DisplayName("AiPlayerFactory crea bots con niveles de dificultad correspondientes")
    void testAiPlayerFactory() {
        AiPlayer easy = AiPlayerFactory.createAiPlayer("EASY");
        assertThat(easy.getDifficulty()).isEqualTo(Difficulty.EASY);
        assertThat(easy.getName()).isEqualTo("AI-Easy");

        AiPlayer normal = AiPlayerFactory.createAiPlayer("NORMAL");
        assertThat(normal.getDifficulty()).isEqualTo(Difficulty.NORMAL);

        AiPlayer hard = AiPlayerFactory.createAiPlayer("HARD");
        assertThat(hard.getDifficulty()).isEqualTo(Difficulty.HARD);

        AiPlayer defaultBot = AiPlayerFactory.createAiPlayer("UNKNOWN");
        assertThat(defaultBot.getDifficulty()).isEqualTo(Difficulty.EASY);

        assertThat(easy.acceptSpecialAction(ShipSpecialAction.BATTLESHIP, 1)).isTrue();
        assertThat(easy.acceptSpecialAction(ShipSpecialAction.BATTLESHIP, 0)).isFalse();
    }

    @Test
    @DisplayName("Game coordina tableros, alternancia de turnos y registro de movimientos")
    void testGameLifecycle() {
        User player1 = new User("p1", "Alice", false);
        AiPlayer bot = AiPlayerFactory.createAiPlayer("EASY");

        Game game = new Game(101L, player1, bot);

        assertThat(game.getId()).isEqualTo(101L);
        assertThat(game.getPlayer1()).isEqualTo(player1);
        assertThat(game.getPlayer2()).isEqualTo(bot);
        assertThat(game.isFinished()).isFalse();
        assertThat(game.getWinner()).isNull();
        assertThat(game.getCurrentTurn()).isEqualTo(1);
        assertThat(game.getCurrentPlayerName()).isEqualTo("Alice");
        assertThat(game.getOpponentPlayerName()).isEqualTo("AI-Easy");
        assertThat(game.getBoardViews()).hasSize(2);

        // Turno del Jugador 1
        String res1 = game.executeShot(0, 0);
        assertThat(res1).isIn("Miss", "Hit", "Sunk");
        assertThat(player1.getMovements()).hasSize(1);
        assertThat(game.getCurrentTurn()).isEqualTo(2); // Turno alternó al bot

        // Turno del Bot
        String res2 = game.executeShot(1, 1);
        assertThat(res2).isIn("Miss", "Hit", "Sunk");
        assertThat(game.getCurrentTurn()).isEqualTo(1);
    }

    @Test
    @DisplayName("Condición de victoria termina la partida y asigna puntos al ganador")
    void testVictoryCondition() {
        User player1 = new User("p1", "Commander", false);
        User player2 = new User("p2", "Enemy", false);

        Game game = new Game(202L, player1, player2);

        // Vaciar tablero defensor y situar un único barco Patrullero
        Board defBoard = game.getBoard2();
        defBoard.clearBoard();
        PatrolBoat patrolBoat = new PatrolBoat();
        defBoard.placeShip(patrolBoat, 0, 0, true);

        // Disparo 1: Hit
        String r1 = game.executeShot(0, 0);
        assertThat(r1).isEqualTo("Hit");
        assertThat(game.isFinished()).isFalse();

        // Turno de Enemy (falla en el agua)
        game.executeShot(9, 9);

        // Disparo 2 de Commander: Sunk -> Victoria inmediata
        String r2 = game.executeShot(0, 1);
        assertThat(r2).contains("Victory");
        assertThat(game.isFinished()).isTrue();
        assertThat(game.getWinner()).isEqualTo(player1);
        assertThat(player1.getScores()).isNotEmpty();
        assertThat(player1.getScores().get(0).getPoints()).isGreaterThan(0);
    }

    @Test
    @DisplayName("ShipPlacementHelper genera colocaciones válidas")
    void testShipPlacementHelper() {
        var fleet = ShipPlacementHelper.generateFleetPlacement();
        assertThat(fleet).isNotEmpty();
        assertThat(fleet).containsKeys("Carrier", "Battleship", "Submarine", "PatrolBoat");
    }
}
