package es.uv.is2.juegos.dominio.repositorio;

import es.uv.is2.juegos.dominio.Juego;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistencia para el catálogo de juegos de mesa.
 */
public interface JuegoRepositorio {

    /**
     * Devuelve todos los juegos del catálogo.
     */
    List<Juego> obtenerTodos();

    /**
     * Busca un juego por su identificador único.
     */
    Optional<Juego> buscarPorId(int id);

    /**
     * Busca todos los juegos cuyo título coincida exactamente (ignorando mayúsculas/minúsculas).
     */
    List<Juego> buscarPorTituloExacto(String titulo);

    /**
     * Actualiza un juego existente en el catálogo. No permite incorporar títulos nuevos.
     */
    void actualizar(Juego juego);

    /**
     * Persiste la colección completa de juegos del catálogo.
     */
    void guardarTodos(List<Juego> juegos);
}
