package es.uv.is2.juegos.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import es.uv.is2.juegos.persistencia.CsvJuegoRepositorio;
import es.uv.is2.juegos.persistencia.InicializadorDatos;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FiltroJuegosServiceTest {

    @TempDir
    Path tempDir;

    private FiltroJuegosService filtroService;
    private ValoracionService valoracionService;

    @BeforeEach
    void setUp() {
        Path rutaJuegos = tempDir.resolve("juegos_de_mesa.csv");
        Path rutaPartidas = tempDir.resolve("partidas.csv");
        new InicializadorDatos(rutaJuegos, rutaPartidas).inicializarSiNoExisten();
        CsvJuegoRepositorio repo = new CsvJuegoRepositorio(rutaJuegos);
        filtroService = new FiltroJuegosService(repo);
        valoracionService = new ValoracionService(repo);
    }

    @Test
    void filtraPorNombreIndependientemente() {
        List<Juego> resultados = filtroService.filtrarPorNombre("Ticket to Ride");
        assertEquals(2, resultados.size());
        assertThrows(ValidacionException.class, () -> filtroService.filtrarPorNombre(""));
    }

    @Test
    void filtraPorValoracionYSinValoracion() {
        assertEquals(50, filtroService.filtrarPorValoracion(null).size());

        valoracionService.asignarValoracion(1, 5);
        valoracionService.asignarValoracion(2, 4);

        assertEquals(1, filtroService.filtrarPorValoracion(5).size());
        assertEquals(1, filtroService.filtrarPorValoracion(4).size());
        assertEquals(48, filtroService.filtrarPorValoracion(null).size());
        assertThrows(ValidacionException.class, () -> filtroService.filtrarPorValoracion(7));
    }

    @Test
    void filtraPorMinimoYMaximoAceptandoSoloValoresPositivos() {
        List<Juego> min5 = filtroService.filtrarPorMinJugadores(5);
        assertEquals(1, min5.size());
        assertEquals("The Resistance", min5.get(0).getTitulo());

        List<Juego> min1 = filtroService.filtrarPorMinJugadores(1);
        assertEquals(50, min1.size());

        List<Juego> max2 = filtroService.filtrarPorMaxJugadores(2);
        assertFalse(max2.isEmpty());
        assertTrue(max2.stream().allMatch(j -> j.getMaxJugadores() <= 2));
    }

    @Test
    void rechazaCeroYValoresNegativosEnFiltrosDeJugadores() {
        assertThrows(ValidacionException.class, () -> filtroService.filtrarPorMinJugadores(0));
        assertThrows(ValidacionException.class, () -> filtroService.filtrarPorMinJugadores(-1));
        assertThrows(ValidacionException.class, () -> filtroService.filtrarPorMaxJugadores(0));
        assertThrows(ValidacionException.class, () -> filtroService.filtrarPorMaxJugadores(-3));
    }
}
