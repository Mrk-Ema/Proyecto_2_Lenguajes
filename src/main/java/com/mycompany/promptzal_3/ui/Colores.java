package com.mycompany.promptzal_3.ui;

import com.mycompany.promptzal_3.lexico.Diccionario;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public final class Colores {

    /*
    *Sintaxis
    **/
    
    /** Directivas: @modelo, @rol, @formato. */
    public static final Color DIRECTIVA = new Color(0xC59BFF);

    /** Comandos de IA: PREGUNTAR, GENERAR, RESUMIR, ... */
    public static final Color COMANDO = new Color(0xFFB547);

    /** Palabras reservadas: AGENTE, contexto, variable, EJECUTAR, EXPORTAR. */
    public static final Color RESERVADA = new Color(0x5AA9FF);

    /** Conectores: SOBRE, DESDE, EN, COMO. */
    public static final Color CONECTOR = new Color(0xFF7AB3);

    /** Funciones del lenguaje: CARGAR. */
    public static final Color FUNCION = new Color(0x7CC4FF);

    /** Cadenas de texto entre comillas. */
    public static final Color CADENA = new Color(0x8FE39A);

    /** Números enteros y decimales. */
    public static final Color NUMERO = new Color(0xD7E07A);

    /** Identificadores escritos por el usuario. */
    public static final Color ID = new Color(0xE9EEFF);

    /** Símbolos: { } ( ) , = + y la flecha ->. */
    public static final Color SIMBOLO = new Color(0x9FB0D4);

    /** Comentarios de línea // y de bloque /* ... *&#47;. */
    public static final Color COMENTARIO = new Color(0x66799F);

    /*
    *Interfaz
    **/

    public static final Color FONDO = new Color(0x0B1526);
    public static final Color FONDO_EDITOR = new Color(0x0D1728);
    public static final Color PANEL = new Color(0x101E3A);
    public static final Color BORDE = new Color(0x223A66);
    public static final Color TEXTO = new Color(0xE9EFFF);
    public static final Color TEXTO_SUAVE = new Color(0x9DB0D8);
    public static final Color NUMERO_LINEA = new Color(0x4E648F);
    public static final Color ACENTO = new Color(0x3FDCEF);
    public static final Color SELECCION = new Color(0x2B4A7E);

    /** Tamaño de la fuente del editor. */
    public static final int TAM_FUENTE = 14;

    /** Fuentes*/
    private static final String[] MONO = {
        "JetBrains Mono", "Cascadia Code", "Fira Code",
        "Consolas", "DejaVu Sans Mono", "Monospaced"
    };

    private Colores() {
    }

    /**
     * Traduce una categoría del diccionario a su color.
     */
    public static Color para(Diccionario.Categoria categoria) {
        if (categoria == null) {
            return ID;
        }
        switch (categoria) {
            case DIRECTIVA:
                return DIRECTIVA;
            case COMANDO:
                return COMANDO;
            case RESERVADA:
                return RESERVADA;
            case CONECTOR:
                return CONECTOR;
            case FUNCION:
                return FUNCION;
            default:
                return ID;
        }
    }

    /**
     * Devuelve la primera fuente monoespaciada instalada en el sistema.
     * Si ninguna de la lista existe, usa la fuente lógica Monospaced.
     */
    public static Font fuenteMono(float tamano) {
        Set<String> instaladas = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames()));
        for (String candidata : MONO) {
            for (String instalada : instaladas) {
                if (instalada.equalsIgnoreCase(candidata)) {
                    return new Font(instalada, Font.PLAIN, (int) tamano);
                }
            }
        }
        return new Font(Font.MONOSPACED, Font.PLAIN, (int) tamano);
    }
}
