package com.mycompany.promptzal_3.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Pestaña de reportes del compilador. Presenta las tres salidas exigidas por
 * el proyecto; cada botón avisa que el reporte se habilitará cuando exista la
 * compilación.
 */
public class PanelReportes extends JPanel {

    public PanelReportes() {
        super(new BorderLayout(0, 12));
        setBackground(Colores.PANEL);
        setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel titulo = new JLabel("Reportes");
        titulo.setForeground(Colores.TEXTO);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));

        JPanel lista = new JPanel();
        lista.setOpaque(false);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.add(crearReporte("Reporte de errores",
                "Errores léxicos, sintácticos y semánticos."));
        lista.add(Box.createVerticalStrut(10));
        lista.add(crearReporte("Tabla de símbolos",
                "Identificadores, tipo, valor y ámbito."));
        lista.add(Box.createVerticalStrut(10));
        lista.add(crearReporte("Árbol de análisis sintáctico",
                "Programa y agentes en forma de árbol."));

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.add(lista, BorderLayout.NORTH);

        add(titulo, BorderLayout.NORTH);
        add(contenedor, BorderLayout.CENTER);
    }

    private JPanel crearReporte(String nombre, String descripcion) {
        JPanel tarjeta = new JPanel(new BorderLayout(12, 0));
        tarjeta.setBackground(Colores.FONDO_EDITOR);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colores.BORDE),
                new EmptyBorder(12, 14, 12, 14)));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        tarjeta.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titulo = new JLabel(nombre);
        titulo.setForeground(Colores.TEXTO);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 13f));

        JLabel detalle = new JLabel(descripcion);
        detalle.setForeground(Colores.TEXTO_SUAVE);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 3));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(detalle);

        JButton abrir = new JButton("Abrir");
        abrir.setFocusable(false);
        abrir.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "El reporte se habilitará cuando el compilador esté implementado.",
                nombre, JOptionPane.INFORMATION_MESSAGE));

        tarjeta.add(textos, BorderLayout.CENTER);
        tarjeta.add(abrir, BorderLayout.EAST);
        return tarjeta;
    }
}
