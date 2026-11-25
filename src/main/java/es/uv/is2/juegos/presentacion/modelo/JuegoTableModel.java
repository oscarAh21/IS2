package es.uv.is2.juegos.presentacion.modelo;

import es.uv.is2.juegos.dominio.Juego;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Modelo de tabla Swing para mostrar una lista de juegos de mesa.
 */
public class JuegoTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS = {
            "ID", "Título", "Editorial", "Año", "Mín. jug.", "Máx. jug.", "Duración", "Estado", "Valoración"
    };

    private List<Juego> juegos = new ArrayList<>();

    public void setJuegos(List<Juego> juegos) {
        this.juegos = new ArrayList<>(juegos);
        fireTableDataChanged();
    }

    public Juego getJuegoEn(int fila) {
        if (fila < 0 || fila >= juegos.size()) {
            return null;
        }
        return juegos.get(fila);
    }

    @Override
    public int getRowCount() {
        return juegos.size();
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
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0, 3, 4, 5 -> Integer.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Juego juego = juegos.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> juego.getId();
            case 1 -> juego.getTitulo();
            case 2 -> juego.getEditorial();
            case 3 -> juego.getAnioLanzamiento();
            case 4 -> juego.getMinJugadores();
            case 5 -> juego.getMaxJugadores();
            case 6 -> juego.getDuracionFormateada();
            case 7 -> juego.getEstado().getEtiqueta();
            case 8 -> juego.getValoracion() == null ? "Sin valoración" : juego.getValoracion() + " / 5";
            default -> "";
        };
    }
}
