package com.mycompany.promptzal_3.pruebas;

import com.mycompany.promptzal_3.ast.NodoPrograma;
import com.mycompany.promptzal_3.errores.Error;
import com.mycompany.promptzal_3.errores.GestorErrores;
import com.mycompany.promptzal_3.generador.GeneradorPromptzal;
import com.mycompany.promptzal_3.lexico.LectorTokens;
import com.mycompany.promptzal_3.lexico.Token;
import com.mycompany.promptzal_3.sintactico.Parser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Prueba de consola del generador de prompt.
public final class PruebaGenerador {

    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Uso: PruebaGenerador <archivo.pz> [esperado.md]");
            return;
        }

        String codigo = Files.readString(Path.of(args[0]),
                StandardCharsets.UTF_8);
        GestorErrores errores = new GestorErrores();
        List<Token> tokens = new LectorTokens().analizar(codigo, errores);
        NodoPrograma programa = new Parser(tokens, errores).parsePrograma();
        String prompt = new GeneradorPromptzal().generar(programa);

        System.out.print(prompt);

        if (errores.hayErrores()) {
            System.err.println("Errores de análisis (" + errores.cantidad()
                    + "):");
            for (Error error : errores.getErrores()) {
                System.err.println("  " + error);
            }
        }

        if (args.length == 2) {
            String esperado = Files.readString(Path.of(args[1]),
                    StandardCharsets.UTF_8).replace("\r\n", "\n");
            String actual = prompt.replace("\r\n", "\n");
            if (esperado.equals(actual)) {
                System.err.println("Prompt correcto.");
            } else {
                System.err.println("Prompt diferente.");
                System.exit(1);
            }
        }
    }
}
