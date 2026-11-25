package es.uv.is2.juegos.presentacion;

import es.uv.is2.juegos.aplicacion.CatalogoService;
import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.excepcion.DominioException;
import es.uv.is2.juegos.dominio.excepcion.PersistenciaException;
import es.uv.is2.juegos.presentacion.modelo.JuegoTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 * Panel Swing para RF-01: Visualización del catálogo completo y consulta por nombre exacto.
 * Responsabilidad asignada al Integrante A.
 */
public class CatalogoPanel extends JPanel {

    private final transient CatalogoService catalogoService;
    private final JuegoTableModel tableModel;
    private final JTable tablaJuegos;
    private final JTextField txtNombreExacto;
    private final JLabel lblResumen;
    private final JLabel lblDetalle;

    public CatalogoPanel(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
        this.tableModel = new JuegoTableModel();
        this.tablaJuegos = new JTable(tableModel);
        this.txtNombreExacto = new JTextField(25);
        this.lblResumen = new JLabel(" ");
        this.lblDetalle = new JLabel("Seleccione un juego de la tabla para ver su información detallada.");

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Consulta por nombre exacto (RF-01)"));
        JButton btnBuscar = new JButton("Buscar por nombre exacto");
        JButton btnVerTodos = new JButton("Ver catálogo completo");

        panelBusqueda.add(new JLabel("Nombre exacto:"));
        panelBusqueda.add(txtNombreExacto);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnVerTodos);

        tablaJuegos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaJuegos.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(tablaJuegos);

        JPanel panelInferior = new JPanel(new GridLayout(2, 1, 4, 4));
        panelInferior.setBorder(BorderFactory.createTitledBorder("Información del juego seleccionado"));
        panelInferior.add(lblDetalle);
        panelInferior.add(lblResumen);

        add(panelBusqueda, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> buscarPorNombreExacto());
        btnVerTodos.addActionListener(e -> recargar());
        txtNombreExacto.addActionListener(e -> buscarPorNombreExacto());

        tablaJuegos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalleSeleccionado();
            }
        });
    }

    public void recargar() {
        try {
            List<Juego> juegos = catalogoService.listarCatalogo();
            tableModel.setJuegos(juegos);
            lblResumen.setText("Mostrando " + juegos.size() + " juegos del catálogo.");
            lblDetalle.setText("Seleccione un juego de la tabla para ver su información detallada.");
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void buscarPorNombreExacto() {
        try {
            List<Juego> encontrados = catalogoService.buscarPorNombreExacto(txtNombreExacto.getText());
            tableModel.setJuegos(encontrados);
            lblResumen.setText("Resultados exactos encontrados: " + encontrados.size());
            if (encontrados.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró ningún juego con el nombre exacto '" + txtNombreExacto.getText().trim() + "'.",
                        "Sin resultados",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void mostrarDetalleSeleccionado() {
        int filaVista = tablaJuegos.getSelectedRow();
        if (filaVista < 0) {
            return;
        }
        int filaModelo = tablaJuegos.convertRowIndexToModel(filaVista);
        Juego j = tableModel.getJuegoEn(filaModelo);
        if (j != null) {
            String val = j.getValoracion() == null ? "Sin valoración" : j.getValoracion() + "/5";
            lblDetalle.setText(String.format(
                    "ID #%d | %s (%d) | Editorial: %s | Jugadores: %s | Duración: %s | Estado: %s | Valoración: %s",
                    j.getId(), j.getTitulo(), j.getAnioLanzamiento(), j.getEditorial(),
                    j.getJugadoresFormateados(), j.getDuracionFormateada(),
                    j.getEstado().getEtiqueta(), val));
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
