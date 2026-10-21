package com.mycompany.promptzal_3.errores;

// Error con fase, mensaje y ubicación.
public final class Error {

    private final TipoError tipo;
    private final String descripcion;
    private final int fila;
    private final int columna;
    private final String encontrado;
    private final String esperado;

    public Error(TipoError tipo, String descripcion, int fila, int columna) {
        this(tipo, descripcion, fila, columna, "", "");
    }

    public Error(TipoError tipo, String descripcion, int fila, int columna,
            String encontrado, String esperado) {
        this.tipo = tipo;
        this.descripcion = descripcion == null ? "" : descripcion;
        this.fila = fila;
        this.columna = columna;
        this.encontrado = encontrado == null ? "" : encontrado;
        this.esperado = esperado == null ? "" : esperado;
    }

    public TipoError getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public String getEncontrado() {
        return encontrado;
    }

    public String getEsperado() {
        return esperado;
    }

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder();
        texto.append(tipo)
                .append(" (fila ").append(fila)
                .append(", col ").append(columna).append("): ")
                .append(descripcion);
        if (!encontrado.isEmpty()) {
            texto.append(" | encontrado: '").append(encontrado).append("'");
        }
        if (!esperado.isEmpty()) {
            texto.append(" | esperado: '").append(esperado).append("'");
        }
        return texto.toString();
    }
}
