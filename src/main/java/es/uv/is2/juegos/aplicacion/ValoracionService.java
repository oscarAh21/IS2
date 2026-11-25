package es.uv.is2.juegos.aplicacion;

import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.excepcion.JuegoNoEncontradoException;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import es.uv.is2.juegos.dominio.repositorio.JuegoRepositorio;
import java.util.Objects;

/**
 * Servicio de aplicación para RF-03: Gestión de valoraciones (1 a 5 o sin valoración).
 * Responsabilidad asignada al Integrante C.
 */
public class ValoracionService {

    private final JuegoRepositorio juegoRepositorio;

    public ValoracionService(JuegoRepositorio juegoRepositorio) {
        this.juegoRepositorio = Objects.requireNonNull(juegoRepositorio, "El repositorio de juegos es obligatorio.");
    }

    /**
     * Asigna una valoración entre 1 y 5 a un juego que no tenga valoración previa.
     */
    public Juego asignarValoracion(int juegoId, int valoracion) {
        Juego.validarValoracionOpcional(valoracion);
        Juego juego = obtenerJuego(juegoId);
        if (juego.getValoracion() != null) {
            throw new ValidacionException("El juego '" + juego.getTitulo()
                    + "' ya tiene una valoración asignada (" + juego.getValoracion()
                    + "). Utilice la operación de modificar valoración.");
        }
        juego.setValoracion(valoracion);
        juegoRepositorio.actualizar(juego);
        return juego;
    }

    /**
     * Modifica la valoración existente de un juego (entre 1 y 5).
     */
    public Juego modificarValoracion(int juegoId, int nuevaValoracion) {
        Juego.validarValoracionOpcional(nuevaValoracion);
        Juego juego = obtenerJuego(juegoId);
        if (juego.getValoracion() == null) {
            throw new ValidacionException("El juego '" + juego.getTitulo()
                    + "' todavía no tiene valoración. Utilice la operación de asignar valoración.");
        }
        juego.setValoracion(nuevaValoracion);
        juegoRepositorio.actualizar(juego);
        return juego;
    }

    /**
     * Elimina la valoración de un juego, dejándolo sin valoración.
     */
    public Juego eliminarValoracion(int juegoId) {
        Juego juego = obtenerJuego(juegoId);
        juego.eliminarValoracion();
        juegoRepositorio.actualizar(juego);
        return juego;
    }

    private Juego obtenerJuego(int juegoId) {
        return juegoRepositorio.buscarPorId(juegoId)
                .orElseThrow(() -> new JuegoNoEncontradoException(juegoId));
    }
}
