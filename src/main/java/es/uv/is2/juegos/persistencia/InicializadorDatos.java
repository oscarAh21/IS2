package es.uv.is2.juegos.persistencia;

import es.uv.is2.juegos.dominio.EstadoJuego;
import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.excepcion.PersistenciaException;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Inicializa los archivos CSV persistentes en {@code data/} a partir del recurso inicial
 * sin sobrescribir los datos existentes en ejecuciones posteriores.
 */
public class InicializadorDatos {

    public static final String RECURSO_CATALOGO_INICIAL = "/juegos_de_mesa.csv";
    private static final int COLUMNAS_CATALOGO_INICIAL = 6;

    private final Path rutaJuegos;
    private final Path rutaPartidas;
    private final String recursoCatalogo;

    public InicializadorDatos(Path rutaJuegos, Path rutaPartidas) {
        this(rutaJuegos, rutaPartidas, RECURSO_CATALOGO_INICIAL);
    }

    public InicializadorDatos(Path rutaJuegos, Path rutaPartidas, String recursoCatalogo) {
        this.rutaJuegos = rutaJuegos;
        this.rutaPartidas = rutaPartidas;
        this.recursoCatalogo = recursoCatalogo;
    }

    /**
     * Crea los archivos persistentes si todavía no existen.
     * Nunca sobrescribe archivos ya existentes.
     */
    public void inicializarSiNoExisten() {
        inicializarJuegosSiNoExiste();
        inicializarPartidasSiNoExiste();
    }

    private void inicializarJuegosSiNoExiste() {
        if (Files.exists(rutaJuegos)) {
            return;
        }
        List<Juego> juegosIniciales = cargarCatalogoInicialDesdeRecurso();
        CsvJuegoRepositorio repo = new CsvJuegoRepositorio(rutaJuegos);
        repo.guardarTodos(juegosIniciales);
    }

    private void inicializarPartidasSiNoExiste() {
        if (Files.exists(rutaPartidas)) {
            return;
        }
        CsvUtil.escribirArchivoSeguro(rutaPartidas, List.of(CsvPartidaRepositorio.CABECERA));
    }

    /**
     * Lee el CSV original de 6 columnas incluido en los recursos y construye los objetos de dominio
     * con identificadores secuenciales, estado inicial {@link EstadoJuego#DISPONIBLE} y sin valoración.
     */
    public List<Juego> cargarCatalogoInicialDesdeRecurso() {
        InputStream stream = InicializadorDatos.class.getResourceAsStream(recursoCatalogo);
        if (stream == null) {
            throw new PersistenciaException("No se encontró el recurso de catálogo inicial: " + recursoCatalogo);
        }
        List<Juego> juegos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String cabecera = reader.readLine();
            if (cabecera == null) {
                throw new PersistenciaException("El recurso de catálogo inicial está vacío: " + recursoCatalogo);
            }
            String linea;
            int numeroLinea = 1;
            int siguienteId = 1;
            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) {
                    continue;
                }
                juegos.add(parsearJuegoInicial(linea, numeroLinea, siguienteId));
                siguienteId++;
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al leer el catálogo inicial desde recursos: " + e.getMessage(), e);
        }
        return juegos;
    }

    private Juego parsearJuegoInicial(String linea, int numLinea, int id) {
        List<String> cols = CsvUtil.parsearLinea(linea);
        if (cols.size() != COLUMNAS_CATALOGO_INICIAL) {
            throw new ValidacionException("Formato inválido en catálogo inicial (línea " + numLinea
                    + "): se esperaban " + COLUMNAS_CATALOGO_INICIAL + " columnas y se encontraron " + cols.size() + ".");
        }
        String titulo = cols.get(0);
        String editorial = cols.get(1);
        int anio = CsvUtil.parsearEntero(cols.get(2), "Año lanzamiento (línea " + numLinea + ")");
        int minJug = CsvUtil.parsearEntero(cols.get(3), "Mín. jugadores (línea " + numLinea + ")");
        int maxJug = CsvUtil.parsearEntero(cols.get(4), "Máx. jugadores (línea " + numLinea + ")");
        int[] duracion = CsvUtil.parsearDuracion(cols.get(5));

        return new Juego(
                id,
                titulo,
                editorial,
                anio,
                minJug,
                maxJug,
                duracion[0],
                duracion[1],
                EstadoJuego.DISPONIBLE,
                null
        );
    }
}
