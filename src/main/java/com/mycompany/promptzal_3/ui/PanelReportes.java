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
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

// Pestaña de reportes.
public class PanelReportes extends JPanel {

    private Runnable alAbrirErrores;
    private Runnable alAbrirSimbolos;
    private Runnable alAbrirAst;

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
                "Errores léxicos, sintácticos y semánticos.", this::abrirErrores));
        lista.add(Box.createVerticalStrut(10));
        lista.add(crearReporte("Tabla de símbolos",
                "Identificadores, tipo, ámbito y línea.", this::abrirSimbolos));
        lista.add(Box.createVerticalStrut(10));
        lista.add(crearReporte("Árbol de análisis sintáctico",
                "Programa y agentes en forma de árbol.", this::abrirAst));

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.add(lista, BorderLayout.NORTH);

        add(titulo, BorderLayout.NORTH);
        add(contenedor, BorderLayout.CENTER);
    }

    public void setAcciones(Runnable errores, Runnable simbolos, Runnable ast) {
        this.alAbrirErrores = errores;
        this.alAbrirSimbolos = simbolos;
        this.alAbrirAst = ast;
    }

    private void abrirErrores() {
        if (alAbrirErrores != null) {
            alAbrirErrores.run();
        }
    }

    private void abrirSimbolos() {
        if (alAbrirSimbolos != null) {
            alAbrirSimbolos.run();
        }
    }

    private void abrirAst() {
        if (alAbrirAst != null) {
            alAbrirAst.run();
        }
    }

    private JPanel crearReporte(String nombre, String descripcion,
            Runnable accion) {
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
        abrir.addActionListener(e -> accion.run());

        tarjeta.add(textos, BorderLayout.CENTER);
        tarjeta.add(abrir, BorderLayout.EAST);
        return tarjeta;
    }
}
