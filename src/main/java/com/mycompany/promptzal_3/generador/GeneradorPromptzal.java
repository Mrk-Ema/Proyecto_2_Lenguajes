package com.mycompany.promptzal_3.generador;

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
import java.util.List;

public final class GeneradorPromptzal {

    public String generar(NodoPrograma programa) {
        if (programa == null) {
            return "";
        }

        StringBuilder salida = new StringBuilder();
        salida.append("# Prompt generado con PromptZal\n");
        agregarDirectiva(salida, "Modelo destino: ", programa.directivas(),
                "@modelo");
        agregarDirectiva(salida, "Rol general: ", programa.directivas(), "@rol");
        agregarDirectiva(salida, "Formato de respuesta: ",
                programa.directivas(), "@formato");
        salida.append('\n');

        for (NodoEjecucion ejecucion : programa.ejecuciones()) {
            NodoAgente agente = buscarAgente(programa.agentes(),
                    ejecucion.agente());
            if (agente != null) {
                generarAgente(salida, agente);
            }
        }

        salida.append("## Resultado solicitado\n");
        if (programa.exportar().isEmpty()) {
            salida.append("- (ninguno)\n");
        } else {
            for (NodoExportacion exportacion : programa.exportar()) {
                salida.append("- ").append(exportacion.variable()).append('\n');
            }
        }
        return salida.toString();
    }

    private void agregarDirectiva(StringBuilder salida, String etiqueta,
            List<NodoDirectiva> directivas, String nombre) {
        String valor = valorDirectiva(directivas, nombre);
        if (valor != null) {
            salida.append(etiqueta).append(valor).append('\n');
        }
    }

    private String valorDirectiva(List<NodoDirectiva> directivas,
            String nombre) {
        for (NodoDirectiva directiva : directivas) {
            if (nombre.equals(directiva.nombre())) {
                return directiva.valor();
            }
        }
        return null;
    }

    private NodoAgente buscarAgente(List<NodoAgente> agentes, String nombre) {
        for (NodoAgente agente : agentes) {
            if (agente.nombre().equals(nombre)) {
                return agente;
            }
        }
        return null;
    }

    private void generarAgente(StringBuilder salida, NodoAgente agente) {
        salida.append("## Agente: ").append(agente.nombre()).append('\n');
        String contexto = agente.contexto() == null
                ? "" : agente.contexto().texto();
        salida.append("Contexto: ").append(contexto).append("\n\n");

        salida.append("Variables disponibles:\n");
        if (agente.variables().isEmpty()) {
            salida.append("- (ninguna)\n");
        } else {
            for (NodoVariableDecl variable : agente.variables()) {
                salida.append("- ").append(variable.nombre()).append(": ")
                        .append(expresionVariable(variable.valor()))
                        .append('\n');
            }
        }
        salida.append('\n');

        salida.append("Instrucciones:\n");
        if (agente.comandos().isEmpty()) {
            salida.append("(ninguna)\n");
        } else {
            int numero = 1;
            for (NodoComando comando : agente.comandos()) {
                salida.append(numero++).append(". ")
                        .append(instruccion(comando)).append('\n');
            }
        }
        salida.append('\n');
    }

    private String instruccion(NodoComando comando) {
        String argumento = expresionNatural(comando.argumento());
        String instruccion = switch (comando.tipo()) {
            case "PREGUNTAR" -> {
                String sobre = expresionConector(comando, "SOBRE");
                yield terminarFrase("Basándote en "
                        + (sobre.isEmpty() ? "el contexto" : sobre)
                        + ", responde: " + argumento);
            }
            case "GENERAR" -> {
                String desde = expresionConector(comando, "DESDE");
                yield terminarFrase("Genera " + argumento
                        + (desde.isEmpty() ? "" : " a partir de: " + desde));
            }
            case "RESUMIR" -> {
                String en = expresionConector(comando, "EN");
                yield terminarFrase("Resume " + argumento
                        + (en.isEmpty() ? "" : " en un máximo de " + en));
            }
            case "ANALIZAR" -> {
                String sobre = expresionConector(comando, "SOBRE");
                yield terminarFrase("Analiza " + argumento
                        + (sobre.isEmpty() ? "" : " considerando: " + sobre));
            }
            case "TRADUCIR" -> {
                String en = expresionConector(comando, "EN");
                yield terminarFrase("Traduce " + argumento
                        + (en.isEmpty() ? "" : " al idioma " + en));
            }
            case "CLASIFICAR" -> {
                String en = expresionConector(comando, "EN");
                yield terminarFrase("Clasifica " + argumento
                        + (en.isEmpty() ? "" : " en las categorías: " + en));
            }
            case "EXTRAER" -> {
                String desde = expresionConector(comando, "DESDE");
                yield terminarFrase("Extrae " + argumento
                        + (desde.isEmpty() ? "" : " a partir de " + desde));
            }
            default -> terminarFrase("Ejecuta " + comando.tipo() + ": "
                    + argumento);
        };

        if (comando.destino() != null && !comando.destino().isEmpty()) {
            String verbo = "PREGUNTAR".equals(comando.tipo())
                    ? "Guarda la respuesta como "
                    : "Guarda el resultado como ";
            instruccion += " " + verbo + comando.destino() + ".";
        }

        String formato = expresionConector(comando, "COMO");
        if (!formato.isEmpty()) {
            instruccion += " " + terminarFrase(
                    "Entrega el resultado en formato " + formato);
        }
        return instruccion;
    }

    private String terminarFrase(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        char ultimo = texto.charAt(texto.length() - 1);
        if (ultimo == '.' || ultimo == '?' || ultimo == '!') {
            return texto;
        }
        return texto + ".";
    }

    private String expresionConector(NodoComando comando, String tipo) {
        for (NodoConector conector : comando.conectores()) {
            if (tipo.equals(conector.tipo())) {
                return expresionNatural(conector.argumento());
            }
        }
        return "";
    }

    private String expresionVariable(NodoExpresion expresion) {
        if (expresion == null || expresion.terminos().isEmpty()) {
            return "(sin valor)";
        }

        StringBuilder texto = new StringBuilder();
        for (NodoTermino termino : expresion.terminos()) {
            if (texto.length() > 0) {
                texto.append(" + ");
            }
            texto.append(terminoVariable(termino));
        }
        return texto.toString();
    }

    private String terminoVariable(NodoTermino termino) {
        if (termino == null) {
            return "";
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

    private String expresionNatural(NodoExpresion expresion) {
        if (expresion == null || expresion.terminos().isEmpty()) {
            return "";
        }

        StringBuilder texto = new StringBuilder();
        for (NodoTermino termino : expresion.terminos()) {
            if (termino == null) {
                continue;
            }
            if (texto.length() > 0) {
                texto.append(" + ");
            }
            texto.append(terminoNatural(termino));
        }
        return texto.toString();
    }

    private String terminoNatural(NodoTermino termino) {
        return switch (termino.clase()) {
            case "CADENA" -> termino.valor();
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
        return "contenido del archivo \"" + cargar.archivo()
                + "\" (adjuntar el archivo)";
    }
}
