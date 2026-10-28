package com.mycompany.promptzal_3.reportes;

import com.mycompany.promptzal_3.ast.NodoAgente;
import com.mycompany.promptzal_3.ast.NodoCargar;
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
import com.mycompany.promptzal_3.errores.TipoError;
import com.mycompany.promptzal_3.semantico.Simbolo;
import com.mycompany.promptzal_3.semantico.TablaSimbolos;
import com.mycompany.promptzal_3.semantico.TipoSimbolo;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class GeneradorReportes {

    public static final Path CARPETA_SALIDAS = Path.of("salidas");

    private static final String CSS = """
            :root { color-scheme: dark; }
            * { box-sizing: border-box; }
            body {
                margin: 0;
                background: #0b1526;
                color: #e9efff;
                font-family: "Segoe UI", "Noto Sans", Arial, sans-serif;
            }
            header {
                padding: 28px 32px 22px;
                background: linear-gradient(135deg, #101e3a, #0b1526);
                border-bottom: 1px solid #223a66;
            }
            h1 { margin: 0; font-size: 26px; letter-spacing: .2px; }
            .lead { margin-top: 7px; color: #9db0d8; }
            main { padding: 24px 32px 44px; }
            .resumen { margin: 0 0 16px; color: #9db0d8; }
            .ok {
                padding: 16px 18px;
                border: 1px solid #2b6b4a;
                background: #0d2a1f;
                border-radius: 8px;
                color: #8fe39a;
                font-weight: 600;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                border: 1px solid #223a66;
                border-radius: 8px;
                overflow: hidden;
            }
            th, td {
                padding: 10px 12px;
                text-align: left;
                vertical-align: top;
                border-bottom: 1px solid #223a66;
            }
            th { background: #101e3a; color: #9db0d8; font-size: 13px; }
            tr:nth-child(even) td { background: #0d1728; }
            tr:last-child td { border-bottom: 0; }
            .tipo { white-space: nowrap; font-weight: 600; }
            .tipo-lexico { color: #ffb547; }
            .tipo-sintactico { color: #ff7ab3; }
            .tipo-semantico { color: #3fdcef; }
            .vacio { color: #66799f; }
            pre {
                margin: 0;
                padding: 18px;
                border: 1px solid #223a66;
                border-radius: 8px;
                background: #0d1728;
                color: #e9efff;
                overflow: auto;
                line-height: 1.45;
                font-family: "JetBrains Mono", "Cascadia Code", Consolas,
                        "DejaVu Sans Mono", monospace;
                font-size: 13px;
            }
            footer {
                padding: 14px 32px;
                border-top: 1px solid #223a66;
                color: #66799f;
                font-size: 12px;
            }
            """;

    public Path errores(GestorErrores gestor) throws IOException {
        return errores(gestor == null ? List.of() : gestor.getErrores(),
                CARPETA_SALIDAS.resolve("reporte_errores.html"));
    }

    public Path errores(List<Error> errores, Path ruta) throws IOException {
        List<Error> lista = errores == null ? List.of() : errores;
        StringBuilder html = new StringBuilder();
        inicio(html, "Reporte de errores",
                "Errores léxicos, sintácticos y semánticos de la última compilación.");

        int lexicos = contar(lista, TipoError.LEXICO);
        int sintacticos = contar(lista, TipoError.SINTACTICO);
        int semanticos = contar(lista, TipoError.SEMANTICO);

        if (lista.isEmpty()) {
            html.append("<div class=\"ok\">0 errores: el programa es válido.</div>\n");
        } else {
            html.append("<div class=\"resumen\">")
                    .append(lista.size()).append(" error(es): ")
                    .append(lexicos).append(" léxicos · ")
                    .append(sintacticos).append(" sintácticos · ")
                    .append(semanticos).append(" semánticos.</div>\n");
            html.append("<table>\n<thead><tr>")
                    .append("<th>Tipo</th><th>Descripción</th><th>Fila</th>")
                    .append("<th>Columna</th><th>Encontrado</th><th>Esperado</th>")
                    .append("</tr></thead>\n<tbody>\n");
            for (Error error : lista) {
                html.append("<tr><td class=\"tipo ")
                        .append(claseTipo(error.getTipo())).append("\">")
                        .append(nombreTipo(error.getTipo()))
                        .append("</td><td>").append(escape(error.getDescripcion()))
                        .append("</td><td>").append(error.getFila())
                        .append("</td><td>").append(error.getColumna())
                        .append("</td><td>").append(mostrar(error.getEncontrado()))
                        .append("</td><td>").append(mostrar(error.getEsperado()))
                        .append("</td></tr>\n");
            }
            html.append("</tbody>\n</table>\n");
        }

        fin(html);
        return escribir(ruta, html.toString());
    }

    public Path simbolos(TablaSimbolos tabla) throws IOException {
        return simbolos(tabla, CARPETA_SALIDAS.resolve("reporte_simbolos.html"));
    }

    public Path simbolos(TablaSimbolos tabla, Path ruta) throws IOException {
        List<Simbolo> simbolos = tabla == null
                ? new ArrayList<>() : new ArrayList<>(tabla.getSimbolos());
        simbolos.sort(Comparator.comparing(Simbolo::ambito)
                .thenComparingInt(Simbolo::linea));

        StringBuilder html = new StringBuilder();
        inicio(html, "Tabla de símbolos",
                "Agentes, variables y resultados registrados durante la compilación.");

        long agentes = simbolos.stream()
                .filter(s -> s.tipo() == TipoSimbolo.AGENTE).count();
        long variables = simbolos.stream()
                .filter(s -> s.tipo() == TipoSimbolo.VARIABLE_DECLARADA).count();
        long resultados = simbolos.stream()
                .filter(s -> s.tipo() == TipoSimbolo.RESULTADO_COMANDO).count();

        html.append("<div class=\"resumen\">")
                .append(agentes).append(" agentes · ")
                .append(variables).append(" variables · ")
                .append(resultados).append(" resultados de comando.</div>\n");

        if (simbolos.isEmpty()) {
            html.append("<div class=\"ok\">No hay símbolos registrados.</div>\n");
        } else {
            html.append("<table>\n<thead><tr>")
                    .append("<th>Nombre</th><th>Tipo</th><th>Ámbito</th>")
                    .append("<th>Línea</th></tr></thead>\n<tbody>\n");
            for (Simbolo simbolo : simbolos) {
                html.append("<tr><td>").append(escape(simbolo.nombre()))
                        .append("</td><td>").append(nombreTipo(simbolo.tipo()))
                        .append("</td><td>").append(escape(simbolo.ambito()))
                        .append("</td><td>").append(simbolo.linea())
                        .append("</td></tr>\n");
            }
            html.append("</tbody>\n</table>\n");
        }

        fin(html);
        return escribir(ruta, html.toString());
    }

    public Path ast(NodoPrograma programa) throws IOException {
        return ast(programa, CARPETA_SALIDAS.resolve("reporte_ast.html"));
    }

    public Path ast(NodoPrograma programa, Path ruta) throws IOException {
        StringBuilder html = new StringBuilder();
        inicio(html, "Árbol de análisis sintáctico",
                "Representación legible del AST construido por el parser.");

        if (programa == null) {
            html.append("<div class=\"ok\">No hay AST para mostrar.</div>\n");
        } else {
            html.append("<pre>").append(escape(arbolTexto(programa)))
                    .append("</pre>\n");
        }

        fin(html);
        return escribir(ruta, html.toString());
    }

    private String arbolTexto(NodoPrograma programa) {
        StringBuilder texto = new StringBuilder("Programa\n");
        texto.append("  Directivas\n");
        if (programa.directivas().isEmpty()) {
            texto.append("    (ninguna)\n");
        } else {
            for (NodoDirectiva directiva : programa.directivas()) {
                texto.append("    ").append(directiva.nombre()).append(" = \"")
                        .append(directiva.valor()).append("\" (línea ")
                        .append(directiva.linea()).append(")\n");
            }
        }

        texto.append("  Agentes\n");
        if (programa.agentes().isEmpty()) {
            texto.append("    (ninguno)\n");
        } else {
            for (NodoAgente agente : programa.agentes()) {
                arbolAgente(texto, agente);
            }
        }

        texto.append("  Ejecutar\n");
        if (programa.ejecuciones().isEmpty()) {
            texto.append("    (ninguno)\n");
        } else {
            for (NodoEjecucion ejecucion : programa.ejecuciones()) {
                texto.append("    ").append(ejecucion.agente())
                        .append(" (línea ").append(ejecucion.linea())
                        .append(")\n");
            }
        }

        texto.append("  Exportar\n");
        if (programa.exportar().isEmpty()) {
            texto.append("    (ninguno)\n");
        } else {
            for (NodoExportacion exportacion : programa.exportar()) {
                texto.append("    ").append(exportacion.variable())
                        .append(" (línea ").append(exportacion.linea())
                        .append(")\n");
            }
        }
        return texto.toString();
    }

    private void arbolAgente(StringBuilder texto, NodoAgente agente) {
        texto.append("    Agente ").append(agente.nombre())
                .append(" (línea ").append(agente.linea()).append(")\n");
        String contexto = agente.contexto() == null
                ? "" : agente.contexto().texto();
        texto.append("      Contexto: \"").append(contexto).append("\" (línea ")
                .append(agente.contexto() == null ? 0 : agente.contexto().linea())
                .append(")\n");

        texto.append("      Variables\n");
        if (agente.variables().isEmpty()) {
            texto.append("        (ninguna)\n");
        } else {
            for (NodoVariableDecl variable : agente.variables()) {
                texto.append("        variable ").append(variable.nombre())
                        .append(" = ").append(expresion(variable.valor()))
                        .append(" (línea ").append(variable.linea()).append(")\n");
            }
        }

        texto.append("      Comandos\n");
        if (agente.comandos().isEmpty()) {
            texto.append("        (ninguno)\n");
        } else {
            for (NodoComando comando : agente.comandos()) {
                texto.append("        ").append(comando.tipo())
                        .append(" (línea ").append(comando.linea()).append(")\n");
                texto.append("          Argumento: ")
                        .append(expresion(comando.argumento())).append('\n');
                for (NodoConector conector : comando.conectores()) {
                    texto.append("          Conector ").append(conector.tipo())
                            .append(": ").append(expresion(conector.argumento()))
                            .append(" (línea ").append(conector.linea())
                            .append(")\n");
                }
                texto.append("          Destino: ").append(comando.destino())
                        .append('\n');
            }
        }
    }

    private String expresion(NodoExpresion expresion) {
        if (expresion == null || expresion.terminos().isEmpty()) {
            return "<vacía>";
        }

        StringBuilder texto = new StringBuilder();
        for (NodoTermino termino : expresion.terminos()) {
            if (texto.length() > 0) {
                texto.append(" + ");
            }
            texto.append(termino(termino));
        }
        return texto.toString();
    }

    private String termino(NodoTermino termino) {
        if (termino == null) {
            return "<vacío>";
        }
        return switch (termino.clase()) {
            case "CADENA" -> "\"" + termino.valor() + "\"";
            case "NUMERO" -> termino.valor()
                    + (termino.unidad() == null ? ""
                            : " " + termino.unidad().palabra());
            case "CARGAR" -> textoCargar(termino.cargar());
            case "ID" -> termino.valor();
            default -> termino.valor() == null ? "" : termino.valor();
        };
    }

    private String textoCargar(NodoCargar cargar) {
        if (cargar == null) {
            return "CARGAR";
        }
        return "CARGAR(\"" + cargar.archivo() + "\")";
    }

    private void inicio(StringBuilder html, String titulo, String subtitulo) {
        html.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n")
                .append("<meta charset=\"UTF-8\">\n")
                .append("<meta name=\"viewport\" content=\"width=device-width, ")
                .append("initial-scale=1\">\n")
                .append("<title>").append(escape(titulo)).append("</title>\n")
                .append("<style>\n").append(CSS).append("</style>\n")
                .append("</head>\n<body>\n<header>\n<h1>")
                .append(escape(titulo)).append("</h1>\n<div class=\"lead\">")
                .append(escape(subtitulo)).append("</div>\n</header>\n<main>\n");
    }

    private void fin(StringBuilder html) {
        html.append("</main>\n<footer>PromptZal · Proyecto 2 · ")
                .append("Reporte generado por el compilador</footer>\n")
                .append("</body>\n</html>\n");
    }

    private Path escribir(Path ruta, String contenido) throws IOException {
        Path padre = ruta.getParent();
        if (padre != null) {
            Files.createDirectories(padre);
        }
        Files.writeString(ruta, contenido, StandardCharsets.UTF_8);
        return ruta;
    }

    private int contar(List<Error> errores, TipoError tipo) {
        int cantidad = 0;
        for (Error error : errores) {
            if (error.getTipo() == tipo) {
                cantidad++;
            }
        }
        return cantidad;
    }

    private String nombreTipo(TipoError tipo) {
        return switch (tipo) {
            case LEXICO -> "Léxico";
            case SINTACTICO -> "Sintáctico";
            case SEMANTICO -> "Semántico";
        };
    }

    private String claseTipo(TipoError tipo) {
        return switch (tipo) {
            case LEXICO -> "tipo-lexico";
            case SINTACTICO -> "tipo-sintactico";
            case SEMANTICO -> "tipo-semantico";
        };
    }

    private String nombreTipo(TipoSimbolo tipo) {
        return switch (tipo) {
            case AGENTE -> "Agente";
            case VARIABLE_DECLARADA -> "Variable declarada";
            case RESULTADO_COMANDO -> "Resultado de comando";
        };
    }

    private String mostrar(String valor) {
        return valor == null || valor.isEmpty()
                ? "<span class=\"vacio\">—</span>" : escape(valor);
    }

    private String escape(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
