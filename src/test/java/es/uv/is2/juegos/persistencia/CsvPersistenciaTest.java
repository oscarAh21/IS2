package es.uv.is2.juegos.persistencia;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uv.is2.juegos.dominio.EstadoJuego;
import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.Partida;
import es.uv.is2.juegos.dominio.TipoGanador;
import es.uv.is2.juegos.dominio.excepcion.JuegoNoEncontradoException;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvPersistenciaTest {

    @TempDir
    Path tempDir;

    @Test
    void inicializa50JuegosSinSobrescribirCambiosExistentes() {
        Path rutaJuegos = tempDir.resolve("juegos_de_mesa.csv");
        Path rutaPartidas = tempDir.resolve("partidas.csv");

        InicializadorDatos inicializador = new InicializadorDatos(rutaJuegos, rutaPartidas);
        inicializador.inicializarSiNoExisten();

        assertTrue(Files.exists(rutaJuegos));
        assertTrue(Files.exists(rutaPartidas));

        CsvJuegoRepositorio repo = new CsvJuegoRepositorio(rutaJuegos);
        List<Juego> juegos = repo.obtenerTodos();
        assertEquals(50, juegos.size());

        Juego primero = juegos.get(0);
        assertEquals(1, primero.getId());
        assertEquals(60, primero.getDuracionMinima());
        assertEquals(120, primero.getDuracionMaxima());
        assertEquals(EstadoJuego.DISPONIBLE, primero.getEstado());
        assertNull(primero.getValoracion());

        Juego pandemic = juegos.get(2);
        assertEquals("Pandemic", pandemic.getTitulo());
        assertEquals(45, pandemic.getDuracionMinima());
        assertEquals(45, pandemic.getDuracionMaxima());

        // Modificamos un juego y volvemos a ejecutar el inicializador: no debe sobrescribir.
        primero.setEstado(EstadoJuego.PRESTADO);
        primero.setValoracion(5);
        repo.actualizar(primero);

        inicializador.inicializarSiNoExisten();

        Juego catanRecargado = repo.buscarPorId(1).orElseThrow();
        assertEquals(EstadoJuego.PRESTADO, catanRecargado.getEstado());
        assertEquals(5, catanRecargado.getValoracion());
    }

    @Test
    void parsearDuracionAceptaValorUnicoEIntervalo() {
        assertArrayEquals(new int[]{60, 60}, CsvUtil.parsearDuracion("60"));
        assertArrayEquals(new int[]{30, 60}, CsvUtil.parsearDuracion("30-60"));
        assertThrows(ValidacionException.class, () -> CsvUtil.parsearDuracion(""));
        assertThrows(ValidacionException.class, () -> CsvUtil.parsearDuracion("30-60-90"));
        assertThrows(ValidacionException.class, () -> CsvUtil.parsearDuracion("abc"));
    }

    @Test
    void rechazaActualizarJuegoFueraDelCatalogo() {
        Path rutaJuegos = tempDir.resolve("juegos_de_mesa.csv");
        Path rutaPartidas = tempDir.resolve("partidas.csv");
        new InicializadorDatos(rutaJuegos, rutaPartidas).inicializarSiNoExisten();

        CsvJuegoRepositorio repo = new CsvJuegoRepositorio(rutaJuegos);
        Juego inexistente = new Juego(999, "Nuevo Juego", "Editorial", 2020, 2, 4, 30, 60, EstadoJuego.DISPONIBLE, null);
        assertThrows(JuegoNoEncontradoException.class, () -> repo.actualizar(inexistente));
    }

    @Test
    void detectaCsvCorruptoOIdsDuplicados() throws IOException {
        Path rutaMal = tempDir.resolve("mal.csv");
        Files.writeString(rutaMal, CsvJuegoRepositorio.CABECERA + "\n1;\"A\";\"B\"\n", StandardCharsets.UTF_8);
        CsvJuegoRepositorio repo = new CsvJuegoRepositorio(rutaMal);
        assertThrows(ValidacionException.class, repo::obtenerTodos);

        Path rutaDup = tempDir.resolve("dup.csv");
        String linea1 = "1;\"Catan\";\"KOSMOS\";1995;3;4;60;120;\"Disponible\";";
        Files.writeString(rutaDup, CsvJuegoRepositorio.CABECERA + "\n" + linea1 + "\n" + linea1 + "\n", StandardCharsets.UTF_8);
        CsvJuegoRepositorio repoDup = new CsvJuegoRepositorio(rutaDup);
        assertThrows(ValidacionException.class, repoDup::obtenerTodos);
    }

    @Test
    void guardaYLeePartidasConYSinGanador() {
        Path rutaPartidas = tempDir.resolve("partidas.csv");
        CsvPartidaRepositorio repo = new CsvPartidaRepositorio(rutaPartidas);

        Partida p1 = new Partida(1, 1, 4, "Laura", TipoGanador.INDIVIDUAL, LocalDate.now().minusDays(5));
        Partida p2 = new Partida(2, 3, 3, null, null, LocalDate.now().minusDays(1));

        repo.guardar(p1);
        repo.guardar(p2);

        List<Partida> todas = repo.obtenerTodas();
        assertEquals(2, todas.size());
        assertTrue(todas.get(0).tuvoGanador());
        assertEquals("Laura", todas.get(0).getGanador());
        assertEquals(TipoGanador.INDIVIDUAL, todas.get(0).getTipoGanador());

        assertFalse(todas.get(1).tuvoGanador());
        assertNull(todas.get(1).getGanador());
        assertEquals(3, repo.obtenerSiguienteId());
    }
}
