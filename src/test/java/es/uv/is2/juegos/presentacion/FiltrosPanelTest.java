package es.uv.is2.juegos.presentacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uv.is2.juegos.aplicacion.CatalogoService;
import es.uv.is2.juegos.aplicacion.FiltroJuegosService;
import es.uv.is2.juegos.persistencia.CsvJuegoRepositorio;
import es.uv.is2.juegos.persistencia.InicializadorDatos;
import es.uv.is2.juegos.presentacion.FiltrosPanel.CriterioFiltro;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FiltrosPanelTest {

    @TempDir
    Path tempDir;

    private FiltrosPanel filtrosPanel;

    @BeforeEach
    void setUp() {
        Path rutaJuegos = tempDir.resolve("juegos_de_mesa.csv");
        Path rutaPartidas = tempDir.resolve("partidas.csv");
        new InicializadorDatos(rutaJuegos, rutaPartidas).inicializarSiNoExisten();
        CsvJuegoRepositorio repo = new CsvJuegoRepositorio(rutaJuegos);
        filtrosPanel = new FiltrosPanel(new CatalogoService(repo), new FiltroJuegosService(repo));
        filtrosPanel.recargar();
    }

    @Test
    void muestraUnicamenteLosCamposDelCriterioSeleccionadoYActualizaAlCambiar() {
        assertEquals(4, CriterioFiltro.values().length);

        // Estado inicial: NOMBRE
        assertTrue(filtrosPanel.esPanelNombreVisible());
        assertFalse(filtrosPanel.esPanelValoracionVisible());
        assertFalse(filtrosPanel.esPanelMinJugadoresVisible());
        assertFalse(filtrosPanel.esPanelMaxJugadoresVisible());

        // Cambiar a VALORACION
        filtrosPanel.seleccionarCriterio(CriterioFiltro.VALORACION);
        assertFalse(filtrosPanel.esPanelNombreVisible());
        assertTrue(filtrosPanel.esPanelValoracionVisible());
        assertFalse(filtrosPanel.esPanelMinJugadoresVisible());
        assertFalse(filtrosPanel.esPanelMaxJugadoresVisible());

        // Cambiar a MIN_JUGADORES
        filtrosPanel.seleccionarCriterio(CriterioFiltro.MIN_JUGADORES);
        assertFalse(filtrosPanel.esPanelNombreVisible());
        assertFalse(filtrosPanel.esPanelValoracionVisible());
        assertTrue(filtrosPanel.esPanelMinJugadoresVisible());
        assertFalse(filtrosPanel.esPanelMaxJugadoresVisible());

        // Cambiar a MAX_JUGADORES
        filtrosPanel.seleccionarCriterio(CriterioFiltro.MAX_JUGADORES);
        assertFalse(filtrosPanel.esPanelNombreVisible());
        assertFalse(filtrosPanel.esPanelValoracionVisible());
        assertFalse(filtrosPanel.esPanelMinJugadoresVisible());
        assertTrue(filtrosPanel.esPanelMaxJugadoresVisible());
    }

    @Test
    void validaEnTiempoRealAlIntroducirCeroONegativosEnFiltrosDeJugadores() {
        filtrosPanel.seleccionarCriterio(CriterioFiltro.MIN_JUGADORES);
        filtrosPanel.setTextoMinJugadores("0");
        assertTrue(filtrosPanel.getMensajeValidacion().contains("mayor que 0"));

        filtrosPanel.setTextoMinJugadores("-2");
        assertTrue(filtrosPanel.getMensajeValidacion().contains("mayor que 0"));

        filtrosPanel.setTextoMinJugadores("3");
        assertTrue(filtrosPanel.getMensajeValidacion().isBlank());

        filtrosPanel.seleccionarCriterio(CriterioFiltro.MAX_JUGADORES);
        filtrosPanel.setTextoMaxJugadores("0");
        assertTrue(filtrosPanel.getMensajeValidacion().contains("mayor que 0"));

        filtrosPanel.setTextoMaxJugadores("-1");
        assertTrue(filtrosPanel.getMensajeValidacion().contains("mayor que 0"));

        filtrosPanel.setTextoMaxJugadores("4");
        assertTrue(filtrosPanel.getMensajeValidacion().isBlank());
    }
}
