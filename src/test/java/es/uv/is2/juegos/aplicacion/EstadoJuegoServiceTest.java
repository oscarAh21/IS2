package es.uv.is2.juegos.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.uv.is2.juegos.dominio.EstadoJuego;
import es.uv.is2.juegos.dominio.excepcion.JuegoNoEncontradoException;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import es.uv.is2.juegos.persistencia.CsvJuegoRepositorio;
import es.uv.is2.juegos.persistencia.InicializadorDatos;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class EstadoJuegoServiceTest {

    @TempDir
    Path tempDir;

    private EstadoJuegoService estadoService;

    @BeforeEach
    void setUp() {
        Path rutaJuegos = tempDir.resolve("juegos_de_mesa.csv");
        Path rutaPartidas = tempDir.resolve("partidas.csv");
        new InicializadorDatos(rutaJuegos, rutaPartidas).inicializarSiNoExisten();
        estadoService = new EstadoJuegoService(new CsvJuegoRepositorio(rutaJuegos));
    }

    @Test
    void permiteTransicionLibreEntreLosCincoEstadosYPersiste() {
        assertEquals(EstadoJuego.DISPONIBLE, estadoService.consultarEstado(1));

        for (EstadoJuego destino : EstadoJuego.values()) {
            estadoService.cambiarEstado(1, destino);
            assertEquals(destino, estadoService.consultarEstado(1));
        }
    }

    @Test
    void validaEstadoNuloYJuegoInexistente() {
        assertThrows(ValidacionException.class, () -> estadoService.cambiarEstado(1, null));
        assertThrows(JuegoNoEncontradoException.class, () -> estadoService.consultarEstado(999));
        assertThrows(JuegoNoEncontradoException.class, () -> estadoService.cambiarEstado(999, EstadoJuego.JUGADO));
    }
}
