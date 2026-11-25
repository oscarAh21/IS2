package es.uv.is2.juegos.presentacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uv.is2.juegos.aplicacion.CatalogoService;
import es.uv.is2.juegos.aplicacion.PartidaService;
import es.uv.is2.juegos.persistencia.CsvJuegoRepositorio;
import es.uv.is2.juegos.persistencia.CsvPartidaRepositorio;
import es.uv.is2.juegos.persistencia.InicializadorDatos;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PartidasPanelTest {

    @TempDir
    Path tempDir;

    private PartidasPanel partidasPanel;

    @BeforeEach
    void setUp() {
        Path rutaJuegos = tempDir.resolve("juegos_de_mesa.csv");
        Path rutaPartidas = tempDir.resolve("partidas.csv");
        new InicializadorDatos(rutaJuegos, rutaPartidas).inicializarSiNoExisten();
        CsvJuegoRepositorio juegoRepo = new CsvJuegoRepositorio(rutaJuegos);
        CsvPartidaRepositorio partidaRepo = new CsvPartidaRepositorio(rutaPartidas);
        partidasPanel = new PartidasPanel(
                new CatalogoService(juegoRepo),
                new PartidaService(juegoRepo, partidaRepo)
        );
        partidasPanel.recargar();
    }

    @Test
    void actualizaInformacionDelJuegoAutomaticamenteAlCambiarSeleccionEnComboBox() {
        // Primer elemento (ID #1, 3 a 4 jugadores)
        String tituloPrimero = partidasPanel.getJuegoRegistroSeleccionado().getTitulo();
        String infoInicial = partidasPanel.getTextoInfoJuegoSeleccionado();
        assertTrue(infoInicial.contains(tituloPrimero));
        assertTrue(infoInicial.contains("Mín. jugadores: 3"));
        assertTrue(infoInicial.contains("Máx. jugadores: 4"));

        // Cambiar al segundo elemento: Carcassonne (2 a 5 jugadores)
        partidasPanel.seleccionarJuegoRegistroPorIndice(1);
        assertEquals("Carcassonne", partidasPanel.getJuegoRegistroSeleccionado().getTitulo());
        String infoCarcassonne = partidasPanel.getTextoInfoJuegoSeleccionado();
        assertTrue(infoCarcassonne.contains("Carcassonne"));
        assertTrue(infoCarcassonne.contains("Mín. jugadores: 2"));
        assertTrue(infoCarcassonne.contains("Máx. jugadores: 5"));

        // Cambiar a The Resistance (índice 28, ID #29, 5 a 10 jugadores)
        partidasPanel.seleccionarJuegoRegistroPorIndice(28);
        assertEquals("The Resistance", partidasPanel.getJuegoRegistroSeleccionado().getTitulo());
        String infoResistance = partidasPanel.getTextoInfoJuegoSeleccionado();
        assertTrue(infoResistance.contains("The Resistance"));
        assertTrue(infoResistance.contains("Mín. jugadores: 5"));
        assertTrue(infoResistance.contains("Máx. jugadores: 10"));
    }
}
