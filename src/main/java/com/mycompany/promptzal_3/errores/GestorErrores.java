package com.mycompany.promptzal_3.errores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Acumula los errores de una compilación.
public class GestorErrores {

    private final List<Error> errores = new ArrayList<>();

    public void agregar(Error error) {
        errores.add(error);
    }

    public void lexico(String descripcion, int fila, int columna) {
        agregar(new Error(TipoError.LEXICO, descripcion, fila, columna));
    }

    public void lexico(String descripcion, int fila, int columna, String encontrado) {
        agregar(new Error(TipoError.LEXICO, descripcion, fila, columna,
                encontrado, ""));
    }

    public void sintactico(String descripcion, int fila, int columna,
            String encontrado, String esperado) {
        agregar(new Error(TipoError.SINTACTICO, descripcion, fila, columna,
                encontrado, esperado));
    }

    public void semantico(String descripcion, int fila, int columna) {
        agregar(new Error(TipoError.SEMANTICO, descripcion, fila, columna));
    }

    public List<Error> getErrores() {
        return Collections.unmodifiableList(errores);
    }

    public boolean hayErrores() {
        return !errores.isEmpty();
    }

    public int cantidad() {
        return errores.size();
    }

    public void limpiar() {
        errores.clear();
    }
}
