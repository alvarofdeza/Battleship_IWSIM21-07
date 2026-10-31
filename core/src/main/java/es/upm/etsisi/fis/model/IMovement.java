package es.upm.etsisi.fis.model;

/**
 * Contrato de operaciones para el registro y trazabilidad de movimientos en una partida.
 */
public interface IMovement {
    /**
     * Crea y devuelve una copia superficial del movimiento actual.
     * @return Nueva instancia clonada de IMovement.
     */
    IMovement cloneMovement();

    void setGameId(Long gameId);
    void setRow(int row);
    void setColumn(int column);
    void setTimestamp(long timestamp);

    int getRow();
    int getColumn();
    Long getGameId();
    long getTimestamp();
}
