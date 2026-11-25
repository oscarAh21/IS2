package es.uv.is2.juegos.presentacion;

import es.uv.is2.juegos.aplicacion.CatalogoService;
import es.uv.is2.juegos.aplicacion.EstadoJuegoService;
import es.uv.is2.juegos.aplicacion.FiltroJuegosService;
import es.uv.is2.juegos.aplicacion.PartidaService;
import es.uv.is2.juegos.aplicacion.ValoracionService;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

/**
 * Ventana principal de la aplicación de escritorio en Java Swing.
 * Coordina los cinco paneles funcionales y mantiene sincronizados los datos mostrados.
 */
public class VentanaPrincipal extends JFrame {

    private final CatalogoPanel catalogoPanel;
    private final EstadosPanel estadosPanel;
    private final ValoracionesPanel valoracionesPanel;
    private final FiltrosPanel filtrosPanel;
    private final PartidasPanel partidasPanel;

    public VentanaPrincipal(CatalogoService catalogoService,
                            EstadoJuegoService estadoJuegoService,
                            ValoracionService valoracionService,
                            FiltroJuegosService filtroJuegosService,
                            PartidaService partidaService) {
        super("Gestión de Juegos de Mesa");

        Runnable refrescarTodo = this::recargarTodosLosPaneles;

        this.catalogoPanel = new CatalogoPanel(catalogoService);
        this.estadosPanel = new EstadosPanel(catalogoService, estadoJuegoService, refrescarTodo);
        this.valoracionesPanel = new ValoracionesPanel(catalogoService, valoracionService, refrescarTodo);
        this.filtrosPanel = new FiltrosPanel(catalogoService, filtroJuegosService);
        this.partidasPanel = new PartidasPanel(catalogoService, partidaService);

        configurarVentana();
        recargarTodosLosPaneles();
    }

    private void configurarVentana() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 640));
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setJMenuBar(crearMenuBar());

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Catálogo", catalogoPanel);
        pestanas.addTab("Filtros", filtrosPanel);
        pestanas.addTab("Estados", estadosPanel);
        pestanas.addTab("Valoraciones", valoracionesPanel);
        pestanas.addTab("Partidas e Historial", partidasPanel);

        add(pestanas, BorderLayout.CENTER);
    }

    private JMenuBar crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemRecargar = new JMenuItem("Recargar datos desde CSV");
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemRecargar.addActionListener(e -> recargarTodosLosPaneles());
        itemSalir.addActionListener(e -> dispose());
        menuArchivo.add(itemRecargar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemAcerca = new JMenuItem("Acerca de");
        itemAcerca.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Gestión de Juegos de Mesa v1.0.0\n"
                        + "Aplicación de escritorio Java 21 + Swing con persistencia CSV.\n"
                        + "Ingeniería del Software II - Arquitectura por capas.",
                "Acerca de",
                JOptionPane.INFORMATION_MESSAGE));
        menuAyuda.add(itemAcerca);

        menuBar.add(menuArchivo);
        menuBar.add(menuAyuda);
        return menuBar;
    }

    public final void recargarTodosLosPaneles() {
        catalogoPanel.recargar();
        filtrosPanel.recargar();
        estadosPanel.recargar();
        valoracionesPanel.recargar();
        partidasPanel.recargar();
    }
}
