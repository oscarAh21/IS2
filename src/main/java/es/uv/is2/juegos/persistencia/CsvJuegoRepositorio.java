package es.uv.is2.juegos.persistencia;

import es.uv.is2.juegos.dominio.EstadoJuego;
import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.excepcion.JuegoNoEncontradoException;
import es.uv.is2.juegos.dominio.excepcion.PersistenciaException;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import es.uv.is2.juegos.dominio.repositorio.JuegoRepositorio;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementación del repositorio de juegos basada en archivo CSV.
 */
public class CsvJuegoRepositorio implements JuegoRepositorio {

    public static final String CABECERA = "\"ID\";\"Título\";\"Editorial\";\"Año lanzamiento\";\"Mín. jugadores\";\"Máx. jugadores\";\"Duración mín. (min)\";\"Duración máx. (min)\";\"Estado\";\"Valoración\"";
    private static final int NUM_COLUMNAS = 10;

    private final Path rutaArchivo;

    public CsvJuegoRepositorio(Path rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public Path getRutaArchivo() {
        return rutaArchivo;
    }

    @Override
    public List<Juego> obtenerTodos() {
        if (!Files.exists(rutaArchivo)) {
            throw new PersistenciaException("No se encuentra el archivo de juegos: " + rutaArchivo);
        }
        try {
            List<String> lineas = Files.readAllLines(rutaArchivo, StandardCharsets.UTF_8);
            if (lineas.isEmpty()) {
                throw new PersistenciaException("El archivo de juegos está vacío: " + rutaArchivo);
            }
            List<Juego> juegos = new ArrayList<>();
            Set<Integer> idsVistos = new HashSet<>();

            for (int i = 1; i < lineas.size(); i++) {
                String linea = lineas.get(i);
                if (linea.isBlank()) {
                    continue;
                }
                Juego juego = deserializarLinea(linea, i + 1);
                if (!idsVistos.add(juego.getId())) {
                    throw new ValidacionException("Identificador de juego duplicado en CSV (línea " + (i + 1) + "): " + juego.getId());
                }
                juegos.add(juego);
            }
            return juegos;
        } catch (IOException e) {
            throw new PersistenciaException("Error al leer el archivo de juegos '" + rutaArchivo + "': " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Juego> buscarPorId(int id) {
        return obtenerTodos().stream()
                .filter(j -> j.getId() == id)
                .findFirst();
    }

    @Override
    public List<Juego> buscarPorTituloExacto(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new ValidacionException("El nombre del juego a buscar no puede estar vacío.");
        }
        String buscado = titulo.trim();
        return obtenerTodos().stream()
                .filter(j -> j.getTitulo().equalsIgnoreCase(buscado))
                .toList();
    }

    @Override
    public void actualizar(Juego juegoActualizado) {
        if (juegoActualizado == null) {
            throw new ValidacionException("El juego a actualizar no puede ser nulo.");
        }
        List<Juego> juegos = new ArrayList<>(obtenerTodos());
        boolean encontrado = false;
        for (int i = 0; i < juegos.size(); i++) {
            if (juegos.get(i).getId() == juegoActualizado.getId()) {
                juegos.set(i, juegoActualizado);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            throw new JuegoNoEncontradoException(juegoActualizado.getId());
        }
        guardarTodos(juegos);
    }

    @Override
    public void guardarTodos(List<Juego> juegos) {
        if (juegos == null) {
            throw new ValidacionException("La lista de juegos a guardar no puede ser nula.");
        }
        Set<Integer> ids = new HashSet<>();
        List<String> lineas = new ArrayList<>();
        lineas.add(CABECERA);
        for (Juego juego : juegos) {
            if (juego == null) {
                throw new ValidacionException("La lista de juegos contiene un elemento nulo.");
            }
            if (!ids.add(juego.getId())) {
                throw new ValidacionException("No se pueden guardar juegos con identificadores duplicados: " + juego.getId());
            }
            lineas.add(serializarJuego(juego));
        }
        CsvUtil.escribirArchivoSeguro(rutaArchivo, lineas);
    }

    private Juego deserializarLinea(String linea, int numLinea) {
        List<String> cols = CsvUtil.parsearLinea(linea);
        if (cols.size() != NUM_COLUMNAS) {
            throw new ValidacionException("Formato CSV inválido en juegos (línea " + numLinea
                    + "): se esperaban " + NUM_COLUMNAS + " columnas y se encontraron " + cols.size() + ".");
        }
        int id = CsvUtil.parsearEntero(cols.get(0), "ID (línea " + numLinea + ")");
        String titulo = cols.get(1);
        String editorial = cols.get(2);
        int anio = CsvUtil.parsearEntero(cols.get(3), "Año lanzamiento (línea " + numLinea + ")");
        int minJug = CsvUtil.parsearEntero(cols.get(4), "Mín. jugadores (línea " + numLinea + ")");
        int maxJug = CsvUtil.parsearEntero(cols.get(5), "Máx. jugadores (línea " + numLinea + ")");
        int durMin = CsvUtil.parsearEntero(cols.get(6), "Duración mín. (línea " + numLinea + ")");
        int durMax = CsvUtil.parsearEntero(cols.get(7), "Duración máx. (línea " + numLinea + ")");
        EstadoJuego estado = EstadoJuego.fromTexto(cols.get(8));
        Integer valoracion = cols.get(9).isBlank()
                ? null
                : CsvUtil.parsearEntero(cols.get(9), "Valoración (línea " + numLinea + ")");

        return new Juego(id, titulo, editorial, anio, minJug, maxJug, durMin, durMax, estado, valoracion);
    }

    private String serializarJuego(Juego j) {
        String valTexto = j.getValoracion() == null ? "" : String.valueOf(j.getValoracion());
        return j.getId()
                + ";" + CsvUtil.formatearTexto(j.getTitulo())
                + ";" + CsvUtil.formatearTexto(j.getEditorial())
                + ";" + j.getAnioLanzamiento()
                + ";" + j.getMinJugadores()
                + ";" + j.getMaxJugadores()
                + ";" + j.getDuracionMinima()
                + ";" + j.getDuracionMaxima()
                + ";" + CsvUtil.formatearTexto(j.getEstado().getEtiqueta())
                + ";" + valTexto;
    }
}
