package com.mycompany.promptzal_3.pruebas;

import com.mycompany.promptzal_3.errores.Error;
import com.mycompany.promptzal_3.errores.GestorErrores;
import com.mycompany.promptzal_3.lexico.LectorTokens;
import com.mycompany.promptzal_3.lexico.Token;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Prueba de consola del lexer.
public final class PruebaLexico {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Uso: PruebaLexico <archivo.pz>");
            return;
        }

        String codigo = Files.readString(Path.of(args[0]), StandardCharsets.UTF_8);
        GestorErrores errores = new GestorErrores();
        List<Token> tokens = new LectorTokens().analizar(codigo, errores);

        for (Token token : tokens) {
            System.out.println(token);
        }

        System.out.println();
        System.out.println("Total de tokens: " + tokens.size());
        if (errores.hayErrores()) {
            System.out.println("Errores léxicos (" + errores.cantidad() + "):");
            for (Error error : errores.getErrores()) {
                System.out.println("  " + error);
            }
        } else {
            System.out.println("Sin errores léxicos.");
        }
    }
}
