package es.uv.is2.juegos.dominio;

import es.uv.is2.juegos.dominio.excepcion.ValidacionException;

/**
 * Tipo de ganador de una partida cuando existe ganador.
 */
public enum TipoGanador {
    INDIVIDUAL("Individual"),
    EQUIPO("Equipo");

    private final String etiqueta;

    TipoGanador(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Convierte un texto en un {@link TipoGanador}.
     */
    public static TipoGanador fromTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ValidacionException("El tipo de ganador es obligatorio cuando hay ganador.");
        }
        String normalizado = texto.trim();
        for (TipoGanador tipo : values()) {
            if (tipo.name().equalsIgnoreCase(normalizado)
                    || tipo.etiqueta.equalsIgnoreCase(normalizado)) {
                return tipo;
            }
        }
        throw new ValidacionException("Tipo de ganador no válido: '" + texto
                + "'. Valores permitidos: Individual, Equipo.");
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
