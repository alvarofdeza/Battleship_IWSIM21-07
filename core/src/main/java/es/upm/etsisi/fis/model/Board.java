package es.upm.etsisi.fis.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tablero de batalla naval de 10x10 que gestiona la colocación de naves, validación de coordenadas y niebla de guerra.
 */
public class Board {
    public static final int BOARD_SIZE = 10;
    private final Cell[][] cells;
    private final IPlayer owner;
    private final List<Ship> ships;

    /**
     * Construye un tablero asignado a un jugador específico.
     * @param owner Jugador propietario del tablero.
     */
    public Board(IPlayer owner) {
        this.owner = owner;
        this.cells = new Cell[BOARD_SIZE][BOARD_SIZE];
        this.ships = new ArrayList<>();

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                this.cells[r][c] = new Cell();
            }
        }
    }

    public static int getBoardSize() {
        return BOARD_SIZE;
    }

    public IPlayer getOwner() {
        return owner;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public List<Ship> getShips() {
        return new ArrayList<>(ships);
    }

    /**
     * Vacía completamente las casillas y la flota del tablero.
     */
    public void clearBoard() {
        this.ships.clear();
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                this.cells[r][c] = new Cell();
            }
        }
    }

    /**
     * Coloca una nave en las coordenadas indicadas con orientación horizontal o vertical.
     * @param ship Nave a posicionar.
     * @param row Fila inicial.
     * @param col Columna inicial.
     * @param horizontal true si se orienta horizontalmente, false si es vertical.
     * @return true si la colocación es válida y exitosa, false si colisiona o desborda los límites.
     */
    public boolean placeShip(Ship ship, int row, int col, boolean horizontal) {
        if (ship == null) return false;
        int len = ship.getLength();

        if (horizontal) {
            if (col + len > BOARD_SIZE || row < 0 || row >= BOARD_SIZE || col < 0) {
                return false;
            }
            for (int c = col; c < col + len; c++) {
                if (cells[row][c].hasShip()) {
                    return false;
                }
            }
            ship.clearPositions();
            for (int c = col; c < col + len; c++) {
                cells[row][c].setShip(ship);
                ship.addPosition(row, c);
            }
        } else {
            if (row + len > BOARD_SIZE || col < 0 || col >= BOARD_SIZE || row < 0) {
                return false;
            }
            for (int r = row; r < row + len; r++) {
                if (cells[r][col].hasShip()) {
                    return false;
                }
            }
            ship.clearPositions();
            for (int r = row; r < row + len; r++) {
                cells[r][col].setShip(ship);
                ship.addPosition(r, col);
            }
        }

        if (!ships.contains(ship)) {
            ships.add(ship);
        }
        return true;
    }

    /**
     * Despliega aleatoriamente una flota estándar completa (Carrier, Battleship, Submarine, PatrolBoat).
     */
    public void placeRandomFleet() {
        Random rng = new Random();
        List<Ship> fleet = List.of(
                new Carrier(),
                new Battleship(),
                new Submarine(),
                new PatrolBoat()
        );

        for (Ship ship : fleet) {
            boolean placed = false;
            while (!placed) {
                int r = rng.nextInt(BOARD_SIZE);
                int c = rng.nextInt(BOARD_SIZE);
                boolean horizontal = rng.nextBoolean();
                placed = placeShip(ship, r, c, horizontal);
            }
        }
    }

    /**
     * Procesa un ataque entrante sobre las coordenadas especificadas.
     * @param coords Coordenadas [fila, columna] del disparo.
     * @return Cadena descriptiva del resultado: "Miss", "Hit", "Sunk", "Already attacked" o "Coordinates out of bounds".
     */
    public String receiveAttack(int[] coords) {
        if (coords == null || coords.length < 2) {
            return "Invalid coordinates";
        }
        int row = coords[0];
        int col = coords[1];

        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            return "Coordinates out of bounds";
        }

        Cell cell = cells[row][col];
        if (cell.isHit()) {
            return "Already attacked";
        }

        cell.markHit();

        if (cell.hasShip()) {
            Ship ship = cell.getShip();
            ship.registerHit();
            if (!ship.isAlive()) {
                return "Sunk";
            }
            return "Hit";
        } else {
            return "Miss";
        }
    }

    /**
     * Comprueba si aún queda al menos una nave viva a flote en el tablero.
     * @return true si hay barcos vivos, false si toda la flota ha sido destruida.
     */
    public boolean hasSurvivingShips() {
        for (Ship ship : ships) {
            if (ship.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public Ship getShip(int row, int col) {
        if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
            return cells[row][col].getShip();
        }
        return null;
    }

    public boolean repairSubmarine(ISubmarine submarine) {
        if (submarine == null || submarine.isRepaired()) return false;
        submarine.markRepaired();
        return true;
    }

    /**
     * Genera la vista del tablero bajo la niebla de guerra para el atacante:
     * '?' = Casilla sin explorar
     * 'M' = Agua (Miss)
     * 'H' = Impacto certero (Hit)
     * 'S' = Nave hundida (Sunk)
     * @return Matriz de 10x10 de caracteres.
     */
    public char[][] getFogOfWarView() {
        char[][] view = new char[BOARD_SIZE][BOARD_SIZE];
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                Cell cell = cells[r][c];
                if (!cell.isHit()) {
                    view[r][c] = '?';
                } else if (!cell.hasShip()) {
                    view[r][c] = 'M';
                } else {
                    Ship ship = cell.getShip();
                    view[r][c] = ship.isAlive() ? 'H' : 'S';
                }
            }
        }
        return view;
    }

    /**
     * Genera la vista del tablero para el defensor propietario:
     * '~' = Mar despejado
     * 'B' = Barco intacto
     * 'M' = Disparo fallido del rival
     * 'H' = Daño sufrido en nave viva
     * 'S' = Daño sufrido en nave hundida
     * @return Matriz de 10x10 de caracteres.
     */
    public char[][] getOwnerView() {
        char[][] view = new char[BOARD_SIZE][BOARD_SIZE];
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                Cell cell = cells[r][c];
                if (!cell.isHit()) {
                    view[r][c] = cell.hasShip() ? 'B' : '~';
                } else if (!cell.hasShip()) {
                    view[r][c] = 'M';
                } else {
                    Ship ship = cell.getShip();
                    view[r][c] = ship.isAlive() ? 'H' : 'S';
                }
            }
        }
        return view;
    }
}
