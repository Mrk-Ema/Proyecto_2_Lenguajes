package com.mycompany.promptzal_3.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * Pestaña donde se mostrará el prompt final generado por el compilador.
 * Por ahora solo presenta el área de texto y un mensaje de espera.
 */
public class PanelPromptzal extends JPanel {

    private final JTextArea area = new JTextArea();
    private final JButton guardar = new JButton("Guardar .md / .txt");

    public PanelPromptzal() {
        super(new BorderLayout(0, 8));
        setBackground(Colores.PANEL);
        setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel titulo = new JLabel("Promptzal generado");
        titulo.setForeground(Colores.TEXTO);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));

        guardar.setFocusable(false);
        guardar.setToolTipText("Se habilita al implementar el generador");
        guardar.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Disponible cuando el compilador genere el prompt.",
                "PromptZal", JOptionPane.INFORMATION_MESSAGE));

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
        area.setText("Aquí aparecerá el prompt final.\n\n"
                + "El compilador todavía no está implementado.");

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(Colores.BORDE));

        add(superior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    /** Muestra un prompt en el área (se usará cuando exista el compilador). */
    public void setPrompt(String texto) {
        area.setText(texto);
        area.setCaretPosition(0);
    }

    public String getPrompt() {
        return area.getText();
    }
}
