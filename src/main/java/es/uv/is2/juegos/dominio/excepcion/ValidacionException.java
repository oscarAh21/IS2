package es.uv.is2.juegos.dominio.excepcion;

/**
 * Excepción lanzada cuando los datos incumplen alguna regla de validación del dominio.
 */
public class ValidacionException extends DominioException {

    public ValidacionException(String message) {
        super(message);
    }

    public ValidacionException(String message, Throwable cause) {
        super(message, cause);
    }
}
