package com.mycompany.promptzal_3.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

/**
 * Panel del editor de código.
 *
 * <p>Contiene el JTextPane con resaltado, la franja de números de línea, las
 * operaciones de abrir/guardar y el control de cambios sin guardar.</p>
 *
 * <p>El resaltado se aplica con un temporizador corto para no repintar en cada
 * tecla. La compilación no participa en absoluto: escribir y colorear funciona
 * siempre.</p>
 */
public class PanelEditor extends JPanel {

    private final JTextPane editor = new JTextPane() {
        @Override
        public boolean getScrollableTracksViewportWidth() {
            Component padre = getParent();
            if (padre == null) {
                return true;
            }
            // Si el texto es más ancho que la vista, aparece scroll horizontal
            // en lugar de ajustar las líneas.
            return getUI().getPreferredSize(this).width <= padre.getSize().width;
        }
    };

    private final NumeroLinea numeros;
    private final JScrollPane scroll;
    private final Timer temporizador;

    private File archivo;
    private boolean sucio;
    private Runnable alCambiarDocumento;

    public PanelEditor() {
        super(new BorderLayout());
        setBackground(Colores.FONDO);

        editor.setFont(Colores.fuenteMono(Colores.TAM_FUENTE));
        editor.setBackground(Colores.FONDO_EDITOR);
        editor.setForeground(Colores.ID);
        editor.setCaretColor(Colores.ACENTO);
        editor.setSelectionColor(Colores.SELECCION);
        editor.setSelectedTextColor(Colores.TEXTO);
        editor.setMargin(new Insets(10, 12, 10, 12));

        numeros = new NumeroLinea(editor);

        scroll = new JScrollPane(editor);
        scroll.setBorder(null);
        scroll.setRowHeaderView(numeros);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        temporizador = new Timer(120, e -> {
            ResaltadorSintaxis.aplicar(editor);
            numeros.repaint();
        });
        temporizador.setRepeats(false);

        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                cambio();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                cambio();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Solo cambian atributos (colores): no marca el archivo como sucio.
            }
        });

        editor.addCaretListener(e -> numeros.repaint());
        scroll.getVerticalScrollBar().addAdjustmentListener(e -> numeros.repaint());

        add(scroll, BorderLayout.CENTER);
    }

    private void cambio() {
        sucio = true;
        temporizador.restart();
        numeros.repaint();
        if (alCambiarDocumento != null) {
            alCambiarDocumento.run();
        }
    }

    // =====================================================================
    //  Operaciones de archivo
    // =====================================================================

    /**
     * Pide un archivo y lo carga en el editor.
     *
     * @param padre componente para centrar el diálogo
     * @return true si se abrió un archivo
     */
    public boolean abrirArchivo(Component padre) {
        JFileChooser selector = crearSelector(false);
        selector.setDialogTitle("Abrir programa PromptZal");
        if (selector.showOpenDialog(padre) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        File seleccionado = selector.getSelectedFile();
        try {
            String contenido = Files.readString(seleccionado.toPath(),
                    StandardCharsets.UTF_8);
            setTexto(contenido, seleccionado, false);
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(padre,
                    "No se pudo abrir el archivo:\n" + ex.getMessage(),
                    "Error al abrir", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Guarda el contenido actual. Si el archivo no tiene ruta todavía, muestra
     * un selector limitado a .pz y corrige la extensión si hace falta.
     *
     * @param padre componente para centrar el diálogo
     * @return true si se guardó correctamente
     */
    public boolean guardarArchivo(Component padre) {
        File destino = archivo;

        if (destino == null) {
            JFileChooser selector = crearSelector(true);
            selector.setDialogTitle("Guardar programa PromptZal");
            if (selector.showSaveDialog(padre) != JFileChooser.APPROVE_OPTION) {
                return false;
            }
            destino = asegurarExtensionPz(selector.getSelectedFile());
            if (destino.exists()) {
                int respuesta = JOptionPane.showConfirmDialog(padre,
                        "El archivo ya existe. ¿Sobrescribirlo?",
                        "Confirmar", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (respuesta != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
        }

        try {
            Files.writeString(destino.toPath(), getTexto(), StandardCharsets.UTF_8);
            archivo = destino;
            sucio = false;
            if (alCambiarDocumento != null) {
                alCambiarDocumento.run();
            }
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(padre,
                    "No se pudo guardar el archivo:\n" + ex.getMessage(),
                    "Error al guardar", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private JFileChooser crearSelector(boolean soloPz) {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter(
                "Programas PromptZal (*.pz)", "pz"));
        selector.setAcceptAllFileFilterUsed(!soloPz);
        return selector;
    }

    /**
     * Fuerza la extensión .pz en el archivo elegido. Si el usuario escribió
     * otra extensión, se reemplaza (por ejemplo, programa.txt -> programa.pz).
     */
    private static File asegurarExtensionPz(File archivo) {
        String nombre = archivo.getName();
        if (nombre.toLowerCase().endsWith(".pz")) {
            return archivo;
        }
        int punto = nombre.lastIndexOf('.');
        String base = punto > 0 ? nombre.substring(0, punto) : nombre;
        if (base.isBlank()) {
            base = "programa";
        }
        return new File(archivo.getParentFile(), base + ".pz");
    }

    /**
     * Reemplaza todo el contenido del editor.
     *
     * @param texto contenido nuevo
     * @param archivo archivo asociado, o null si no tiene ruta
     * @param marcarSucio true si debe quedar marcado como cambio sin guardar
     */
    public void setTexto(String texto, File archivo, boolean marcarSucio) {
        try {
            editor.getDocument().remove(0, editor.getDocument().getLength());
            editor.getDocument().insertString(0, texto, null);
            editor.setCaretPosition(0);
        } catch (BadLocationException ex) {
            // No debería ocurrir: los offsets son válidos.
        }
        this.archivo = archivo;
        this.sucio = marcarSucio;
        ResaltadorSintaxis.aplicar(editor);
        numeros.repaint();
        if (alCambiarDocumento != null) {
            alCambiarDocumento.run();
        }
    }

    // =====================================================================
    //  Consultas y utilidades
    // =====================================================================

    public JTextPane getEditor() {
        return editor;
    }

    public String getTexto() {
        return editor.getText();
    }

    public File getArchivo() {
        return archivo;
    }

    public String getNombreArchivo() {
        return archivo == null ? "sin título" : archivo.getName();
    }

    public boolean isSucio() {
        return sucio;
    }

    public void setAlCambiarDocumento(Runnable alCambiarDocumento) {
        this.alCambiarDocumento = alCambiarDocumento;
    }

    /** @return número de línea (1-based) donde está el cursor */
    public int getLineaCursor() {
        int posicion = editor.getCaretPosition();
        Element raiz = editor.getDocument().getDefaultRootElement();
        return raiz.getElementIndex(posicion) + 1;
    }

    /** @return columna (1-based) donde está el cursor */
    public int getColumnaCursor() {
        int posicion = editor.getCaretPosition();
        Element raiz = editor.getDocument().getDefaultRootElement();
        Element linea = raiz.getElement(raiz.getElementIndex(posicion));
        return posicion - linea.getStartOffset() + 1;
    }
}
