package com.mycompany.promptzal_3.pruebas;

import com.mycompany.promptzal_3.ast.NodoAgente;
import com.mycompany.promptzal_3.ast.NodoComando;
import com.mycompany.promptzal_3.ast.NodoConector;
import com.mycompany.promptzal_3.ast.NodoDirectiva;
import com.mycompany.promptzal_3.ast.NodoEjecucion;
import com.mycompany.promptzal_3.ast.NodoExpresion;
import com.mycompany.promptzal_3.ast.NodoExportacion;
import com.mycompany.promptzal_3.ast.NodoPrograma;
import com.mycompany.promptzal_3.ast.NodoTermino;
import com.mycompany.promptzal_3.ast.NodoVariableDecl;
import com.mycompany.promptzal_3.errores.Error;
import com.mycompany.promptzal_3.errores.GestorErrores;
import com.mycompany.promptzal_3.lexico.LectorTokens;
import com.mycompany.promptzal_3.lexico.Token;
import com.mycompany.promptzal_3.sintactico.Parser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Prueba de consola del parser.
public final class PruebaParser {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Uso: PruebaParser <archivo.pz>");
            return;
        }

        String codigo = Files.readString(Path.of(args[0]), StandardCharsets.UTF_8);
        GestorErrores errores = new GestorErrores();
        List<Token> tokens = new LectorTokens().analizar(codigo, errores);
        NodoPrograma programa = new Parser(tokens, errores).parsePrograma();

        imprimir(programa);
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

    private static void imprimir(NodoPrograma programa) {
        System.out.println("Programa");

        System.out.println("  Directivas:");
        if (programa.directivas().isEmpty()) {
            System.out.println("    (ninguna)");
        }
        for (NodoDirectiva directiva : programa.directivas()) {
            System.out.println("    " + directiva.nombre() + " = \""
                    + directiva.valor() + "\" (línea " + directiva.linea() + ")");
        }

        System.out.println("  Agentes:");
        if (programa.agentes().isEmpty()) {
            System.out.println("    (ninguno)");
        }
        for (NodoAgente agente : programa.agentes()) {
            imprimirAgente(agente);
        }

        System.out.println("  Ejecutar: " + programa.ejecuciones().stream()
                .map(NodoEjecucion::agente).toList());
        System.out.println("  Exportar: " + programa.exportar().stream()
                .map(NodoExportacion::variable).toList());
    }

    private static void imprimirAgente(NodoAgente agente) {
        System.out.println("    Agente " + agente.nombre()
                + " (línea " + agente.linea() + ")");
        System.out.println("      Contexto: \""
                + (agente.contexto() == null ? "" : agente.contexto().texto())
                + "\" (línea "
                + (agente.contexto() == null ? 0 : agente.contexto().linea())
                + ")");

        System.out.println("      Variables:");
        if (agente.variables().isEmpty()) {
            System.out.println("        (ninguna)");
        }
        for (NodoVariableDecl variable : agente.variables()) {
            System.out.println("        " + variable.nombre() + " = "
                    + expresion(variable.valor())
                    + " (línea " + variable.linea() + ")");
        }

        System.out.println("      Comandos:");
        if (agente.comandos().isEmpty()) {
            System.out.println("        (ninguno)");
        }
        for (NodoComando comando : agente.comandos()) {
            imprimirComando(comando);
        }
    }

    private static void imprimirComando(NodoComando comando) {
        System.out.println("        " + comando.tipo()
                + " (línea " + comando.linea() + ")");
        System.out.println("          Argumento: " + expresion(comando.argumento()));
        for (NodoConector conector : comando.conectores()) {
            System.out.println("          Conector " + conector.tipo() + ": "
                    + expresion(conector.argumento())
                    + " (línea " + conector.linea() + ")");
        }
        System.out.println("          Destino: " + comando.destino());
    }

    private static String expresion(NodoExpresion expresion) {
        if (expresion == null || expresion.terminos().isEmpty()) {
            return "<vacía>";
        }

        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < expresion.terminos().size(); i++) {
            if (i > 0) {
                texto.append(" + ");
            }
            texto.append(termino(expresion.terminos().get(i)));
        }
        return texto.toString();
    }

    private static String termino(NodoTermino termino) {
        if (termino == null) {
            return "<vacío>";
        }
        return switch (termino.clase()) {
            case "CADENA" -> "\"" + termino.valor() + "\"";
            case "NUMERO" -> termino.valor()
                    + (termino.unidad() == null ? "" : " " + termino.unidad().palabra());
            case "CARGAR" -> "CARGAR(\""
                    + (termino.cargar() == null ? "" : termino.cargar().archivo())
                    + "\")";
            default -> termino.valor() == null ? "" : termino.valor();
        };
    }
}
