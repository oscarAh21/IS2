package es.uv.is2.juegos.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PartidaTest {

    @Test
    void crearPartidaConGanadorIndividualYEquipo() {
        LocalDate hoy = LocalDate.now();
        Partida individual = new Partida(1, 1, 4, "Ana", TipoGanador.INDIVIDUAL, hoy);
        assertTrue(individual.tuvoGanador());
        assertEquals("Ana", individual.getGanador());
        assertEquals(TipoGanador.INDIVIDUAL, individual.getTipoGanador());

        Partida equipo = new Partida(2, 11, 6, "Equipo Azul", TipoGanador.EQUIPO, hoy.minusDays(2));
        assertTrue(equipo.tuvoGanador());
        assertEquals(TipoGanador.EQUIPO, equipo.getTipoGanador());
    }

    @Test
    void crearPartidaSinGanador() {
        Partida sinGanador = new Partida(1, 3, 4, null, null, LocalDate.now());
        assertFalse(sinGanador.tuvoGanador());
        assertNull(sinGanador.getGanador());
        assertNull(sinGanador.getTipoGanador());
    }

    @Test
    void rechazaPartidasInvalidas() {
        LocalDate hoy = LocalDate.now();

        assertThrows(ValidacionException.class, () -> new Partida(0, 1, 4, null, null, hoy));
        assertThrows(ValidacionException.class, () -> new Partida(1, 0, 4, null, null, hoy));
        assertThrows(ValidacionException.class, () -> new Partida(1, 1, 0, null, null, hoy));
        assertThrows(ValidacionException.class, () -> new Partida(1, 1, 4, null, null, null));
        assertThrows(ValidacionException.class, () -> new Partida(1, 1, 4, null, null, hoy.plusDays(1)));
        assertThrows(ValidacionException.class, () -> new Partida(1, 1, 4, "Ana", null, hoy));
        assertThrows(ValidacionException.class, () -> new Partida(1, 1, 4, null, TipoGanador.INDIVIDUAL, hoy));
    }

    @Test
    void tipoGanadorFromTextoValidaValores() {
        assertEquals(TipoGanador.INDIVIDUAL, TipoGanador.fromTexto("Individual"));
        assertEquals(TipoGanador.EQUIPO, TipoGanador.fromTexto("EQUIPO"));
        assertThrows(ValidacionException.class, () -> TipoGanador.fromTexto("Otro"));
    }
}
