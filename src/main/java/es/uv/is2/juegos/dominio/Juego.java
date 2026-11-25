package es.uv.is2.juegos.dominio;

import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import java.time.Year;
import java.util.Objects;

/**
 * Entidad de dominio que representa un juego de mesa del catálogo doméstico.
 * Dos juegos pueden compartir título, pero se distinguen únicamente por su identificador único.
 */
public class Juego {

    public static final int VALORACION_MINIMA = 1;
    public static final int VALORACION_MAXIMA = 5;

    private final int id;
    private final String titulo;
    private final String editorial;
    private final int anioLanzamiento;
    private final int minJugadores;
    private final int maxJugadores;
    private final int duracionMinima;
    private final int duracionMaxima;
    private EstadoJuego estado;
    private Integer valoracion;

    public Juego(int id,
                 String titulo,
                 String editorial,
                 int anioLanzamiento,
                 int minJugadores,
                 int maxJugadores,
                 int duracionMinima,
                 int duracionMaxima,
                 EstadoJuego estado,
                 Integer valoracion) {
        validarId(id);
        validarTextoObligatorio(titulo, "título");
        validarTextoObligatorio(editorial, "editorial");
        validarAnio(anioLanzamiento);
        validarJugadores(minJugadores, maxJugadores);
        validarDuracion(duracionMinima, duracionMaxima);
        validarEstado(estado);
        validarValoracionOpcional(valoracion);

        this.id = id;
        this.titulo = titulo.trim();
        this.editorial = editorial.trim();
        this.anioLanzamiento = anioLanzamiento;
        this.minJugadores = minJugadores;
        this.maxJugadores = maxJugadores;
        this.duracionMinima = duracionMinima;
        this.duracionMaxima = duracionMaxima;
        this.estado = estado;
        this.valoracion = valoracion;
    }

    private static void validarId(int id) {
        if (id <= 0) {
            throw new ValidacionException("El identificador del juego debe ser un entero positivo.");
        }
    }

    private static void validarTextoObligatorio(String valor, String nombreCampo) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacionException("El campo '" + nombreCampo + "' es obligatorio y no puede estar vacío.");
        }
    }

    private static void validarAnio(int anio) {
        int anioActual = Year.now().getValue();
        if (anio <= 0 || anio > anioActual) {
            throw new ValidacionException("El año de lanzamiento (" + anio
                    + ") debe ser válido y no posterior al año actual (" + anioActual + ").");
        }
    }

    public static void validarNumeroJugadoresPositivo(int valor, String tipoLimite) {
        if (valor <= 0) {
            throw new ValidacionException("El número " + tipoLimite
                    + " de jugadores debe ser estrictamente mayor que 0 (no se admiten 0 ni valores negativos).");
        }
    }

    private static void validarJugadores(int min, int max) {
        validarNumeroJugadoresPositivo(min, "mínimo");
        validarNumeroJugadoresPositivo(max, "máximo");
        if (max < min) {
            throw new ValidacionException("El número máximo de jugadores (" + max
                    + ") no puede ser inferior al mínimo (" + min + ").");
        }
    }

    private static void validarDuracion(int min, int max) {
        if (min <= 0 || max <= 0) {
            throw new ValidacionException("La duración mínima y máxima deben ser mayores que cero.");
        }
        if (max < min) {
            throw new ValidacionException("La duración máxima (" + max
                    + ") no puede ser inferior a la mínima (" + min + ").");
        }
    }

    private static void validarEstado(EstadoJuego estado) {
        if (estado == null) {
            throw new ValidacionException("El estado del juego es obligatorio.");
        }
    }

    public static void validarValoracionOpcional(Integer valoracion) {
        if (valoracion != null && (valoracion < VALORACION_MINIMA || valoracion > VALORACION_MAXIMA)) {
            throw new ValidacionException("La valoración debe estar comprendida entre "
                    + VALORACION_MINIMA + " y " + VALORACION_MAXIMA + ".");
        }
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getEditorial() {
        return editorial;
    }

    public int getAnioLanzamiento() {
        return anioLanzamiento;
    }

    public int getMinJugadores() {
        return minJugadores;
    }

    public int getMaxJugadores() {
        return maxJugadores;
    }

    public int getDuracionMinima() {
        return duracionMinima;
    }

    public int getDuracionMaxima() {
        return duracionMaxima;
    }

    public EstadoJuego getEstado() {
        return estado;
    }

    public void setEstado(EstadoJuego nuevoEstado) {
        validarEstado(nuevoEstado);
        this.estado = nuevoEstado;
    }

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer nuevaValoracion) {
        validarValoracionOpcional(nuevaValoracion);
        this.valoracion = nuevaValoracion;
    }

    public void eliminarValoracion() {
        this.valoracion = null;
    }

    public String getDuracionFormateada() {
        if (duracionMinima == duracionMaxima) {
            return duracionMinima + " min";
        }
        return duracionMinima + "-" + duracionMaxima + " min";
    }

    public String getJugadoresFormateados() {
        if (minJugadores == maxJugadores) {
            return String.valueOf(minJugadores);
        }
        return minJugadores + "-" + maxJugadores;
    }

    /**
     * Valida que el número de participantes de una partida se encuentre dentro del rango
     * real de jugadores permitido por este juego (ambos extremos inclusive).
     */
    public void validarNumeroParticipantes(int numeroParticipantes) {
        if (numeroParticipantes < minJugadores || numeroParticipantes > maxJugadores) {
            throw new ValidacionException("No se puede registrar la partida para '" + titulo
                    + "': el número de participantes (" + numeroParticipantes
                    + ") debe estar entre " + minJugadores + " y " + maxJugadores + " jugadores.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Juego other)) {
            return false;
        }
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "#" + id + " - " + titulo + " (" + anioLanzamiento + ")";
    }
}
