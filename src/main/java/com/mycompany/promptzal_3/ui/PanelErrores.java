package com.mycompany.promptzal_3.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/** Pestaña de errores. */
public class PanelErrores extends JPanel {

    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JLabel subtitulo;

    public PanelErrores() {
        super(new BorderLayout(0, 8));
        setBackground(Colores.PANEL);
        setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel titulo = new JLabel("Errores de la última compilación");
        titulo.setForeground(Colores.TEXTO);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));

        subtitulo = new JLabel("Sin errores registrados.");
        subtitulo.setForeground(Colores.TEXTO_SUAVE);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(titulo, BorderLayout.NORTH);
        cabecera.add(subtitulo, BorderLayout.SOUTH);

        modelo = new DefaultTableModel(
                new Object[]{"Tipo", "Descripción", "Fila", "Columna",
                             "Encontrado", "Esperado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.setFillsViewportHeight(true);
        tabla.setBackground(Colores.FONDO_EDITOR);
        tabla.setForeground(Colores.TEXTO);
        tabla.setGridColor(Colores.BORDE);
        tabla.getTableHeader().setBackground(Colores.PANEL);
        tabla.getTableHeader().setForeground(Colores.TEXTO_SUAVE);

        int[] anchos = {60, 130, 40, 60, 80, 80};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        scroll.getViewport().setBackground(Colores.FONDO_EDITOR);

        add(cabecera, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    // Limpia la tabla.
    public void limpiar() {
        modelo.setRowCount(0);
    }

    // Cambia el resumen.
    public void setSubtitulo(String texto) {
        subtitulo.setText(texto);
    }

    // Agrega una fila de error.
    public void agregar(String tipo, String descripcion, int fila, int columna,
            String encontrado, String esperado) {
        modelo.addRow(new Object[]{tipo, descripcion, fila, columna,
            encontrado, esperado});
    }

    public int cantidadErrores() {
        return modelo.getRowCount();
    }
}
