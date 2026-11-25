package es.uv.is2.juegos.presentacion;

import es.uv.is2.juegos.aplicacion.CatalogoService;
import es.uv.is2.juegos.aplicacion.ValoracionService;
import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.excepcion.DominioException;
import es.uv.is2.juegos.dominio.excepcion.PersistenciaException;
import es.uv.is2.juegos.presentacion.modelo.JuegoTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

/**
 * Panel Swing para RF-03: Asignar, modificar y eliminar valoraciones (1 a 5).
 * Responsabilidad asignada al Integrante C.
 */
public class ValoracionesPanel extends JPanel {

    private final transient CatalogoService catalogoService;
    private final transient ValoracionService valoracionService;
    private final transient Runnable onDatosActualizados;

    private final JuegoTableModel tableModel;
    private final JTable tablaJuegos;
    private final JLabel lblJuegoSeleccionado;
    private final JLabel lblValoracionActual;
    private final JComboBox<Integer> cmbValoracion;
    private final JLabel lblResultado;

    public ValoracionesPanel(CatalogoService catalogoService,
                             ValoracionService valoracionService,
                             Runnable onDatosActualizados) {
        this.catalogoService = catalogoService;
        this.valoracionService = valoracionService;
        this.onDatosActualizados = onDatosActualizados;
        this.tableModel = new JuegoTableModel();
        this.tablaJuegos = new JTable(tableModel);
        this.lblJuegoSeleccionado = new JLabel("Ningún juego seleccionado");
        this.lblValoracionActual = new JLabel("-");
        this.cmbValoracion = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        this.lblResultado = new JLabel(" ");

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tablaJuegos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaJuegos.setAutoCreateRowSorter(true);

        JPanel panelControles = new JPanel(new GridLayout(3, 1, 6, 6));
        panelControles.setBorder(BorderFactory.createTitledBorder("Gestión de valoraciones (RF-03)"));

        JPanel filaInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filaInfo.add(new JLabel("Juego seleccionado:"));
        filaInfo.add(lblJuegoSeleccionado);
        filaInfo.add(new JLabel("| Valoración actual:"));
        filaInfo.add(lblValoracionActual);

        JPanel filaAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        JButton btnAsignar = new JButton("Asignar valoración");
        JButton btnModificar = new JButton("Modificar valoración");
        JButton btnEliminar = new JButton("Eliminar valoración");
        filaAcciones.add(new JLabel("Valoración (1-5):"));
        filaAcciones.add(cmbValoracion);
        filaAcciones.add(btnAsignar);
        filaAcciones.add(btnModificar);
        filaAcciones.add(btnEliminar);

        JPanel filaResultado = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filaResultado.add(lblResultado);

        panelControles.add(filaInfo);
        panelControles.add(filaAcciones);
        panelControles.add(filaResultado);

        add(new JScrollPane(tablaJuegos), BorderLayout.CENTER);
        add(panelControles, BorderLayout.SOUTH);

        tablaJuegos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarSeleccion();
            }
        });

        btnAsignar.addActionListener(e -> asignarValoracion());
        btnModificar.addActionListener(e -> modificarValoracion());
        btnEliminar.addActionListener(e -> eliminarValoracion());
    }

    public void recargar() {
        try {
            tableModel.setJuegos(catalogoService.listarCatalogo());
            actualizarSeleccion();
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private Juego obtenerSeleccionado() {
        int filaVista = tablaJuegos.getSelectedRow();
        if (filaVista < 0) {
            return null;
        }
        return tableModel.getJuegoEn(tablaJuegos.convertRowIndexToModel(filaVista));
    }

    private void actualizarSeleccion() {
        Juego seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            lblJuegoSeleccionado.setText("Ningún juego seleccionado");
            lblValoracionActual.setText("-");
        } else {
            lblJuegoSeleccionado.setText("#" + seleccionado.getId() + " - " + seleccionado.getTitulo());
            String valTexto = seleccionado.getValoracion() == null
                    ? "Sin valoración"
                    : seleccionado.getValoracion() + " / 5";
            lblValoracionActual.setText(valTexto);
            if (seleccionado.getValoracion() != null) {
                cmbValoracion.setSelectedItem(seleccionado.getValoracion());
            }
        }
    }

    private void asignarValoracion() {
        Juego seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            mostrarError("Seleccione primero un juego de la tabla.");
            return;
        }
        Integer valor = (Integer) cmbValoracion.getSelectedItem();
        try {
            Juego actualizado = valoracionService.asignarValoracion(seleccionado.getId(), valor);
            notificarExito("Valoración asignada: '" + actualizado.getTitulo() + "' tiene ahora " + valor + " / 5.");
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void modificarValoracion() {
        Juego seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            mostrarError("Seleccione primero un juego de la tabla.");
            return;
        }
        Integer valor = (Integer) cmbValoracion.getSelectedItem();
        try {
            Juego actualizado = valoracionService.modificarValoracion(seleccionado.getId(), valor);
            notificarExito("Valoración modificada: '" + actualizado.getTitulo() + "' tiene ahora " + valor + " / 5.");
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminarValoracion() {
        Juego seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            mostrarError("Seleccione primero un juego de la tabla.");
            return;
        }
        try {
            Juego actualizado = valoracionService.eliminarValoracion(seleccionado.getId());
            notificarExito("Valoración eliminada: '" + actualizado.getTitulo() + "' queda sin valoración.");
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void notificarExito(String mensaje) {
        lblResultado.setText(mensaje);
        onDatosActualizados.run();
        JOptionPane.showMessageDialog(this, mensaje, "Operación completada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        lblResultado.setText("Error: " + mensaje);
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
