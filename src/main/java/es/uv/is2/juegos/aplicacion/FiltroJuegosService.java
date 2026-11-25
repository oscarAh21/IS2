package es.uv.is2.juegos.aplicacion;

import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import es.uv.is2.juegos.dominio.repositorio.JuegoRepositorio;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Servicio de aplicación para RF-04: Filtros independientes sobre el catálogo de juegos.
 * Cada criterio se mantiene como una operación independiente.
 * Responsabilidad asignada al Integrante D.
 */
public class FiltroJuegosService {

    private final JuegoRepositorio juegoRepositorio;

    public FiltroJuegosService(JuegoRepositorio juegoRepositorio) {
        this.juegoRepositorio = Objects.requireNonNull(juegoRepositorio, "El repositorio de juegos es obligatorio.");
    }

    /**
     * Filtra los juegos cuyo título contiene el texto indicado (sin distinguir mayúsculas/minúsculas).
     */
    public List<Juego> filtrarPorNombre(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ValidacionException("Debe introducir un texto para filtrar por nombre.");
        }
        String buscado = texto.trim().toLowerCase(Locale.ROOT);
        return juegoRepositorio.obtenerTodos().stream()
                .filter(j -> j.getTitulo().toLowerCase(Locale.ROOT).contains(buscado))
                .toList();
    }

    /**
     * Filtra los juegos que tengan exactamente la valoración indicada (1 a 5),
     * o los juegos sin valoración si el parámetro es {@code null}.
     */
    public List<Juego> filtrarPorValoracion(Integer valoracion) {
        Juego.validarValoracionOpcional(valoracion);
        return juegoRepositorio.obtenerTodos().stream()
                .filter(j -> Objects.equals(j.getValoracion(), valoracion))
                .toList();
    }

    /**
     * Filtra los juegos cuyo número mínimo de jugadores sea mayor o igual al valor indicado.
     * El valor debe ser estrictamente mayor que cero.
     */
    public List<Juego> filtrarPorMinJugadores(int minJugadores) {
        validarNumeroJugadoresPositivo(minJugadores, "mínimo");
        return juegoRepositorio.obtenerTodos().stream()
                .filter(j -> j.getMinJugadores() >= minJugadores)
                .toList();
    }

    /**
     * Filtra los juegos cuyo número máximo de jugadores sea menor o igual al valor indicado.
     * El valor debe ser estrictamente mayor que cero.
     */
    public List<Juego> filtrarPorMaxJugadores(int maxJugadores) {
        validarNumeroJugadoresPositivo(maxJugadores, "máximo");
        return juegoRepositorio.obtenerTodos().stream()
                .filter(j -> j.getMaxJugadores() <= maxJugadores)
                .toList();
    }

    /**
     * Valida que el número de jugadores usado en un filtro sea estrictamente mayor que cero.
     */
    public void validarNumeroJugadoresPositivo(int valor, String tipoLimite) {
        if (valor <= 0) {
            throw new ValidacionException("El número " + tipoLimite
                    + " de jugadores debe ser estrictamente mayor que 0 (no se admiten 0 ni valores negativos).");
        }
    }
}
