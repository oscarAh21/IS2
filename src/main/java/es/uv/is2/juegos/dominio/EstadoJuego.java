package es.uv.is2.juegos.dominio;

import es.uv.is2.juegos.dominio.excepcion.ValidacionException;

/**
 * Estados permitidos para un juego de mesa en el catálogo.
 */
public enum EstadoJuego {
    DISPONIBLE("Disponible"),
    DANADO("Dañado"),
    JUGADO("Jugado"),
    COMPLETO("Completo"),
    PRESTADO("Prestado");

    private final String etiqueta;

    EstadoJuego(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Convierte un texto (etiqueta o nombre del enum) en un {@link EstadoJuego}.
     */
    public static EstadoJuego fromTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ValidacionException("El estado del juego es obligatorio.");
        }
        String normalizado = texto.trim();
        for (EstadoJuego estado : values()) {
            if (estado.name().equalsIgnoreCase(normalizado)
                    || estado.etiqueta.equalsIgnoreCase(normalizado)) {
                return estado;
            }
        }
        throw new ValidacionException("Estado de juego no válido: '" + texto
                + "'. Valores permitidos: Disponible, Dañado, Jugado, Completo, Prestado.");
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
