package es.uv.is2.juegos.dominio.repositorio;

import es.uv.is2.juegos.dominio.Partida;
import java.util.List;

/**
 * Contrato de persistencia para el historial de partidas.
 */
public interface PartidaRepositorio {

    /**
     * Devuelve todas las partidas registradas.
     */
    List<Partida> obtenerTodas();

    /**
     * Devuelve las partidas asociadas a un juego concreto.
     */
    List<Partida> buscarPorJuegoId(int juegoId);

    /**
     * Guarda una nueva partida y la devuelve.
     */
    Partida guardar(Partida partida);

    /**
     * Calcula el siguiente identificador disponible para una nueva partida.
     */
    int obtenerSiguienteId();
}
