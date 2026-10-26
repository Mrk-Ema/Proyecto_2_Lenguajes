package com.mycompany.promptzal_3.sintactico;

import com.mycompany.promptzal_3.ast.NodoAgente;
import com.mycompany.promptzal_3.ast.NodoCargar;
import com.mycompany.promptzal_3.ast.NodoComando;
import com.mycompany.promptzal_3.ast.NodoConector;
import com.mycompany.promptzal_3.ast.NodoContexto;
import com.mycompany.promptzal_3.ast.NodoDirectiva;
import com.mycompany.promptzal_3.ast.NodoExpresion;
import com.mycompany.promptzal_3.ast.NodoPrograma;
import com.mycompany.promptzal_3.ast.NodoTermino;
import com.mycompany.promptzal_3.ast.NodoUnidad;
import com.mycompany.promptzal_3.ast.NodoVariableDecl;
import com.mycompany.promptzal_3.errores.GestorErrores;
import com.mycompany.promptzal_3.lexico.TipoToken;
import com.mycompany.promptzal_3.lexico.Token;
import java.util.ArrayList;
import java.util.List;

// Parser descendente recursivo.
public class Parser {

    private static final int MAX_ERRORES = 200;

    private final List<Token> tokens;
    private final GestorErrores errores;
    private int indice;
    private int erroresReportados;

    public Parser(List<Token> tokens, GestorErrores errores) {
        if (tokens == null || tokens.isEmpty()) {
            this.tokens = List.of(new Token(TipoToken.EOF, "", 1, 1));
        } else {
            this.tokens = tokens;
        }
        this.errores = errores == null ? new GestorErrores() : errores;
    }

    public NodoPrograma parsePrograma() {
        indice = 0;
        erroresReportados = 0;

        List<NodoDirectiva> directivas = parseDirectivas();
        List<NodoAgente> agentes = parseAgentes();
        List<String> ejecuciones = parseEjecuciones();
        List<String> exportar = parseExportar();

        if (!es(TipoToken.EOF)) {
            reportar("Se esperaba EOF", actual(), TipoToken.EOF);
            while (!es(TipoToken.EOF)) {
                avanzar();
            }
        }

        return new NodoPrograma(directivas, agentes, ejecuciones, exportar);
    }

    private List<NodoDirectiva> parseDirectivas() {
        List<NodoDirectiva> directivas = new ArrayList<>();
        while (es(TipoToken.DIRECTIVA)) {
            Token directiva = avanzar();
            Token valor = esperar(TipoToken.CADENA);
            directivas.add(new NodoDirectiva(directiva.getLexema(),
                    valor == null ? "" : sinComillas(valor.getLexema()),
                    directiva.getFila()));
        }
        return directivas;
    }

    private List<NodoAgente> parseAgentes() {
        List<NodoAgente> agentes = new ArrayList<>();
        if (!es(TipoToken.AGENTE)) {
            reportar("Se esperaba AGENTE", actual(), TipoToken.AGENTE);
            return agentes;
        }

        while (es(TipoToken.AGENTE)) {
            NodoAgente agente = parseAgente();
            if (agente != null) {
                agentes.add(agente);
            }
            if (!es(TipoToken.AGENTE) && !es(TipoToken.EJECUTAR)
                    && !es(TipoToken.EXPORTAR) && !es(TipoToken.EOF)) {
                sincronizarSeccion();
            }
        }
        return agentes;
    }

    private NodoAgente parseAgente() {
        Token inicio = esperar(TipoToken.AGENTE);
        if (inicio == null) {
            return null;
        }

        Token nombre = esperar(TipoToken.ID);
        String nombreAgente = nombre == null ? "<error>" : nombre.getLexema();

        if (esperar(TipoToken.LLAVE_A) == null) {
            return null;
        }

        NodoContexto contexto = parseContexto();
        List<NodoVariableDecl> variables = parseVariables();
        List<NodoComando> comandos = parseComandos();
        esperar(TipoToken.LLAVE_C);

        return new NodoAgente(nombreAgente, contexto, variables, comandos,
                inicio.getFila());
    }

    private NodoContexto parseContexto() {
        Token contexto = esperar(TipoToken.CONTEXTO);
        if (contexto == null) {
            return new NodoContexto("", actual().getFila());
        }

        if (esperar(TipoToken.IGUAL) == null) {
            return new NodoContexto("", contexto.getFila());
        }

        Token texto = esperar(TipoToken.CADENA);
        return new NodoContexto(texto == null ? "" : sinComillas(texto.getLexema()),
                contexto.getFila());
    }

    private List<NodoVariableDecl> parseVariables() {
        List<NodoVariableDecl> variables = new ArrayList<>();
        while (es(TipoToken.VARIABLE)) {
            Token variable = avanzar();
            Token nombre = esperar(TipoToken.ID);
            if (nombre == null) {
                continue;
            }
            if (esperar(TipoToken.IGUAL) == null) {
                continue;
            }
            NodoExpresion valor = parseExpresion();
            variables.add(new NodoVariableDecl(nombre.getLexema(), valor,
                    variable.getFila()));
        }
        return variables;
    }

    private List<NodoComando> parseComandos() {
        List<NodoComando> comandos = new ArrayList<>();
        if (!es(TipoToken.COMANDO_IA)) {
            reportar("Se esperaba COMANDO_IA", actual(), TipoToken.COMANDO_IA);
            return comandos;
        }

        while (es(TipoToken.COMANDO_IA)) {
            NodoComando comando = parseComando();
            if (comando != null) {
                comandos.add(comando);
            }
        }
        return comandos;
    }

    private NodoComando parseComando() {
        Token inicio = esperar(TipoToken.COMANDO_IA);
        if (inicio == null) {
            return null;
        }

        NodoExpresion argumento = parseExpresion();
        if (argumento.terminos().isEmpty()) {
            return null;
        }

        List<NodoConector> conectores = parseConectores();
        if (esperar(TipoToken.FLECHA) == null) {
            return null;
        }

        Token destino = esperar(TipoToken.ID);
        if (destino == null) {
            return null;
        }

        return new NodoComando(inicio.getLexema(), argumento, conectores,
                destino.getLexema(), inicio.getFila());
    }

    private List<NodoConector> parseConectores() {
        List<NodoConector> conectores = new ArrayList<>();
        while (es(TipoToken.CONECTOR)) {
            Token conector = avanzar();
            NodoExpresion argumento = parseExpresion();
            conectores.add(new NodoConector(conector.getLexema(), argumento,
                    conector.getFila()));
        }
        return conectores;
    }

    private NodoExpresion parseExpresion() {
        List<NodoTermino> terminos = new ArrayList<>();
        NodoTermino primero = parseTermino();
        if (primero == null) {
            return new NodoExpresion(terminos);
        }
        terminos.add(primero);

        while (es(TipoToken.MAS)) {
            avanzar();
            NodoTermino siguiente = parseTermino();
            if (siguiente == null) {
                break;
            }
            terminos.add(siguiente);
        }
        return new NodoExpresion(terminos);
    }

    private NodoTermino parseTermino() {
        if (es(TipoToken.CADENA)) {
            Token token = avanzar();
            return new NodoTermino("CADENA", sinComillas(token.getLexema()),
                    null, null, token.getFila());
        }
        if (es(TipoToken.ID)) {
            Token token = avanzar();
            return new NodoTermino("ID", token.getLexema(), null, null,
                    token.getFila());
        }
        if (es(TipoToken.NUMERO)) {
            Token token = avanzar();
            NodoUnidad unidad = parseUnidad();
            return new NodoTermino("NUMERO", token.getLexema(), unidad, null,
                    token.getFila());
        }
        if (es(TipoToken.CARGAR)) {
            NodoCargar cargar = parseCargar();
            if (cargar == null) {
                return null;
            }
            return new NodoTermino("CARGAR", null, null, cargar, cargar.linea());
        }

        reportar("Se esperaba CADENA, ID, NUMERO o CARGAR", actual(), null);
        sincronizar();
        return null;
    }

    private NodoUnidad parseUnidad() {
        if (es(TipoToken.ID)) {
            Token token = avanzar();
            return new NodoUnidad(token.getLexema(), token.getFila());
        }
        return null;
    }

    private NodoCargar parseCargar() {
        Token inicio = esperar(TipoToken.CARGAR);
        if (inicio == null) {
            return null;
        }
        if (esperar(TipoToken.PAR_A) == null) {
            return null;
        }
        Token archivo = esperar(TipoToken.CADENA);
        if (archivo == null) {
            return null;
        }
        if (esperar(TipoToken.PAR_C) == null) {
            return null;
        }
        return new NodoCargar(sinComillas(archivo.getLexema()), inicio.getFila());
    }

    private List<String> parseEjecuciones() {
        List<String> ejecuciones = new ArrayList<>();
        if (!es(TipoToken.EJECUTAR)) {
            reportar("Se esperaba EJECUTAR", actual(), TipoToken.EJECUTAR);
            return ejecuciones;
        }

        while (es(TipoToken.EJECUTAR)) {
            avanzar();
            Token id = esperar(TipoToken.ID);
            if (id != null) {
                ejecuciones.add(id.getLexema());
            }
        }
        return ejecuciones;
    }

    private List<String> parseExportar() {
        List<String> exportar = new ArrayList<>();
        if (esperar(TipoToken.EXPORTAR) == null) {
            return exportar;
        }

        Token id = esperar(TipoToken.ID);
        if (id == null) {
            return exportar;
        }
        exportar.add(id.getLexema());

        while (es(TipoToken.COMA)) {
            avanzar();
            Token siguiente = esperar(TipoToken.ID);
            if (siguiente == null) {
                break;
            }
            exportar.add(siguiente.getLexema());
        }
        return exportar;
    }

    private Token actual() {
        if (indice >= tokens.size()) {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(indice);
    }

    private Token avanzar() {
        Token token = actual();
        if (indice < tokens.size() - 1) {
            indice++;
        }
        return token;
    }

    private boolean es(TipoToken tipo) {
        return actual().getTipo() == tipo;
    }

    private Token esperar(TipoToken tipo) {
        if (es(tipo)) {
            return avanzar();
        }

        Token encontrado = actual();
        reportar("Se esperaba " + tipo + " y se encontró '"
                + encontrado.getLexema() + "'", encontrado, tipo);
        sincronizar();
        return null;
    }

    private void sincronizar() {
        if (es(TipoToken.EOF)) {
            return;
        }
        if (!esSincronizacion(actual().getTipo())) {
            avanzar();
        }
        while (!esSincronizacion(actual().getTipo()) && !es(TipoToken.EOF)) {
            avanzar();
        }
    }

    private void sincronizarSeccion() {
        while (!es(TipoToken.AGENTE) && !es(TipoToken.EJECUTAR)
                && !es(TipoToken.EXPORTAR) && !es(TipoToken.EOF)) {
            avanzar();
        }
    }

    private boolean esSincronizacion(TipoToken tipo) {
        return tipo == TipoToken.DIRECTIVA
                || tipo == TipoToken.AGENTE
                || tipo == TipoToken.LLAVE_C
                || tipo == TipoToken.COMANDO_IA
                || tipo == TipoToken.VARIABLE
                || tipo == TipoToken.EJECUTAR
                || tipo == TipoToken.EXPORTAR
                || tipo == TipoToken.EOF;
    }

    private void reportar(String descripcion, Token token, TipoToken esperado) {
        if (erroresReportados >= MAX_ERRORES) {
            return;
        }
        errores.sintactico(descripcion, token.getFila(), token.getColumna(),
                token.getLexema(), esperado == null ? "" : esperado.name());
        erroresReportados++;
    }

    private static String sinComillas(String texto) {
        if (texto == null) {
            return "";
        }
        if (texto.length() >= 2 && texto.startsWith("\"") && texto.endsWith("\"")) {
            return texto.substring(1, texto.length() - 1);
        }
        return texto;
    }
}
