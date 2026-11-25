package es.uv.is2.juegos.aplicacion;

import es.uv.is2.juegos.dominio.EstadoJuego;
import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.excepcion.JuegoNoEncontradoException;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import es.uv.is2.juegos.dominio.repositorio.JuegoRepositorio;
import java.util.Objects;

/**
 * Servicio de aplicación para RF-02: Gestión de estados de los juegos.
 * Permite consultar y cambiar libremente entre los cinco estados permitidos.
 * Responsabilidad asignada al Integrante B.
 */
public class EstadoJuegoService {

    private final JuegoRepositorio juegoRepositorio;

    public EstadoJuegoService(JuegoRepositorio juegoRepositorio) {
        this.juegoRepositorio = Objects.requireNonNull(juegoRepositorio, "El repositorio de juegos es obligatorio.");
    }

    /**
     * Consulta el estado actual de un juego.
     */
    public EstadoJuego consultarEstado(int juegoId) {
        Juego juego = juegoRepositorio.buscarPorId(juegoId)
                .orElseThrow(() -> new JuegoNoEncontradoException(juegoId));
        return juego.getEstado();
    }

    /**
     * Cambia el estado de un juego a cualquiera de los cinco estados permitidos y persiste el cambio.
     */
    public Juego cambiarEstado(int juegoId, EstadoJuego nuevoEstado) {
        if (nuevoEstado == null) {
            throw new ValidacionException("Debe seleccionar un estado válido.");
        }
        Juego juego = juegoRepositorio.buscarPorId(juegoId)
                .orElseThrow(() -> new JuegoNoEncontradoException(juegoId));
        juego.setEstado(nuevoEstado);
        juegoRepositorio.actualizar(juego);
        return juego;
    }
}
