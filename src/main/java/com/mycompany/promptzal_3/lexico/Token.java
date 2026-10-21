package com.mycompany.promptzal_3.lexico;

import java.util.Objects;

// Token con tipo, lexema y posición inicial.
public final class Token {

    private final TipoToken tipo;
    private final String lexema;
    private final int fila;
    private final int columna;

    public Token(TipoToken tipo, String lexema, int fila, int columna) {
        this.tipo = Objects.requireNonNull(tipo, "El tipo de token no puede ser null");
        this.lexema = lexema == null ? "" : lexema;
        this.fila = fila;
        this.columna = columna;
    }

    public TipoToken getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    @Override
    public String toString() {
        return String.format("%-12s %-22s fila=%-3d col=%-3d",
                tipo, "'" + lexema + "'", fila, columna);
    }
}
