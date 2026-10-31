package es.upm.etsisi.fis.server.exception;

/**
 * Excepcion lanzada cuando un recurso solicitado (partida, usuario, registro) no existe en el sistema.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
