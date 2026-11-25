package es.uv.is2.juegos.persistencia;

import es.uv.is2.juegos.dominio.Partida;
import es.uv.is2.juegos.dominio.TipoGanador;
import es.uv.is2.juegos.dominio.excepcion.PersistenciaException;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import es.uv.is2.juegos.dominio.repositorio.PartidaRepositorio;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementación del repositorio de partidas basada en archivo CSV.
 */
public class CsvPartidaRepositorio implements PartidaRepositorio {

    public static final String CABECERA = "\"ID\";\"JuegoID\";\"Participantes\";\"TieneGanador\";\"Ganador\";\"TipoGanador\";\"Fecha\"";
    private static final int NUM_COLUMNAS = 7;

    private final Path rutaArchivo;

    public CsvPartidaRepositorio(Path rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public Path getRutaArchivo() {
        return rutaArchivo;
    }

    @Override
    public List<Partida> obtenerTodas() {
        if (!Files.exists(rutaArchivo)) {
            return new ArrayList<>();
        }
        try {
            List<String> lineas = Files.readAllLines(rutaArchivo, StandardCharsets.UTF_8);
            if (lineas.isEmpty()) {
                return new ArrayList<>();
            }
            List<Partida> partidas = new ArrayList<>();
            Set<Integer> idsVistos = new HashSet<>();

            for (int i = 1; i < lineas.size(); i++) {
                String linea = lineas.get(i);
                if (linea.isBlank()) {
                    continue;
                }
                Partida partida = deserializarLinea(linea, i + 1);
                if (!idsVistos.add(partida.getId())) {
                    throw new ValidacionException("Identificador de partida duplicado en CSV (línea " + (i + 1) + "): " + partida.getId());
                }
                partidas.add(partida);
            }
            return partidas;
        } catch (IOException e) {
            throw new PersistenciaException("Error al leer el archivo de partidas '" + rutaArchivo + "': " + e.getMessage(), e);
        }
    }

    @Override
    public List<Partida> buscarPorJuegoId(int juegoId) {
        return obtenerTodas().stream()
                .filter(p -> p.getJuegoId() == juegoId)
                .toList();
    }

    @Override
    public Partida guardar(Partida partida) {
        if (partida == null) {
            throw new ValidacionException("La partida a guardar no puede ser nula.");
        }
        List<Partida> todas = new ArrayList<>(obtenerTodas());
        boolean duplicado = todas.stream().anyMatch(p -> p.getId() == partida.getId());
        if (duplicado) {
            throw new ValidacionException("Ya existe una partida con el identificador " + partida.getId() + ".");
        }
        todas.add(partida);
        guardarTodas(todas);
        return partida;
    }

    @Override
    public int obtenerSiguienteId() {
        return obtenerTodas().stream()
                .mapToInt(Partida::getId)
                .max()
                .orElse(0) + 1;
    }

    private void guardarTodas(List<Partida> partidas) {
        List<String> lineas = new ArrayList<>();
        lineas.add(CABECERA);
        for (Partida p : partidas) {
            lineas.add(serializarPartida(p));
        }
        CsvUtil.escribirArchivoSeguro(rutaArchivo, lineas);
    }

    private Partida deserializarLinea(String linea, int numLinea) {
        List<String> cols = CsvUtil.parsearLinea(linea);
        if (cols.size() != NUM_COLUMNAS) {
            throw new ValidacionException("Formato CSV inválido en partidas (línea " + numLinea
                    + "): se esperaban " + NUM_COLUMNAS + " columnas y se encontraron " + cols.size() + ".");
        }
        int id = CsvUtil.parsearEntero(cols.get(0), "ID partida (línea " + numLinea + ")");
        int juegoId = CsvUtil.parsearEntero(cols.get(1), "JuegoID (línea " + numLinea + ")");
        int participantes = CsvUtil.parsearEntero(cols.get(2), "Participantes (línea " + numLinea + ")");
        String tieneGanadorTexto = cols.get(3);
        String ganadorTexto = cols.get(4);
        String tipoTexto = cols.get(5);
        LocalDate fecha = parsearFecha(cols.get(6), numLinea);

        boolean tieneGanador;
        if ("SI".equalsIgnoreCase(tieneGanadorTexto) || "TRUE".equalsIgnoreCase(tieneGanadorTexto)) {
            tieneGanador = true;
        } else if ("NO".equalsIgnoreCase(tieneGanadorTexto) || "FALSE".equalsIgnoreCase(tieneGanadorTexto)) {
            tieneGanador = false;
        } else {
            throw new ValidacionException("Valor inválido para TieneGanador en línea " + numLinea + ": '" + tieneGanadorTexto + "'.");
        }

        if (!tieneGanador) {
            return new Partida(id, juegoId, participantes, null, null, fecha);
        }
        if (ganadorTexto.isBlank()) {
            throw new ValidacionException("La partida en línea " + numLinea + " indica que tiene ganador pero el campo Ganador está vacío.");
        }
        TipoGanador tipo = TipoGanador.fromTexto(tipoTexto);
        return new Partida(id, juegoId, participantes, ganadorTexto, tipo, fecha);
    }

    private LocalDate parsearFecha(String texto, int numLinea) {
        if (texto == null || texto.isBlank()) {
            throw new ValidacionException("La fecha de la partida en línea " + numLinea + " es obligatoria.");
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new ValidacionException("Formato de fecha inválido en línea " + numLinea + " (use AAAA-MM-DD): '" + texto + "'.", e);
        }
    }

    private String serializarPartida(Partida p) {
        String tieneGanador = p.tuvoGanador() ? "SI" : "NO";
        String ganador = p.tuvoGanador() ? CsvUtil.formatearTexto(p.getGanador()) : "\"\"";
        String tipo = p.tuvoGanador() ? CsvUtil.formatearTexto(p.getTipoGanador().getEtiqueta()) : "\"\"";
        return p.getId()
                + ";" + p.getJuegoId()
                + ";" + p.getNumeroParticipantes()
                + ";" + tieneGanador
                + ";" + ganador
                + ";" + tipo
                + ";" + p.getFecha();
    }
}
