package es.uv.is2.juegos.presentacion;

import es.uv.is2.juegos.aplicacion.CatalogoService;
import es.uv.is2.juegos.aplicacion.FiltroJuegosService;
import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.excepcion.DominioException;
import es.uv.is2.juegos.dominio.excepcion.PersistenciaException;
import es.uv.is2.juegos.presentacion.modelo.JuegoTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Panel Swing para RF-04: Selección dinámica de criterio de filtrado y ejecución validada.
 * Muestra únicamente los campos correspondientes al filtro seleccionado y oculta los demás.
 * Responsabilidad asignada al Integrante D.
 */
public class FiltrosPanel extends JPanel {

    private static final String SIN_VALORACION = "Sin valoración";

    /**
     * Criterios independientes seleccionables en la interfaz de filtros.
     */
    public enum CriterioFiltro {
        NOMBRE("Nombre"),
        VALORACION("Valoración"),
        MIN_JUGADORES("Número mínimo de jugadores"),
        MAX_JUGADORES("Número máximo de jugadores");

        private final String etiqueta;

        CriterioFiltro(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        @Override
        public String toString() {
            return etiqueta;
        }
    }

    private final transient CatalogoService catalogoService;
    private final transient FiltroJuegosService filtroJuegosService;

    private final JuegoTableModel tableModel;
    private final JTable tablaResultados;

    private final JComboBox<CriterioFiltro> cmbCriterio;
    private final CardLayout cardLayout;
    private final JPanel panelCamposCriterio;

    private final JPanel panelNombre;
    private final JPanel panelValoracion;
    private final JPanel panelMinJugadores;
    private final JPanel panelMaxJugadores;

    private final JTextField txtNombre;
    private final JComboBox<String> cmbValoracion;
    private final JTextField txtMinJugadores;
    private final JTextField txtMaxJugadores;

    private final JLabel lblValidacion;
    private final JLabel lblResumen;

    public FiltrosPanel(CatalogoService catalogoService, FiltroJuegosService filtroJuegosService) {
        this.catalogoService = catalogoService;
        this.filtroJuegosService = filtroJuegosService;
        this.tableModel = new JuegoTableModel();
        this.tablaResultados = new JTable(tableModel);

        this.cmbCriterio = new JComboBox<>(CriterioFiltro.values());
        this.cardLayout = new CardLayout();
        this.panelCamposCriterio = new JPanel(cardLayout);

        this.panelNombre = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        this.panelValoracion = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        this.panelMinJugadores = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        this.panelMaxJugadores = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));

        this.txtNombre = new JTextField(18);
        this.cmbValoracion = new JComboBox<>(new String[]{SIN_VALORACION, "1", "2", "3", "4", "5"});
        this.txtMinJugadores = new JTextField(6);
        this.txtMaxJugadores = new JTextField(6);

        this.lblValidacion = new JLabel(" ");
        this.lblResumen = new JLabel(" ");

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFiltros = new JPanel(new GridLayout(4, 1, 4, 4));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de juegos (RF-04)"));

        JPanel filaSelector = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        filaSelector.add(new JLabel("Criterio de filtrado:"));
        filaSelector.add(cmbCriterio);

        panelNombre.add(new JLabel("Nombre contiene:"));
        panelNombre.add(txtNombre);

        panelValoracion.add(new JLabel("Valoración:"));
        panelValoracion.add(cmbValoracion);

        panelMinJugadores.add(new JLabel("Mín. jugadores (>=, entero > 0):"));
        panelMinJugadores.add(txtMinJugadores);

        panelMaxJugadores.add(new JLabel("Máx. jugadores (<=, entero > 0):"));
        panelMaxJugadores.add(txtMaxJugadores);

        panelCamposCriterio.add(panelNombre, CriterioFiltro.NOMBRE.name());
        panelCamposCriterio.add(panelValoracion, CriterioFiltro.VALORACION.name());
        panelCamposCriterio.add(panelMinJugadores, CriterioFiltro.MIN_JUGADORES.name());
        panelCamposCriterio.add(panelMaxJugadores, CriterioFiltro.MAX_JUGADORES.name());

        JPanel filaAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        JButton btnAplicar = new JButton("Aplicar filtro");
        JButton btnVerTodos = new JButton("Mostrar todos los juegos");
        filaAcciones.add(btnAplicar);
        filaAcciones.add(btnVerTodos);
        filaAcciones.add(lblResumen);

        lblValidacion.setForeground(new Color(180, 0, 0));
        JPanel filaValidacion = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        filaValidacion.add(lblValidacion);

        panelFiltros.add(filaSelector);
        panelFiltros.add(panelCamposCriterio);
        panelFiltros.add(filaAcciones);
        panelFiltros.add(filaValidacion);

        tablaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaResultados.setAutoCreateRowSorter(true);

        add(panelFiltros, BorderLayout.NORTH);
        add(new JScrollPane(tablaResultados), BorderLayout.CENTER);

        cmbCriterio.addActionListener(e -> actualizarPanelCriterio());
        btnAplicar.addActionListener(e -> aplicarFiltroSeleccionado());
        btnVerTodos.addActionListener(e -> recargar());

        DocumentListener listenerValidacion = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validarEntradaActual();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validarEntradaActual();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validarEntradaActual();
            }
        };

        txtMinJugadores.getDocument().addDocumentListener(listenerValidacion);
        txtMaxJugadores.getDocument().addDocumentListener(listenerValidacion);

        actualizarPanelCriterio();
    }

    public void recargar() {
        try {
            List<Juego> todos = catalogoService.listarCatalogo();
            tableModel.setJuegos(todos);
            lblResumen.setText("Mostrando todos los juegos (" + todos.size() + ").");
            lblValidacion.setText(" ");
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    public void seleccionarCriterio(CriterioFiltro criterio) {
        cmbCriterio.setSelectedItem(criterio);
    }

    public CriterioFiltro getCriterioSeleccionado() {
        return (CriterioFiltro) cmbCriterio.getSelectedItem();
    }

    public boolean esPanelNombreVisible() {
        return panelNombre.isVisible();
    }

    public boolean esPanelValoracionVisible() {
        return panelValoracion.isVisible();
    }

    public boolean esPanelMinJugadoresVisible() {
        return panelMinJugadores.isVisible();
    }

    public boolean esPanelMaxJugadoresVisible() {
        return panelMaxJugadores.isVisible();
    }

    public String getMensajeValidacion() {
        return lblValidacion.getText();
    }

    void setTextoMinJugadores(String texto) {
        txtMinJugadores.setText(texto);
    }

    void setTextoMaxJugadores(String texto) {
        txtMaxJugadores.setText(texto);
    }

    private void actualizarPanelCriterio() {
        CriterioFiltro criterio = getCriterioSeleccionado();
        if (criterio != null) {
            cardLayout.show(panelCamposCriterio, criterio.name());
        }
        validarEntradaActual();
        revalidate();
        repaint();
    }

    private void validarEntradaActual() {
        CriterioFiltro criterio = getCriterioSeleccionado();
        if (criterio == null) {
            lblValidacion.setText(" ");
            return;
        }
        try {
            switch (criterio) {
                case MIN_JUGADORES -> validarCampoEnteroOpcional(txtMinJugadores.getText(), "mínimo");
                case MAX_JUGADORES -> validarCampoEnteroOpcional(txtMaxJugadores.getText(), "máximo");
                default -> lblValidacion.setText(" ");
            }
        } catch (DominioException ex) {
            lblValidacion.setText(ex.getMessage());
        } catch (NumberFormatException ex) {
            lblValidacion.setText("Introduzca únicamente números enteros positivos mayores que 0.");
        }
    }

    private void validarCampoEnteroOpcional(String texto, String tipoLimite) {
        if (texto == null || texto.isBlank()) {
            lblValidacion.setText(" ");
            return;
        }
        int valor = Integer.parseInt(texto.trim());
        filtroJuegosService.validarNumeroJugadoresPositivo(valor, tipoLimite);
        lblValidacion.setText(" ");
    }

    private void aplicarFiltroSeleccionado() {
        CriterioFiltro criterio = getCriterioSeleccionado();
        if (criterio == null) {
            return;
        }
        switch (criterio) {
            case NOMBRE -> filtrarPorNombre();
            case VALORACION -> filtrarPorValoracion();
            case MIN_JUGADORES -> filtrarPorMinJugadores();
            case MAX_JUGADORES -> filtrarPorMaxJugadores();
        }
    }

    private void filtrarPorNombre() {
        try {
            List<Juego> resultado = filtroJuegosService.filtrarPorNombre(txtNombre.getText());
            actualizarTabla(resultado, "Filtro por nombre '" + txtNombre.getText().trim() + "'");
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void filtrarPorValoracion() {
        try {
            String seleccion = (String) cmbValoracion.getSelectedItem();
            Integer valoracion = SIN_VALORACION.equals(seleccion) ? null : Integer.parseInt(seleccion);
            List<Juego> resultado = filtroJuegosService.filtrarPorValoracion(valoracion);
            actualizarTabla(resultado, "Filtro por valoración (" + seleccion + ")");
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void filtrarPorMinJugadores() {
        try {
            int min = Integer.parseInt(txtMinJugadores.getText().trim());
            List<Juego> resultado = filtroJuegosService.filtrarPorMinJugadores(min);
            actualizarTabla(resultado, "Filtro por mín. jugadores >= " + min);
        } catch (NumberFormatException ex) {
            mostrarError("Introduzca un número entero mayor que 0 para el mínimo de jugadores.");
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void filtrarPorMaxJugadores() {
        try {
            int max = Integer.parseInt(txtMaxJugadores.getText().trim());
            List<Juego> resultado = filtroJuegosService.filtrarPorMaxJugadores(max);
            actualizarTabla(resultado, "Filtro por máx. jugadores <= " + max);
        } catch (NumberFormatException ex) {
            mostrarError("Introduzca un número entero mayor que 0 para el máximo de jugadores.");
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void actualizarTabla(List<Juego> juegos, String descripcion) {
        lblValidacion.setText(" ");
        tableModel.setJuegos(juegos);
        lblResumen.setText(descripcion + " -> " + juegos.size() + " juego(s) encontrado(s).");
    }

    private void mostrarError(String mensaje) {
        lblValidacion.setText(mensaje);
        JOptionPane.showMessageDialog(this, mensaje, "Error de filtrado", JOptionPane.ERROR_MESSAGE);
    }
}
