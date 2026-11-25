package es.uv.is2.juegos;

import es.uv.is2.juegos.aplicacion.CatalogoService;
import es.uv.is2.juegos.aplicacion.EstadoJuegoService;
import es.uv.is2.juegos.aplicacion.FiltroJuegosService;
import es.uv.is2.juegos.aplicacion.PartidaService;
import es.uv.is2.juegos.aplicacion.ValoracionService;
import es.uv.is2.juegos.dominio.repositorio.JuegoRepositorio;
import es.uv.is2.juegos.dominio.repositorio.PartidaRepositorio;
import es.uv.is2.juegos.persistencia.CsvJuegoRepositorio;
import es.uv.is2.juegos.persistencia.CsvPartidaRepositorio;
import es.uv.is2.juegos.persistencia.InicializadorDatos;
import es.uv.is2.juegos.presentacion.VentanaPrincipal;
import java.nio.file.Path;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicación de escritorio para la gestión de juegos de mesa.
 */
public final class Main {

    public static final Path RUTA_JUEGOS_DEFECTO = Path.of("data", "juegos_de_mesa.csv");
    public static final Path RUTA_PARTIDAS_DEFECTO = Path.of("data", "partidas.csv");

    private Main() {
    }

    public static void main(String[] args) {
        configurarAspectoVisual();

        SwingUtilities.invokeLater(() -> {
            try {
                InicializadorDatos inicializador = new InicializadorDatos(RUTA_JUEGOS_DEFECTO, RUTA_PARTIDAS_DEFECTO);
                inicializador.inicializarSiNoExisten();

                JuegoRepositorio juegoRepositorio = new CsvJuegoRepositorio(RUTA_JUEGOS_DEFECTO);
                PartidaRepositorio partidaRepositorio = new CsvPartidaRepositorio(RUTA_PARTIDAS_DEFECTO);

                CatalogoService catalogoService = new CatalogoService(juegoRepositorio);
                EstadoJuegoService estadoJuegoService = new EstadoJuegoService(juegoRepositorio);
                ValoracionService valoracionService = new ValoracionService(juegoRepositorio);
                FiltroJuegosService filtroJuegosService = new FiltroJuegosService(juegoRepositorio);
                PartidaService partidaService = new PartidaService(juegoRepositorio, partidaRepositorio);

                VentanaPrincipal ventana = new VentanaPrincipal(
                        catalogoService,
                        estadoJuegoService,
                        valoracionService,
                        filtroJuegosService,
                        partidaService
                );
                ventana.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "No se pudo iniciar la aplicación:\n" + ex.getMessage(),
                        "Error al iniciar",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void configurarAspectoVisual() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Se mantiene el Look & Feel por defecto de Swing si el del sistema no está disponible.
        }
    }
}
