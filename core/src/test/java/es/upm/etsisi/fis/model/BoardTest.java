package es.upm.etsisi.fis.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite de pruebas unitarias para la cuadrícula y reglas del tablero de combate (Board).
 */
class BoardTest {

    private Board board;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User("player1", "Admiral", false);
        board = new Board(mockUser);
    }

    @Test
    @DisplayName("El tablero inicializa con dimensiones 10x10 y sin naves")
    void testBoardInitialization() {
        assertThat(Board.getBoardSize()).isEqualTo(10);
        assertThat(board.getOwner()).isEqualTo(mockUser);
        assertThat(board.getShips()).isEmpty();
        assertThat(board.hasSurvivingShips()).isFalse();

        Cell[][] cells = board.getCells();
        assertThat(cells).hasDimensions(10, 10);
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                assertThat(cells[r][c].isHit()).isFalse();
                assertThat(cells[r][c].hasShip()).isFalse();
            }
        }
    }

    @Test
    @DisplayName("Colocar nave horizontalmente dentro de los límites es exitoso")
    void testPlaceShipHorizontalValid() {
        Ship battleship = new Battleship(); // longitud 4
        boolean result = board.placeShip(battleship, 2, 3, true);

        assertThat(result).isTrue();
        assertThat(board.getShips()).contains(battleship);
        assertThat(board.hasSurvivingShips()).isTrue();

        for (int c = 3; c < 7; c++) {
            assertThat(board.getShip(2, c)).isEqualTo(battleship);
            assertThat(board.getCells()[2][c].hasShip()).isTrue();
        }
        assertThat(board.getShip(2, 7)).isNull();
    }

    @Test
    @DisplayName("Colocar nave verticalmente dentro de los límites es exitoso")
    void testPlaceShipVerticalValid() {
        Ship submarine = new Submarine(); // longitud 3
        boolean result = board.placeShip(submarine, 4, 5, false);

        assertThat(result).isTrue();
        for (int r = 4; r < 7; r++) {
            assertThat(board.getShip(r, 5)).isEqualTo(submarine);
        }
    }

    @Test
    @DisplayName("Rechazar colocación de nave que desborda el tablero")
    void testPlaceShipOutOfBounds() {
        Ship carrier = new Carrier(); // longitud 5
        // Desborde horizontal
        assertThat(board.placeShip(carrier, 0, 7, true)).isFalse();
        // Desborde vertical
        assertThat(board.placeShip(carrier, 8, 0, false)).isFalse();
        // Coordenadas negativas
        assertThat(board.placeShip(carrier, -1, 0, true)).isFalse();
        assertThat(board.placeShip(carrier, 0, -1, false)).isFalse();
    }

    @Test
    @DisplayName("Rechazar colocación de nave sobre casillas ya ocupadas")
    void testPlaceShipCollision() {
        Ship patrolBoat = new PatrolBoat(); // longitud 2
        Ship submarine = new Submarine();   // longitud 3

        assertThat(board.placeShip(patrolBoat, 3, 3, true)).isTrue();
        // Intentar cruzar verticalmente sobre (3, 3)
        assertThat(board.placeShip(submarine, 2, 3, false)).isFalse();
    }

    @Test
    @DisplayName("Colocar flota aleatoria despliega las 4 naves sin colisión")
    void testPlaceRandomFleet() {
        board.placeRandomFleet();

        assertThat(board.getShips()).hasSize(4);
        assertThat(board.hasSurvivingShips()).isTrue();

        int occupiedCells = 0;
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                if (board.getCells()[r][c].hasShip()) {
                    occupiedCells++;
                }
            }
        }
        // Carrier(5) + Battleship(4) + Submarine(3) + PatrolBoat(2) = 14
        assertThat(occupiedCells).isEqualTo(14);
    }

    @Test
    @DisplayName("Secuencia de impactos: Miss, Hit, Sunk y Already attacked")
    void testAttackSequence() {
        Ship patrolBoat = new PatrolBoat(); // longitud 2 en (1, 1) y (1, 2)
        board.placeShip(patrolBoat, 1, 1, true);

        // Disparo al agua (Miss)
        assertThat(board.receiveAttack(new int[]{0, 0})).isEqualTo("Miss");
        assertThat(board.getCells()[0][0].isHit()).isTrue();

        // Repetir disparo
        assertThat(board.receiveAttack(new int[]{0, 0})).isEqualTo("Already attacked");

        // Primer impacto en nave viva (Hit)
        assertThat(board.receiveAttack(new int[]{1, 1})).isEqualTo("Hit");
        assertThat(patrolBoat.isAlive()).isTrue();
        assertThat(board.hasSurvivingShips()).isTrue();

        // Segundo impacto en nave (Sunk)
        assertThat(board.receiveAttack(new int[]{1, 2})).isEqualTo("Sunk");
        assertThat(patrolBoat.isAlive()).isFalse();
        assertThat(board.hasSurvivingShips()).isFalse();

        // Coordenadas fuera de rango
        assertThat(board.receiveAttack(new int[]{-1, 5})).isEqualTo("Coordinates out of bounds");
        assertThat(board.receiveAttack(new int[]{10, 5})).isEqualTo("Coordinates out of bounds");
        assertThat(board.receiveAttack(null)).isEqualTo("Invalid coordinates");
    }

    @Test
    @DisplayName("La niebla de guerra oculta naves no atacadas")
    void testFogOfWarVisibility() {
        Ship patrolBoat = new PatrolBoat();
        board.placeShip(patrolBoat, 5, 5, true);

        board.receiveAttack(new int[]{0, 0}); // Miss
        board.receiveAttack(new int[]{5, 5}); // Hit

        char[][] fogView = board.getFogOfWarView();
        assertThat(fogView[0][0]).isEqualTo('M'); // Miss
        assertThat(fogView[5][5]).isEqualTo('H'); // Hit
        assertThat(fogView[5][6]).isEqualTo('?'); // Barco intacto oculto
        assertThat(fogView[9][9]).isEqualTo('?'); // Casilla desconocida

        char[][] ownerView = board.getOwnerView();
        assertThat(ownerView[5][6]).isEqualTo('B'); // El dueño ve su propio barco
        assertThat(ownerView[9][9]).isEqualTo('~'); // El dueño ve agua
    }

    @Test
    @DisplayName("Reparación de daños del Submarino")
    void testRepairSubmarine() {
        Submarine sub = new Submarine();
        assertThat(board.repairSubmarine(sub)).isTrue();
        assertThat(sub.isRepaired()).isTrue();
        // Segundo intento de reparación debe rechazarse
        assertThat(board.repairSubmarine(sub)).isFalse();
    }
}
