package com.mycompany.promptzal_3.ui;

import com.mycompany.promptzal_3.lexico.Diccionario;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Resalta la sintaxis del editor con un solo recorrido del texto.
 *
 * <p>Se reconocen, en este orden de prioridad: comentarios, cadenas,
 * directivas, números, la flecha {@code ->}, símbolos y palabras. Las palabras
 * se clasifican consultando el {@link Diccionario}.</p>
 *
 * <p>No depende de la compilación: se ejecuta cada vez que el documento
 * cambia (con un pequeño retardo desde {@link PanelEditor}).</p>
 */
public final class ResaltadorSintaxis {

    private ResaltadorSintaxis() {
        // clase de utilidades: no se instancia
    }

    /**
     * Aplica los colores a todo el contenido del editor.
     *
     * @param editor panel de texto a pintar
     */
    public static void aplicar(JTextPane editor) {
        StyledDocument doc = editor.getStyledDocument();
        String texto;
        try {
            texto = doc.getText(0, doc.getLength());
        } catch (BadLocationException ex) {
            return;
        }

        // Color base: todo el texto empieza como identificador.
        doc.setCharacterAttributes(0, texto.length(), attrs(Colores.ID), true);

        int i = 0;
        int n = texto.length();
        while (i < n) {
            char c = texto.charAt(i);

            // Comentario de línea: // hasta el final de la línea.
            if (c == '/' && i + 1 < n && texto.charAt(i + 1) == '/') {
                int fin = texto.indexOf('\n', i);
                if (fin < 0) {
                    fin = n;
                }
                pintar(doc, i, fin - i, Colores.COMENTARIO);
                i = fin;
                continue;
            }

            // Comentario de bloque: /* ... */ (si no se cierra, llega al final).
            if (c == '/' && i + 1 < n && texto.charAt(i + 1) == '*') {
                int fin = texto.indexOf("*/", i + 2);
                fin = (fin < 0) ? n : fin + 2;
                pintar(doc, i, fin - i, Colores.COMENTARIO);
                i = fin;
                continue;
            }

            // Cadena: "..." sin pasar de la línea.
            if (c == '"') {
                int fin = i + 1;
                while (fin < n && texto.charAt(fin) != '"' && texto.charAt(fin) != '\n') {
                    fin++;
                }
                if (fin < n && texto.charAt(fin) == '"') {
                    fin++;
                }
                pintar(doc, i, fin - i, Colores.CADENA);
                i = fin;
                continue;
            }

            // Directiva: @palabra.
            if (c == '@') {
                int fin = i + 1;
                while (fin < n && esLetraONumero(texto.charAt(fin))) {
                    fin++;
                }
                String palabra = texto.substring(i, fin);
                Color color = Diccionario.DIRECTIVAS.contains(palabra)
                        ? Colores.DIRECTIVA
                        : Colores.ID;
                pintar(doc, i, fin - i, color);
                i = fin;
                continue;
            }

            // Número: dígitos con punto decimal opcional.
            if (Character.isDigit(c)) {
                int fin = i + 1;
                while (fin < n && (Character.isDigit(texto.charAt(fin))
                        || texto.charAt(fin) == '.')) {
                    fin++;
                }
                pintar(doc, i, fin - i, Colores.NUMERO);
                i = fin;
                continue;
            }

            // Flecha de asignación ->.
            if (c == '-' && i + 1 < n && texto.charAt(i + 1) == '>') {
                pintar(doc, i, 2, Colores.SIMBOLO);
                i += 2;
                continue;
            }

            // Símbolos sueltos.
            if ("{}()[],=+".indexOf(c) >= 0) {
                pintar(doc, i, 1, Colores.SIMBOLO);
                i++;
                continue;
            }

            // Palabra: identificador, reservada, comando, conector o función.
            if (esInicioPalabra(c)) {
                int fin = i + 1;
                while (fin < n && esLetraONumero(texto.charAt(fin))) {
                    fin++;
                }
                String palabra = texto.substring(i, fin);
                pintar(doc, i, fin - i,
                        Colores.para(Diccionario.categoriaDe(palabra)));
                i = fin;
                continue;
            }

            // Espacios, saltos de línea y cualquier otro carácter: color base.
            i++;
        }
    }

    private static boolean esInicioPalabra(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private static boolean esLetraONumero(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private static void pintar(StyledDocument doc, int inicio, int largo, Color color) {
        if (largo > 0) {
            doc.setCharacterAttributes(inicio, largo, attrs(color), true);
        }
    }

    private static SimpleAttributeSet attrs(Color color) {
        SimpleAttributeSet atributos = new SimpleAttributeSet();
        StyleConstants.setForeground(atributos, color);
        return atributos;
    }
}
