package com.mycompany.promptzal_3.pruebas;

import com.mycompany.promptzal_3.ast.NodoPrograma;
import com.mycompany.promptzal_3.errores.Error;
import com.mycompany.promptzal_3.errores.GestorErrores;
import com.mycompany.promptzal_3.lexico.LectorTokens;
import com.mycompany.promptzal_3.lexico.Token;
import com.mycompany.promptzal_3.semantico.Simbolo;
import com.mycompany.promptzal_3.semantico.TablaSimbolos;
import com.mycompany.promptzal_3.semantico.ValidadorSemantico;
import com.mycompany.promptzal_3.sintactico.Parser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Prueba de consola del análisis semántico.
public final class PruebaSemantica {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Uso: PruebaSemantica <archivo.pz>");
            return;
        }

        String codigo = Files.readString(Path.of(args[0]),
                StandardCharsets.UTF_8);
        GestorErrores errores = new GestorErrores();
        List<Token> tokens = new LectorTokens().analizar(codigo, errores);
        NodoPrograma programa = new Parser(tokens, errores).parsePrograma();
        TablaSimbolos tabla = new ValidadorSemantico(errores).validar(programa);

        imprimirTabla(tabla);
        System.out.println();

        if (errores.hayErrores()) {
            System.out.println("Errores (" + errores.cantidad() + "):");
            for (Error error : errores.getErrores()) {
                System.out.println("  " + error);
            }
        } else {
            System.out.println("Sin errores.");
        }
    }

    private static void imprimirTabla(TablaSimbolos tabla) {
        System.out.println("Tabla de símbolos:");
        if (tabla.getSimbolos().isEmpty()) {
            System.out.println("  (vacía)");
            return;
        }

        for (Simbolo simbolo : tabla.getSimbolos()) {
            System.out.printf("  %-20s %-22s ámbito=%-12s línea=%d%n",
                    simbolo.tipo(), simbolo.nombre(), simbolo.ambito(),
                    simbolo.linea());
        }
    }
}
