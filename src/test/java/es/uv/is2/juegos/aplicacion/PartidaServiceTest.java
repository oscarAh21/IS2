package es.uv.is2.juegos.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.Partida;
import es.uv.is2.juegos.dominio.TipoGanador;
import es.uv.is2.juegos.dominio.excepcion.JuegoNoEncontradoException;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import es.uv.is2.juegos.persistencia.CsvJuegoRepositorio;
import es.uv.is2.juegos.persistencia.CsvPartidaRepositorio;
import es.uv.is2.juegos.persistencia.InicializadorDatos;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PartidaServiceTest {

    @TempDir
    Path tempDir;

    private PartidaService partidaService;

    @BeforeEach
    void setUp() {
        Path rutaJuegos = tempDir.resolve("juegos_de_mesa.csv");
        Path rutaPartidas = tempDir.resolve("partidas.csv");
        new InicializadorDatos(rutaJuegos, rutaPartidas).inicializarSiNoExisten();
        partidaService = new PartidaService(
                new CsvJuegoRepositorio(rutaJuegos),
                new CsvPartidaRepositorio(rutaPartidas)
        );
    }

    @Test
    void registraPartidasConYSinGanadorYConsultaHistorial() {
        LocalDate hoy = LocalDate.now();
        // Catan (#1): min=3, max=4
        Partida p1 = partidaService.registrarPartida(1, 4, "Carlos", TipoGanador.INDIVIDUAL, hoy.minusDays(10));
        Partida p2 = partidaService.registrarPartidaSinGanador(1, 3, hoy.minusDays(2));

        assertTrue(p1.tuvoGanador());
        assertFalse(p2.tuvoGanador());

        List<Partida> historial = partidaService.consultarHistorialPorJuego(1);
        assertEquals(2, historial.size());
        assertEquals(p2.getId(), historial.get(0).getId());
    }

    @Test
    void aceptaExactamenteMinimoYMaximoDeParticipantesDelJuego() {
        LocalDate hoy = LocalDate.now();
        // Catan (#1): min=3, max=4
        Partida conMinimo = partidaService.registrarPartidaSinGanador(1, 3, hoy);
        Partida conMaximo = partidaService.registrarPartida(1, 4, "Elena", TipoGanador.INDIVIDUAL, hoy);
        assertEquals(3, conMinimo.getNumeroParticipantes());
        assertEquals(4, conMaximo.getNumeroParticipantes());

        // 7 Wonders Duel (#18): min=2, max=2
        Partida exactoDos = partidaService.registrarPartidaSinGanador(18, 2, hoy);
        assertEquals(2, exactoDos.getNumeroParticipantes());
    }

    @Test
    void rechazaParticipantesInferioresAlMinimoOSuperioresAlMaximoDelJuego() {
        LocalDate hoy = LocalDate.now();
        // Catan (#1): rango permitido 3..4
        ValidacionException errMenor = assertThrows(ValidacionException.class,
                () -> partidaService.registrarPartidaSinGanador(1, 2, hoy));
        assertTrue(errMenor.getMessage().contains("3"));
        assertTrue(errMenor.getMessage().contains("4"));

        ValidacionException errMayor = assertThrows(ValidacionException.class,
                () -> partidaService.registrarPartida(1, 5, "Ana", TipoGanador.INDIVIDUAL, hoy));
        assertTrue(errMayor.getMessage().contains("3"));
        assertTrue(errMayor.getMessage().contains("4"));

        // 7 Wonders Duel (#18): rango permitido 2..2
        assertThrows(ValidacionException.class, () -> partidaService.registrarPartidaSinGanador(18, 1, hoy));
        assertThrows(ValidacionException.class, () -> partidaService.registrarPartidaSinGanador(18, 3, hoy));
    }

    @Test
    void consultaJuegosJugadosDuranteElUltimoAnio() {
        LocalDate referencia = LocalDate.now();

        // Dentro del último año
        partidaService.registrarPartida(1, 4, "Ana", TipoGanador.INDIVIDUAL, referencia.minusMonths(2));
        partidaService.registrarPartidaSinGanador(1, 4, referencia.minusDays(5));
        partidaService.registrarPartida(5, 6, "Equipo Rojo", TipoGanador.EQUIPO, referencia.minusYears(1));

        // Fuera del último año
        partidaService.registrarPartida(10, 3, "Luis", TipoGanador.INDIVIDUAL, referencia.minusYears(1).minusDays(1));

        List<Juego> jugadosUltimoAnio = partidaService.consultarJuegosJugadosUltimoAnio(referencia);
        assertEquals(2, jugadosUltimoAnio.size());
        assertTrue(jugadosUltimoAnio.stream().anyMatch(j -> j.getId() == 1));
        assertTrue(jugadosUltimoAnio.stream().anyMatch(j -> j.getId() == 5));
        assertFalse(jugadosUltimoAnio.stream().anyMatch(j -> j.getId() == 10));
    }

    @Test
    void rechazaJuegoInexistenteAlRegistrarOConsultarHistorial() {
        assertThrows(JuegoNoEncontradoException.class,
                () -> partidaService.registrarPartidaSinGanador(999, 2, LocalDate.now()));
        assertThrows(JuegoNoEncontradoException.class,
                () -> partidaService.consultarHistorialPorJuego(999));
    }
}
