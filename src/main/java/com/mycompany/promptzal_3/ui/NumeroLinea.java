package com.mycompany.promptzal_3.ui;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

/**
 * Franja de números de línea que acompaña al editor.
 */
public class NumeroLinea extends JComponent {

    private static final int ANCHO = 56;
    private static final int MARGEN_DERECHO = 10;

    private final JTextPane editor;

    public NumeroLinea(JTextPane editor) {
        this.editor = editor;
        setOpaque(true);
        setBackground(Colores.FONDO_EDITOR);
        setFont(editor.getFont());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fondo.
        g2.setColor(Colores.FONDO_EDITOR);
        g2.fillRect(0, 0, getWidth(), getHeight());

        FontMetrics fm = g2.getFontMetrics(editor.getFont());
        g2.setFont(editor.getFont());
        g2.setColor(Colores.NUMERO_LINEA);

        Element raiz = editor.getDocument().getDefaultRootElement();
        Rectangle clip = g.getClipBounds();
        if (clip == null) {
            clip = new Rectangle(0, 0, getWidth(), getHeight());
        }

        for (int i = 0; i < raiz.getElementCount(); i++) {
            Element linea = raiz.getElement(i);
            try {
                Rectangle2D r = editor.modelToView2D(linea.getStartOffset());
                if (r == null) {
                    continue;
                }
                int y = (int) (r.getY() + r.getHeight() - fm.getDescent());
                if (y < clip.y - r.getHeight() || y > clip.y + clip.height) {
                    continue;
                }
                String numero = String.valueOf(i + 1);
                int x = getWidth() - fm.stringWidth(numero) - MARGEN_DERECHO;
                g2.drawString(numero, x, y);
            } catch (BadLocationException ex) {
                // Si el documento cambia mientras se pinta, se ignora la línea.
            }
        }

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ANCHO, editor.getHeight());
    }
}
