package es.uv.is2.juegos.aplicacion;

import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.Partida;
import es.uv.is2.juegos.dominio.TipoGanador;
import es.uv.is2.juegos.dominio.excepcion.JuegoNoEncontradoException;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import es.uv.is2.juegos.dominio.repositorio.JuegoRepositorio;
import es.uv.is2.juegos.dominio.repositorio.PartidaRepositorio;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Servicio de aplicación para RF-05: Registro de partidas, historial y juegos jugados en el último año.
 * Responsabilidad asignada al Integrante E.
 */
public class PartidaService {

    private final JuegoRepositorio juegoRepositorio;
    private final PartidaRepositorio partidaRepositorio;

    public PartidaService(JuegoRepositorio juegoRepositorio, PartidaRepositorio partidaRepositorio) {
        this.juegoRepositorio = Objects.requireNonNull(juegoRepositorio, "El repositorio de juegos es obligatorio.");
        this.partidaRepositorio = Objects.requireNonNull(partidaRepositorio, "El repositorio de partidas es obligatorio.");
    }

    /**
     * Registra una partida con ganador (individual o equipo) o sin ganador si ganador y tipoGanador son nulos.
     * Valida que el número de participantes se encuentre dentro del rango real del juego seleccionado.
     */
    public Partida registrarPartida(int juegoId,
                                    int numeroParticipantes,
                                    String ganador,
                                    TipoGanador tipoGanador,
                                    LocalDate fecha) {
        Juego juego = obtenerJuego(juegoId);
        juego.validarNumeroParticipantes(numeroParticipantes);
        int nuevoId = partidaRepositorio.obtenerSiguienteId();
        Partida partida = new Partida(nuevoId, juegoId, numeroParticipantes, ganador, tipoGanador, fecha);
        return partidaRepositorio.guardar(partida);
    }

    /**
     * Registra expresamente una partida sin ganador.
     */
    public Partida registrarPartidaSinGanador(int juegoId, int numeroParticipantes, LocalDate fecha) {
        return registrarPartida(juegoId, numeroParticipantes, null, null, fecha);
    }

    /**
     * Devuelve el historial de partidas de un juego concreto, ordenado de más reciente a más antigua.
     */
    public List<Partida> consultarHistorialPorJuego(int juegoId) {
        verificarJuegoExiste(juegoId);
        return partidaRepositorio.buscarPorJuegoId(juegoId).stream()
                .sorted(Comparator.comparing(Partida::getFecha).thenComparingInt(Partida::getId).reversed())
                .toList();
    }

    /**
     * Devuelve todas las partidas registradas ordenadas de más reciente a más antigua.
     */
    public List<Partida> listarTodasLasPartidas() {
        return partidaRepositorio.obtenerTodas().stream()
                .sorted(Comparator.comparing(Partida::getFecha).thenComparingInt(Partida::getId).reversed())
                .toList();
    }

    /**
     * Devuelve los juegos distintos que tienen al menos una partida registrada durante el último año
     * (desde hoy hace un año hasta hoy, ambos inclusive).
     */
    public List<Juego> consultarJuegosJugadosUltimoAnio() {
        return consultarJuegosJugadosUltimoAnio(LocalDate.now());
    }

    /**
     * Sobrecarga parametrizada por fecha de referencia para facilitar pruebas deterministas.
     */
    public List<Juego> consultarJuegosJugadosUltimoAnio(LocalDate fechaReferencia) {
        if (fechaReferencia == null) {
            throw new ValidacionException("La fecha de referencia no puede ser nula.");
        }
        LocalDate limiteInferior = fechaReferencia.minusYears(1);
        Set<Integer> idsJugados = new LinkedHashSet<>();
        for (Partida partida : partidaRepositorio.obtenerTodas()) {
            LocalDate fecha = partida.getFecha();
            if (!fecha.isBefore(limiteInferior) && !fecha.isAfter(fechaReferencia)) {
                idsJugados.add(partida.getJuegoId());
            }
        }
        return juegoRepositorio.obtenerTodos().stream()
                .filter(j -> idsJugados.contains(j.getId()))
                .toList();
    }

    private Juego obtenerJuego(int juegoId) {
        return juegoRepositorio.buscarPorId(juegoId)
                .orElseThrow(() -> new JuegoNoEncontradoException(juegoId));
    }

    private void verificarJuegoExiste(int juegoId) {
        obtenerJuego(juegoId);
    }
}
