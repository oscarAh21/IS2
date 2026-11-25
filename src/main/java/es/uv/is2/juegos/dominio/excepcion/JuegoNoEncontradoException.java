package es.uv.is2.juegos.dominio.excepcion;

/**
 * Excepción lanzada cuando no se encuentra un juego por su identificador.
 */
public class JuegoNoEncontradoException extends DominioException {

    public JuegoNoEncontradoException(int juegoId) {
        super("No existe ningún juego con el identificador " + juegoId + ".");
    }
}
