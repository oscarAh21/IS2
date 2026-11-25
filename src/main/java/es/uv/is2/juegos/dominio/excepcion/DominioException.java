package es.uv.is2.juegos.dominio.excepcion;

/**
 * Excepción base para errores de reglas de negocio y dominio.
 */
public class DominioException extends RuntimeException {

    public DominioException(String message) {
        super(message);
    }

    public DominioException(String message, Throwable cause) {
        super(message, cause);
    }
}
