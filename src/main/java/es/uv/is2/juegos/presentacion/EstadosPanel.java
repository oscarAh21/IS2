package es.uv.is2.juegos.presentacion;

import es.uv.is2.juegos.aplicacion.CatalogoService;
import es.uv.is2.juegos.aplicacion.EstadoJuegoService;
import es.uv.is2.juegos.dominio.EstadoJuego;
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
 * Panel Swing para RF-02: Consulta y modificación del estado de un juego.
 * Responsabilidad asignada al Integrante B.
 */
public class EstadosPanel extends JPanel {

    private final transient CatalogoService catalogoService;
    private final transient EstadoJuegoService estadoJuegoService;
    private final transient Runnable onDatosActualizados;

    private final JuegoTableModel tableModel;
    private final JTable tablaJuegos;
    private final JLabel lblJuegoSeleccionado;
    private final JLabel lblEstadoActual;
    private final JComboBox<EstadoJuego> cmbNuevoEstado;
    private final JLabel lblResultado;

    public EstadosPanel(CatalogoService catalogoService,
                        EstadoJuegoService estadoJuegoService,
                        Runnable onDatosActualizados) {
        this.catalogoService = catalogoService;
        this.estadoJuegoService = estadoJuegoService;
        this.onDatosActualizados = onDatosActualizados;
        this.tableModel = new JuegoTableModel();
        this.tablaJuegos = new JTable(tableModel);
        this.lblJuegoSeleccionado = new JLabel("Ningún juego seleccionado");
        this.lblEstadoActual = new JLabel("-");
        this.cmbNuevoEstado = new JComboBox<>(EstadoJuego.values());
        this.lblResultado = new JLabel(" ");

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tablaJuegos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaJuegos.setAutoCreateRowSorter(true);

        JPanel panelControles = new JPanel(new GridLayout(3, 1, 6, 6));
        panelControles.setBorder(BorderFactory.createTitledBorder("Gestión de estados (RF-02)"));

        JPanel filaInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filaInfo.add(new JLabel("Juego seleccionado:"));
        filaInfo.add(lblJuegoSeleccionado);
        filaInfo.add(new JLabel("| Estado actual:"));
        filaInfo.add(lblEstadoActual);

        JPanel filaAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        JButton btnConsultar = new JButton("Consultar estado");
        JButton btnCambiar = new JButton("Cambiar estado");
        filaAcciones.add(btnConsultar);
        filaAcciones.add(new JLabel("Nuevo estado:"));
        filaAcciones.add(cmbNuevoEstado);
        filaAcciones.add(btnCambiar);

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

        btnConsultar.addActionListener(e -> consultarEstadoSeleccionado());
        btnCambiar.addActionListener(e -> cambiarEstadoSeleccionado());
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
            lblEstadoActual.setText("-");
        } else {
            lblJuegoSeleccionado.setText("#" + seleccionado.getId() + " - " + seleccionado.getTitulo());
            lblEstadoActual.setText(seleccionado.getEstado().getEtiqueta());
            cmbNuevoEstado.setSelectedItem(seleccionado.getEstado());
        }
    }

    private void consultarEstadoSeleccionado() {
        Juego seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            mostrarError("Seleccione primero un juego de la tabla.");
            return;
        }
        try {
            EstadoJuego estado = estadoJuegoService.consultarEstado(seleccionado.getId());
            String mensaje = "El juego '" + seleccionado.getTitulo() + "' (ID #" + seleccionado.getId()
                    + ") tiene el estado: " + estado.getEtiqueta() + ".";
            lblEstadoActual.setText(estado.getEtiqueta());
            lblResultado.setText(mensaje);
            JOptionPane.showMessageDialog(this, mensaje, "Estado del juego", JOptionPane.INFORMATION_MESSAGE);
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void cambiarEstadoSeleccionado() {
        Juego seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            mostrarError("Seleccione primero un juego de la tabla.");
            return;
        }
        EstadoJuego nuevoEstado = (EstadoJuego) cmbNuevoEstado.getSelectedItem();
        try {
            Juego actualizado = estadoJuegoService.cambiarEstado(seleccionado.getId(), nuevoEstado);
            String mensaje = "Estado actualizado correctamente: '" + actualizado.getTitulo()
                    + "' ahora está en estado " + actualizado.getEstado().getEtiqueta() + ".";
            lblResultado.setText(mensaje);
            onDatosActualizados.run();
            JOptionPane.showMessageDialog(this, mensaje, "Operación completada", JOptionPane.INFORMATION_MESSAGE);
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        lblResultado.setText("Error: " + mensaje);
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
