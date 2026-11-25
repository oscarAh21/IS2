package es.uv.is2.juegos.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.excepcion.JuegoNoEncontradoException;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import es.uv.is2.juegos.persistencia.CsvJuegoRepositorio;
import es.uv.is2.juegos.persistencia.InicializadorDatos;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ValoracionServiceTest {

    @TempDir
    Path tempDir;

    private ValoracionService valoracionService;

    @BeforeEach
    void setUp() {
        Path rutaJuegos = tempDir.resolve("juegos_de_mesa.csv");
        Path rutaPartidas = tempDir.resolve("partidas.csv");
        new InicializadorDatos(rutaJuegos, rutaPartidas).inicializarSiNoExisten();
        valoracionService = new ValoracionService(new CsvJuegoRepositorio(rutaJuegos));
    }

    @Test
    void asignaModificaYEliminaValoracionCorrectamente() {
        Juego asignado = valoracionService.asignarValoracion(1, 4);
        assertEquals(4, asignado.getValoracion());

        Juego modificado = valoracionService.modificarValoracion(1, 5);
        assertEquals(5, modificado.getValoracion());

        Juego eliminado = valoracionService.eliminarValoracion(1);
        assertNull(eliminado.getValoracion());
    }

    @Test
    void rechazaValoracionesFueraDeRangoYOperacionesInconsistentes() {
        assertThrows(ValidacionException.class, () -> valoracionService.asignarValoracion(1, 0));
        assertThrows(ValidacionException.class, () -> valoracionService.asignarValoracion(1, 6));
        assertThrows(ValidacionException.class, () -> valoracionService.modificarValoracion(1, 3));

        valoracionService.asignarValoracion(1, 3);
        assertThrows(ValidacionException.class, () -> valoracionService.asignarValoracion(1, 4));
        assertThrows(JuegoNoEncontradoException.class, () -> valoracionService.eliminarValoracion(999));
    }
}
