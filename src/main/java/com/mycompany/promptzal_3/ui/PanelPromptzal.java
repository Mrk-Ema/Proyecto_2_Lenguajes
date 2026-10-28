package com.mycompany.promptzal_3.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

// Pestaña donde se muestra y guarda el prompt generado.
public class PanelPromptzal extends JPanel {

    private static final String TEXTO_INICIAL
            = "Compila un programa .pz para generar el prompt.";

    private final JTextArea area = new JTextArea();
    private final JButton guardar = new JButton("Guardar .md / .txt");
    private File archivoPrompt;

    public PanelPromptzal() {
        super(new BorderLayout(0, 8));
        setBackground(Colores.PANEL);
        setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel titulo = new JLabel("Promptzal generado");
        titulo.setForeground(Colores.TEXTO);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));

        guardar.setFocusable(false);
        guardar.setEnabled(false);
        guardar.setToolTipText("Guardar el prompt como .md o .txt");
        guardar.addActionListener(e -> guardarPrompt());

        JPanel superior = new JPanel(new BorderLayout());
        superior.setOpaque(false);
        superior.add(titulo, BorderLayout.WEST);
        superior.add(guardar, BorderLayout.EAST);

        area.setEditable(false);
        area.setFont(Colores.fuenteMono(13f));
        area.setBackground(Colores.FONDO_EDITOR);
        area.setForeground(Colores.TEXTO);
        area.setCaretColor(Colores.ACENTO);
        area.setSelectionColor(Colores.SELECCION);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new Insets(10, 12, 10, 12));
        area.setText(TEXTO_INICIAL);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(Colores.BORDE));

        add(superior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    // Muestra un prompt en el área.
    public void setPrompt(String texto) {
        String contenido = texto == null ? "" : texto;
        area.setText(contenido);
        area.setCaretPosition(0);
        guardar.setEnabled(!contenido.isBlank());
    }

    public String getPrompt() {
        return area.getText();
    }

    private void guardarPrompt() {
        String prompt = getPrompt();
        if (prompt.isBlank()) {
            return;
        }

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar promptzal");
        selector.setFileFilter(new FileNameExtensionFilter(
                "Promptzal (*.md, *.txt)", "md", "txt"));
        selector.setAcceptAllFileFilterUsed(false);
        if (archivoPrompt != null) {
            selector.setSelectedFile(archivoPrompt);
        } else {
            selector.setSelectedFile(new File("promptzal.md"));
        }

        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File destino = asegurarExtension(selector.getSelectedFile());
        if (destino.exists()) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "El archivo ya existe. ¿Sobrescribirlo?",
                    "Confirmar", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            Files.writeString(destino.toPath(), prompt, StandardCharsets.UTF_8);
            archivoPrompt = destino;
            JOptionPane.showMessageDialog(this,
                    "Prompt guardado en:\n" + destino.getAbsolutePath(),
                    "PromptZal", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el prompt:\n" + ex.getMessage(),
                    "Error al guardar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static File asegurarExtension(File archivo) {
        String nombre = archivo.getName().toLowerCase();
        if (nombre.endsWith(".md") || nombre.endsWith(".txt")) {
            return archivo;
        }

        String original = archivo.getName();
        int punto = original.lastIndexOf('.');
        String base = punto > 0 ? original.substring(0, punto) : original;
        if (base.isBlank()) {
            base = "promptzal";
        }
        return new File(archivo.getParentFile(), base + ".md");
    }
}
