package com.mycompany.promptzal_3.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Ventana principal del IDE de PromptZal
 *
 * Organiza el editor a la izquierda y las pestañas de promptzal, errores y
 * reportes a la derecha. Los botones Abrir y Guardar funcionan, compilar y los
 * reportes muestran un aviso de que la lógica aún no existe
 */
public class VentanaPrincipal extends JFrame {

    /** Programa de ejemplo que se carga al iniciar para ver todos los colores. */
    private static final String TEXTO_DEMO = """
            @modelo "claude-sonnet-4-6"
            @rol "analista de datos"
            @formato "markdown"

            // Agente que prepara el analisis de un archivo de ventas
            /* Los comentarios de bloque
               tambien se colorean */
            AGENTE analista {
              contexto = "Eres un analista de datos experto"
              variable ventas = CARGAR("ventas.csv")
              variable saludo = "Hola " + "mundo"
              PREGUNTAR "Cuales son las 3 tendencias principales?" SOBRE ventas -> tendencias
              RESUMIR tendencias EN 100 palabras -> resumen
            }

            // Segundo agente: muestra el resto de comandos y conectores
            AGENTE demostracion {
              contexto = "Agente de ejemplo para revisar los colores"
              variable datos = CARGAR("datos.csv")
              GENERAR codigo DESDE datos COMO "markdown" -> codigo
              ANALIZAR datos SOBRE "ventas mensuales" -> analisis
              TRADUCIR "hola mundo" EN ingles -> saludoIngles
              CLASIFICAR datos EN "alta, media, baja" -> categoria
              EXTRAER "correos" DESDE datos -> correos
            }

            EJECUTAR analista
            EJECUTAR demostracion
            EXPORTAR resumen, analisis, codigo
            """;

    private final PanelEditor panelEditor = new PanelEditor();
    private final PanelPromptzal panelPromptzal = new PanelPromptzal();
    private final PanelErrores panelErrores = new PanelErrores();
    private final PanelReportes panelReportes = new PanelReportes();
    private final JTabbedPane pestanas = new JTabbedPane();

    private final JLabel etiquetaArchivo = new JLabel("sin título");
    private final JLabel etiquetaPosicion = new JLabel("Línea 1, Col 1");
    private final JLabel etiquetaMensaje = new JLabel("Listo");

    public VentanaPrincipal() {
        super("PromptZal IDE — Proyecto 2");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(980, 620));
        setSize(1280, 800);
        setLocationRelativeTo(null);

        add(crearBarraHerramientas(), BorderLayout.NORTH);
        add(crearCentro(), BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);

        panelEditor.setAlCambiarDocumento(this::actualizarEstado);
        panelEditor.getEditor().addCaretListener(e -> actualizarPosicion());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (confirmarDescartar()) {
                    dispose();
                    System.exit(0);
                }
            }
        });

        panelEditor.setTexto(TEXTO_DEMO, null, false);
        actualizarEstado();
        actualizarPosicion();

        SwingUtilities.invokeLater(
                () -> panelEditor.getEditor().requestFocusInWindow());
    }

    /*
    *Construcción de la interfaz
    **/

    private JToolBar crearBarraHerramientas() {
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);
        barra.setBorder(new EmptyBorder(6, 10, 6, 10));

        barra.add(boton("Abrir", this::abrir));
        barra.add(boton("Guardar", this::guardar));
        barra.addSeparator(new Dimension(16, 0));
        barra.add(boton("Compilar", this::compilar));
        barra.add(boton("Reportes", this::mostrarReportes));
        barra.add(Box.createHorizontalGlue());

        JLabel marca = new JLabel("PromptZal");
        marca.setForeground(Colores.ACENTO);
        marca.setFont(marca.getFont().deriveFont(Font.BOLD, 15f));
        barra.add(marca);

        return barra;
    }

    private JSplitPane crearCentro() {
        pestanas.addTab("Promptzal", panelPromptzal);
        pestanas.addTab("Errores", panelErrores);
        pestanas.addTab("Reportes", panelReportes);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                panelEditor, pestanas);
        split.setResizeWeight(0.62);
        split.setDividerLocation(760);
        split.setContinuousLayout(true);
        split.setBorder(null);
        return split;
    }

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout(12, 0));
        barra.setBackground(Colores.PANEL);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Colores.BORDE),
                new EmptyBorder(6, 12, 6, 12)));

        etiquetaArchivo.setForeground(Colores.TEXTO);
        etiquetaMensaje.setForeground(Colores.TEXTO_SUAVE);
        etiquetaPosicion.setForeground(Colores.TEXTO_SUAVE);

        barra.add(etiquetaArchivo, BorderLayout.WEST);
        barra.add(etiquetaMensaje, BorderLayout.CENTER);
        barra.add(etiquetaPosicion, BorderLayout.EAST);
        return barra;
    }

    private JButton boton(String texto, Runnable accion) {
        JButton b = new JButton(texto);
        b.setFocusable(false);
        b.addActionListener(e -> accion.run());
        return b;
    }

    /*
    *Acciones
    **/
    
    private void abrir() {
        if (!confirmarDescartar()) {
            return;
        }
        if (panelEditor.abrirArchivo(this)) {
            etiquetaMensaje.setText("Archivo abierto");
        }
    }

    private void guardar() {
        if (panelEditor.guardarArchivo(this)) {
            etiquetaMensaje.setText("Guardado");
        }
    }

    private void compilar() {
        etiquetaMensaje.setText(
                "Compilador pendiente: la lógica se implementará próximamente.");
    }

    private void mostrarReportes() {
        pestanas.setSelectedComponent(panelReportes);
        etiquetaMensaje.setText("Reportes pendientes de implementación.");
    }

    private boolean confirmarDescartar() {
        if (!panelEditor.isSucio()) {
            return true;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Hay cambios sin guardar. ¿Descartarlos?",
                "PromptZal", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        return respuesta == JOptionPane.YES_OPTION;
    }


    /*
    *Estado de la ventana
    **/
    
    private void actualizarEstado() {
        String nombre = panelEditor.getNombreArchivo();
        boolean sucio = panelEditor.isSucio();

        setTitle("PromptZal IDE — " + nombre + (sucio ? " *" : ""));
        etiquetaArchivo.setText(nombre + (sucio ? " (sin guardar)" : ""));
        etiquetaMensaje.setText(sucio ? "Cambios sin guardar" : "Listo");
    }

    private void actualizarPosicion() {
        etiquetaPosicion.setText("Línea " + panelEditor.getLineaCursor()
                + ", Col " + panelEditor.getColumnaCursor());
    }
}
