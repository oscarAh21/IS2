package es.uv.is2.juegos.presentacion;

import es.uv.is2.juegos.aplicacion.CatalogoService;
import es.uv.is2.juegos.aplicacion.PartidaService;
import es.uv.is2.juegos.dominio.Juego;
import es.uv.is2.juegos.dominio.Partida;
import es.uv.is2.juegos.dominio.TipoGanador;
import es.uv.is2.juegos.dominio.excepcion.DominioException;
import es.uv.is2.juegos.dominio.excepcion.PersistenciaException;
import es.uv.is2.juegos.presentacion.modelo.JuegoTableModel;
import es.uv.is2.juegos.presentacion.modelo.PartidaTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * Panel Swing para RF-05: Registro de partidas, consulta del historial por juego
 * y consulta de juegos jugados durante el último año.
 * Responsabilidad asignada al Integrante E.
 */
public class PartidasPanel extends JPanel {

    private final transient CatalogoService catalogoService;
    private final transient PartidaService partidaService;

    private final JComboBox<Juego> cmbJuegoRegistro;
    private final JLabel lblInfoJuegoSeleccionado;
    private final JTextField txtParticipantes;
    private final JTextField txtFecha;
    private final JCheckBox chkSinGanador;
    private final JTextField txtGanador;
    private final JComboBox<TipoGanador> cmbTipoGanador;

    private final JComboBox<Juego> cmbJuegoHistorial;
    private final PartidaTableModel historialTableModel;
    private final JuegoTableModel ultimoAnioTableModel;
    private final JLabel lblResumenUltimoAnio;

    private Map<Integer, String> mapaNombresJuegos = new HashMap<>();

    public PartidasPanel(CatalogoService catalogoService, PartidaService partidaService) {
        this.catalogoService = catalogoService;
        this.partidaService = partidaService;

        this.cmbJuegoRegistro = new JComboBox<>();
        this.lblInfoJuegoSeleccionado = new JLabel("Seleccione un juego para consultar sus restricciones de jugadores.");
        this.txtParticipantes = new JTextField(8);
        this.txtFecha = new JTextField(LocalDate.now().toString(), 12);
        this.chkSinGanador = new JCheckBox("Partida sin ganador");
        this.txtGanador = new JTextField(18);
        this.cmbTipoGanador = new JComboBox<>(TipoGanador.values());

        this.cmbJuegoHistorial = new JComboBox<>();
        this.historialTableModel = new PartidaTableModel();
        this.ultimoAnioTableModel = new JuegoTableModel();
        this.lblResumenUltimoAnio = new JLabel(" ");

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFormulario = crearPanelRegistro();
        JTabbedPane pestanasConsultas = new JTabbedPane();
        pestanasConsultas.addTab("Historial de partidas", crearPanelHistorial());
        pestanasConsultas.addTab("Juegos jugados en el último año", crearPanelUltimoAnio());

        add(panelFormulario, BorderLayout.NORTH);
        add(pestanasConsultas, BorderLayout.CENTER);
    }

    private JPanel crearPanelRegistro() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registrar nueva partida (RF-05)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Juego:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(cmbJuegoRegistro, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblInfoJuegoSeleccionado.setBorder(BorderFactory.createTitledBorder("Información del juego seleccionado"));
        panel.add(lblInfoJuegoSeleccionado, gbc);

        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Participantes:"), gbc);
        gbc.gridx = 1;
        panel.add(txtParticipantes, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Fecha (AAAA-MM-DD):"), gbc);
        gbc.gridx = 3;
        panel.add(txtFecha, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        panel.add(chkSinGanador, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Ganador:"), gbc);
        gbc.gridx = 1;
        panel.add(txtGanador, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Tipo ganador:"), gbc);
        gbc.gridx = 3;
        panel.add(cmbTipoGanador, gbc);

        JButton btnRegistrar = new JButton("Registrar partida");
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4;
        panel.add(btnRegistrar, gbc);

        cmbJuegoRegistro.addActionListener(e -> actualizarInfoJuegoSeleccionado());
        chkSinGanador.addActionListener(e -> actualizarEstadoCamposGanador());
        btnRegistrar.addActionListener(e -> registrarPartida());

        return panel;
    }

    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnConsultarJuego = new JButton("Consultar historial del juego");
        JButton btnVerTodas = new JButton("Ver todas las partidas");
        barra.add(new JLabel("Juego:"));
        barra.add(cmbJuegoHistorial);
        barra.add(btnConsultarJuego);
        barra.add(btnVerTodas);

        JTable tablaHistorial = new JTable(historialTableModel);
        tablaHistorial.setAutoCreateRowSorter(true);

        panel.add(barra, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

        btnConsultarJuego.addActionListener(e -> consultarHistorialSeleccionado());
        btnVerTodas.addActionListener(e -> cargarTodasLasPartidas());

        return panel;
    }

    private JPanel crearPanelUltimoAnio() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnActualizar = new JButton("Consultar juegos jugados en el último año");
        barra.add(btnActualizar);
        barra.add(lblResumenUltimoAnio);

        JTable tablaUltimoAnio = new JTable(ultimoAnioTableModel);
        tablaUltimoAnio.setAutoCreateRowSorter(true);

        panel.add(barra, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaUltimoAnio), BorderLayout.CENTER);

        btnActualizar.addActionListener(e -> cargarJuegosUltimoAnio());

        return panel;
    }

    public void recargar() {
        try {
            List<Juego> juegos = catalogoService.listarCatalogo();
            mapaNombresJuegos = new HashMap<>();
            cmbJuegoRegistro.removeAllItems();
            cmbJuegoHistorial.removeAllItems();
            for (Juego juego : juegos) {
                mapaNombresJuegos.put(juego.getId(), "#" + juego.getId() + " - " + juego.getTitulo());
                cmbJuegoRegistro.addItem(juego);
                cmbJuegoHistorial.addItem(juego);
            }
            actualizarInfoJuegoSeleccionado();
            cargarTodasLasPartidas();
            cargarJuegosUltimoAnio();
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    public void seleccionarJuegoRegistroPorIndice(int indice) {
        cmbJuegoRegistro.setSelectedIndex(indice);
    }

    public Juego getJuegoRegistroSeleccionado() {
        return (Juego) cmbJuegoRegistro.getSelectedItem();
    }

    public String getTextoInfoJuegoSeleccionado() {
        return lblInfoJuegoSeleccionado.getText();
    }

    private void actualizarInfoJuegoSeleccionado() {
        Juego juego = getJuegoRegistroSeleccionado();
        if (juego == null) {
            lblInfoJuegoSeleccionado.setText("Seleccione un juego para consultar sus restricciones de jugadores.");
            return;
        }
        lblInfoJuegoSeleccionado.setText(String.format(
                "Juego: %s | Mín. jugadores: %d | Máx. jugadores: %d | Duración: %s",
                juego.getTitulo(),
                juego.getMinJugadores(),
                juego.getMaxJugadores(),
                juego.getDuracionFormateada()));
    }

    private void actualizarEstadoCamposGanador() {
        boolean sinGanador = chkSinGanador.isSelected();
        txtGanador.setEnabled(!sinGanador);
        cmbTipoGanador.setEnabled(!sinGanador);
        if (sinGanador) {
            txtGanador.setText("");
        }
    }

    private void registrarPartida() {
        Juego juegoSeleccionado = (Juego) cmbJuegoRegistro.getSelectedItem();
        if (juegoSeleccionado == null) {
            mostrarError("Debe seleccionar un juego del catálogo.");
            return;
        }
        int participantes;
        try {
            participantes = Integer.parseInt(txtParticipantes.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarError("El número de participantes debe ser un número entero válido.");
            return;
        }
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(txtFecha.getText().trim());
        } catch (DateTimeParseException ex) {
            mostrarError("La fecha debe tener el formato AAAA-MM-DD (por ejemplo, " + LocalDate.now() + ").");
            return;
        }

        try {
            Partida creada;
            if (chkSinGanador.isSelected()) {
                creada = partidaService.registrarPartidaSinGanador(juegoSeleccionado.getId(), participantes, fecha);
            } else {
                String ganador = txtGanador.getText();
                TipoGanador tipo = (TipoGanador) cmbTipoGanador.getSelectedItem();
                creada = partidaService.registrarPartida(juegoSeleccionado.getId(), participantes, ganador, tipo, fecha);
            }
            txtParticipantes.setText("");
            txtGanador.setText("");
            cargarTodasLasPartidas();
            cargarJuegosUltimoAnio();
            JOptionPane.showMessageDialog(this,
                    "Partida #" + creada.getId() + " registrada correctamente para '" + juegoSeleccionado.getTitulo() + "'.",
                    "Partida registrada",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void consultarHistorialSeleccionado() {
        Juego juego = (Juego) cmbJuegoHistorial.getSelectedItem();
        if (juego == null) {
            mostrarError("Seleccione un juego para consultar su historial.");
            return;
        }
        try {
            List<Partida> historial = partidaService.consultarHistorialPorJuego(juego.getId());
            historialTableModel.setDatos(historial, mapaNombresJuegos);
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void cargarTodasLasPartidas() {
        try {
            List<Partida> partidas = partidaService.listarTodasLasPartidas();
            historialTableModel.setDatos(partidas, mapaNombresJuegos);
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void cargarJuegosUltimoAnio() {
        try {
            List<Juego> jugados = partidaService.consultarJuegosJugadosUltimoAnio();
            ultimoAnioTableModel.setJuegos(jugados);
            lblResumenUltimoAnio.setText("Juegos distintos jugados en el último año: " + jugados.size());
        } catch (DominioException | PersistenciaException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error en partidas", JOptionPane.ERROR_MESSAGE);
    }
}
