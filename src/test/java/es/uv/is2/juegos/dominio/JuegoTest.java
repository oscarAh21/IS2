package es.uv.is2.juegos.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import java.time.Year;
import org.junit.jupiter.api.Test;

class JuegoTest {

    @Test
    void crearJuegoValidoYManipularEstadoYValoracion() {
        Juego juego = new Juego(1, "Catan", "KOSMOS", 1995, 3, 4, 60, 120, EstadoJuego.DISPONIBLE, null);

        assertEquals(1, juego.getId());
        assertEquals("Catan", juego.getTitulo());
        assertEquals("KOSMOS", juego.getEditorial());
        assertEquals(EstadoJuego.DISPONIBLE, juego.getEstado());
        assertNull(juego.getValoracion());
        assertEquals("60-120 min", juego.getDuracionFormateada());
        assertEquals("3-4", juego.getJugadoresFormateados());

        juego.setEstado(EstadoJuego.PRESTADO);
        assertEquals(EstadoJuego.PRESTADO, juego.getEstado());

        juego.setValoracion(5);
        assertEquals(5, juego.getValoracion());

        juego.eliminarValoracion();
        assertNull(juego.getValoracion());
    }

    @Test
    void dosJuegosConMismoTituloPeroDistintoIdSonEntidadesIndependientes() {
        Juego copia1 = new Juego(1, "Catan", "KOSMOS", 1995, 3, 4, 60, 120, EstadoJuego.DISPONIBLE, 4);
        Juego copia2 = new Juego(2, "Catan", "KOSMOS", 1995, 3, 4, 60, 120, EstadoJuego.PRESTADO, 5);

        assertNotEquals(copia1, copia2);
        assertNotEquals(copia1.hashCode(), copia2.hashCode());
    }

    @Test
    void rechazaDatosInvalidosEnConstruccion() {
        int anioFuturo = Year.now().getValue() + 1;

        assertThrows(ValidacionException.class,
                () -> new Juego(0, "Catan", "KOSMOS", 1995, 3, 4, 60, 120, EstadoJuego.DISPONIBLE, null));
        assertThrows(ValidacionException.class,
                () -> new Juego(1, " ", "KOSMOS", 1995, 3, 4, 60, 120, EstadoJuego.DISPONIBLE, null));
        assertThrows(ValidacionException.class,
                () -> new Juego(1, "Catan", "", 1995, 3, 4, 60, 120, EstadoJuego.DISPONIBLE, null));
        assertThrows(ValidacionException.class,
                () -> new Juego(1, "Catan", "KOSMOS", anioFuturo, 3, 4, 60, 120, EstadoJuego.DISPONIBLE, null));
        assertThrows(ValidacionException.class,
                () -> new Juego(1, "Catan", "KOSMOS", 1995, -1, 4, 60, 120, EstadoJuego.DISPONIBLE, null));
        assertThrows(ValidacionException.class,
                () -> new Juego(1, "Catan", "KOSMOS", 1995, 5, 4, 60, 120, EstadoJuego.DISPONIBLE, null));
        assertThrows(ValidacionException.class,
                () -> new Juego(1, "Catan", "KOSMOS", 1995, 3, 4, 0, 120, EstadoJuego.DISPONIBLE, null));
        assertThrows(ValidacionException.class,
                () -> new Juego(1, "Catan", "KOSMOS", 1995, 3, 4, 90, 60, EstadoJuego.DISPONIBLE, null));
        assertThrows(ValidacionException.class,
                () -> new Juego(1, "Catan", "KOSMOS", 1995, 3, 4, 60, 120, null, null));
        assertThrows(ValidacionException.class,
                () -> new Juego(1, "Catan", "KOSMOS", 1995, 3, 4, 60, 120, EstadoJuego.DISPONIBLE, 0));
        assertThrows(ValidacionException.class,
                () -> new Juego(1, "Catan", "KOSMOS", 1995, 3, 4, 60, 120, EstadoJuego.DISPONIBLE, 6));
    }

    @Test
    void estadoJuegoFromTextoValidaValoresPermitidos() {
        assertEquals(EstadoJuego.DISPONIBLE, EstadoJuego.fromTexto("Disponible"));
        assertEquals(EstadoJuego.DANADO, EstadoJuego.fromTexto("Dañado"));
        assertEquals(EstadoJuego.DANADO, EstadoJuego.fromTexto("DANADO"));
        assertEquals(EstadoJuego.JUGADO, EstadoJuego.fromTexto("Jugado"));
        assertEquals(EstadoJuego.COMPLETO, EstadoJuego.fromTexto("Completo"));
        assertEquals(EstadoJuego.PRESTADO, EstadoJuego.fromTexto("Prestado"));
        assertThrows(ValidacionException.class, () -> EstadoJuego.fromTexto("Perdido"));
        assertThrows(ValidacionException.class, () -> EstadoJuego.fromTexto(" "));
    }
}
