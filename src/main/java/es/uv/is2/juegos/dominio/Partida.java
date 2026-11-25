package es.uv.is2.juegos.dominio;

import es.uv.is2.juegos.dominio.excepcion.ValidacionException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad de dominio que representa una partida jugada de un juego de mesa.
 */
public class Partida {

    private final int id;
    private final int juegoId;
    private final int numeroParticipantes;
    private final String ganador;
    private final TipoGanador tipoGanador;
    private final LocalDate fecha;

    public Partida(int id,
                   int juegoId,
                   int numeroParticipantes,
                   String ganador,
                   TipoGanador tipoGanador,
                   LocalDate fecha) {
        if (id <= 0) {
            throw new ValidacionException("El identificador de la partida debe ser un entero positivo.");
        }
        if (juegoId <= 0) {
            throw new ValidacionException("El identificador del juego asociado debe ser un entero positivo.");
        }
        if (numeroParticipantes <= 0) {
            throw new ValidacionException("El número de participantes debe ser un entero mayor que cero.");
        }
        if (fecha == null) {
            throw new ValidacionException("La fecha de la partida es obligatoria.");
        }
        if (fecha.isAfter(LocalDate.now())) {
            throw new ValidacionException("La fecha de la partida no puede ser posterior a la fecha actual.");
        }

        boolean sinGanador = (ganador == null || ganador.isBlank());
        if (sinGanador && tipoGanador != null) {
            throw new ValidacionException("No se puede indicar tipo de ganador si la partida no tiene ganador.");
        }
        if (!sinGanador && tipoGanador == null) {
            throw new ValidacionException("Cuando se indica un ganador es obligatorio especificar si es Individual o Equipo.");
        }

        this.id = id;
        this.juegoId = juegoId;
        this.numeroParticipantes = numeroParticipantes;
        this.ganador = sinGanador ? null : ganador.trim();
        this.tipoGanador = sinGanador ? null : tipoGanador;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public int getJuegoId() {
        return juegoId;
    }

    public int getNumeroParticipantes() {
        return numeroParticipantes;
    }

    public boolean tuvoGanador() {
        return ganador != null;
    }

    public String getGanador() {
        return ganador;
    }

    public TipoGanador getTipoGanador() {
        return tipoGanador;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Partida other)) {
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
        return "Partida #" + id + " (Juego #" + juegoId + ", " + fecha + ")";
    }
}
