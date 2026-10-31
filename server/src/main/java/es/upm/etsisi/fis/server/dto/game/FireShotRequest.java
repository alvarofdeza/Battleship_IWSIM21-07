package es.upm.etsisi.fis.server.dto.game;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Peticion DTO para efectuar un disparo con coordenadas sobre el tablero adversario.
 */
public class FireShotRequest {

    @Min(value = 0, message = "Row must be between 0 and 9")
    @Max(value = 9, message = "Row must be between 0 and 9")
    private int row;

    @Min(value = 0, message = "Column must be between 0 and 9")
    @Max(value = 9, message = "Column must be between 0 and 9")
    private int col;

    public FireShotRequest() {}

    public FireShotRequest(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
