package es.uv.is2.juegos.aplicacion;

import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.excepcion.JuegoNoEncontradoException;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import es.uv.is2.juegos.dominio.repositorio.JuegoRepositorio;
import java.util.List;
import java.util.Objects;

/**
 * Servicio de aplicación para RF-01: Catálogo de juegos y consulta por nombre exacto.
 * Responsabilidad asignada al Integrante A.
 */
public class CatalogoService {

    private final JuegoRepositorio juegoRepositorio;

    public CatalogoService(JuegoRepositorio juegoRepositorio) {
        this.juegoRepositorio = Objects.requireNonNull(juegoRepositorio, "El repositorio de juegos es obligatorio.");
    }

    /**
     * Devuelve todos los juegos del catálogo.
     */
    public List<Juego> listarCatalogo() {
        return juegoRepositorio.obtenerTodos();
    }

    /**
     * Obtiene un juego por su identificador único.
     */
    public Juego obtenerPorId(int juegoId) {
        return juegoRepositorio.buscarPorId(juegoId)
                .orElseThrow(() -> new JuegoNoEncontradoException(juegoId));
    }

    /**
     * Consulta los juegos cuyo título coincide exactamente con el nombre indicado
     * (sin distinguir mayúsculas ni minúsculas).
     */
    public List<Juego> buscarPorNombreExacto(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ValidacionException("Debe introducir un nombre exacto para realizar la búsqueda.");
        }
        return juegoRepositorio.buscarPorTituloExacto(nombre.trim());
    }
}
