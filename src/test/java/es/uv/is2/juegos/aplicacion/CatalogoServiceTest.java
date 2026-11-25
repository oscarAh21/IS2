package es.uv.is2.juegos.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.excepcion.JuegoNoEncontradoException;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import es.uv.is2.juegos.persistencia.CsvJuegoRepositorio;
import es.uv.is2.juegos.persistencia.InicializadorDatos;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CatalogoServiceTest {

    @TempDir
    Path tempDir;

    private CatalogoService catalogoService;

    @BeforeEach
    void setUp() {
        Path rutaJuegos = tempDir.resolve("juegos_de_mesa.csv");
        Path rutaPartidas = tempDir.resolve("partidas.csv");
        new InicializadorDatos(rutaJuegos, rutaPartidas).inicializarSiNoExisten();
        catalogoService = new CatalogoService(new CsvJuegoRepositorio(rutaJuegos));
    }

    @Test
    void listaLos50JuegosDelCatalogoInicial() {
        List<Juego> juegos = catalogoService.listarCatalogo();
        assertEquals(50, juegos.size());
    }

    @Test
    void buscaPorNombreExactoSinDistinguirMayusculas() {
        List<Juego> encontrados = catalogoService.buscarPorNombreExacto("carcassonne");
        assertEquals(1, encontrados.size());
        assertEquals("Carcassonne", encontrados.get(0).getTitulo());

        List<Juego> noExacto = catalogoService.buscarPorNombreExacto("Carcass");
        assertTrue(noExacto.isEmpty());
    }

    @Test
    void lanzaExcepcionSiNombreVacioOIdNoExiste() {
        assertThrows(ValidacionException.class, () -> catalogoService.buscarPorNombreExacto("  "));
        assertThrows(JuegoNoEncontradoException.class, () -> catalogoService.obtenerPorId(999));
    }
}
