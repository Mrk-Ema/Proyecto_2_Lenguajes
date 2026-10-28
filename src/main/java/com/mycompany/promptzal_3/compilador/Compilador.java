package com.mycompany.promptzal_3.compilador;

import com.mycompany.promptzal_3.ast.NodoPrograma;
import com.mycompany.promptzal_3.errores.GestorErrores;
import com.mycompany.promptzal_3.generador.GeneradorPromptzal;
import com.mycompany.promptzal_3.lexico.LectorTokens;
import com.mycompany.promptzal_3.lexico.Token;
import com.mycompany.promptzal_3.semantico.TablaSimbolos;
import com.mycompany.promptzal_3.semantico.ValidadorSemantico;
import com.mycompany.promptzal_3.sintactico.Parser;
import java.util.List;

public final class Compilador {

    public ResultadoCompilacion compilar(String codigo) {
        GestorErrores errores = new GestorErrores();
        String texto = codigo == null ? "" : codigo;

        List<Token> tokens = new LectorTokens().analizar(texto, errores);
        NodoPrograma programa = new Parser(tokens, errores).parsePrograma();
        TablaSimbolos tabla = new ValidadorSemantico(errores).validar(programa);
        String prompt = new GeneradorPromptzal().generar(programa);

        return new ResultadoCompilacion(programa, tabla, errores, prompt);
    }
}
