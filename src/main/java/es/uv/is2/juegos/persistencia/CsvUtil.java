package es.uv.is2.juegos.persistencia;

import es.uv.is2.juegos.dominio.excepcion.PersistenciaException;
import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilidades para lectura, escritura y validación de archivos CSV con separador ';'.
 */
public final class CsvUtil {

    public static final char SEPARADOR = ';';

    private CsvUtil() {
    }

    /**
     * Parsea una línea CSV delimitada por ';' respetando campos entrecomillados y comillas escapadas.
     */
    public static List<String> parsearLinea(String linea) {
        if (linea == null) {
            throw new ValidacionException("La línea CSV no puede ser nula.");
        }
        String limpia = eliminarBom(linea);
        List<String> campos = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean enComillas = false;

        for (int i = 0; i < limpia.length(); i++) {
            char c = limpia.charAt(i);
            if (enComillas) {
                if (c == '"') {
                    if (i + 1 < limpia.length() && limpia.charAt(i + 1) == '"') {
                        actual.append('"');
                        i++;
                    } else {
                        enComillas = false;
                    }
                } else {
                    actual.append(c);
                }
            } else {
                if (c == '"') {
                    enComillas = true;
                } else if (c == SEPARADOR) {
                    campos.add(actual.toString().trim());
                    actual.setLength(0);
                } else {
                    actual.append(c);
                }
            }
        }
        if (enComillas) {
            throw new ValidacionException("Formato CSV inválido: comillas sin cerrar en la línea -> " + linea);
        }
        campos.add(actual.toString().trim());
        return campos;
    }

    private static String eliminarBom(String texto) {
        if (!texto.isEmpty() && texto.charAt(0) == '\uFEFF') {
            return texto.substring(1);
        }
        return texto;
    }

    /**
     * Escapa y entrecomilla un valor de texto para escritura segura en CSV.
     */
    public static String formatearTexto(String valor) {
        if (valor == null) {
            return "";
        }
        String escapado = valor.replace("\"", "\"\"");
        return "\"" + escapado + "\"";
    }

    /**
     * Parsea un entero validando que no esté vacío ni tenga formato incorrecto.
     */
    public static int parsearEntero(String valor, String nombreCampo) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacionException("El campo '" + nombreCampo + "' es obligatorio y debe ser un número entero.");
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            throw new ValidacionException("El campo '" + nombreCampo + "' debe ser un número entero válido (valor recibido: '" + valor + "').", e);
        }
    }

    /**
     * Parsea una duración expresada como valor único ("60") o intervalo ("30-60").
     *
     * @return arreglo de dos enteros: [duracionMinima, duracionMaxima]
     */
    public static int[] parsearDuracion(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ValidacionException("La duración de la partida es obligatoria.");
        }
        String limpio = texto.trim();
        if (limpio.contains("-")) {
            String[] partes = limpio.split("-", -1);
            if (partes.length != 2) {
                throw new ValidacionException("Formato de duración inválido: '" + texto + "'. Use un número o un intervalo 'min-max'.");
            }
            int min = parsearEntero(partes[0], "duración mínima");
            int max = parsearEntero(partes[1], "duración máxima");
            return new int[]{min, max};
        }
        int valor = parsearEntero(limpio, "duración");
        return new int[]{valor, valor};
    }

    /**
     * Escribe un archivo de forma segura mediante un archivo temporal en el mismo directorio
     * para evitar corrupción si ocurre un fallo durante la escritura.
     */
    public static void escribirArchivoSeguro(Path destino, List<String> lineas) {
        try {
            Path padre = destino.toAbsolutePath().getParent();
            if (padre != null) {
                Files.createDirectories(padre);
            }
            Path temporal = Files.createTempFile(padre, destino.getFileName().toString(), ".tmp");
            try {
                Files.write(temporal, lineas, StandardCharsets.UTF_8);
                try {
                    Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                Files.deleteIfExists(temporal);
                throw e;
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al escribir el archivo CSV '" + destino + "': " + e.getMessage(), e);
        }
    }
}
