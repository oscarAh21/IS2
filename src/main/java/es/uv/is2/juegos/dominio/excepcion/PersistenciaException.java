package es.uv.is2.juegos.dominio.excepcion;

/**
 * Excepción lanzada cuando ocurre un error al leer o escribir en la capa de persistencia.
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String message) {
        super(message);
    }

    public PersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }
}
