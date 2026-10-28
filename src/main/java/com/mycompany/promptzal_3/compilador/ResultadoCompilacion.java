package com.mycompany.promptzal_3.compilador;

import com.mycompany.promptzal_3.ast.NodoPrograma;
import com.mycompany.promptzal_3.errores.GestorErrores;
import com.mycompany.promptzal_3.semantico.TablaSimbolos;

public record ResultadoCompilacion(
        NodoPrograma programa,
        TablaSimbolos tablaSimbolos,
        GestorErrores errores,
        String prompt) {
}
