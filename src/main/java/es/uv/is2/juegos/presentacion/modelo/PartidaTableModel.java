package es.uv.is2.juegos.presentacion.modelo;

import es.uv.is2.juegos.dominio.Partida;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 * Modelo de tabla Swing para mostrar el historial de partidas.
 */
public class PartidaTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS = {
            "ID", "Juego", "Fecha", "Participantes", "¿Ganador?", "Ganador", "Tipo ganador"
    };

    private List<Partida> partidas = new ArrayList<>();
    private Map<Integer, String> nombresJuegos = new HashMap<>();

    public void setDatos(List<Partida> partidas, Map<Integer, String> nombresJuegos) {
        this.partidas = new ArrayList<>(partidas);
        this.nombresJuegos = new HashMap<>(nombresJuegos);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return partidas.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNAS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Partida p = partidas.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> p.getId();
            case 1 -> nombresJuegos.getOrDefault(p.getJuegoId(), "Juego #" + p.getJuegoId());
            case 2 -> p.getFecha().toString();
            case 3 -> p.getNumeroParticipantes();
            case 4 -> p.tuvoGanador() ? "Sí" : "No";
            case 5 -> p.tuvoGanador() ? p.getGanador() : "Sin ganador";
            case 6 -> p.tuvoGanador() ? p.getTipoGanador().getEtiqueta() : "-";
            default -> "";
        };
    }
}
