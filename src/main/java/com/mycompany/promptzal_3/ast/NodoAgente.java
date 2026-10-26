package com.mycompany.promptzal_3.ast;

import java.util.List;

public record NodoAgente(
        String nombre,
        NodoContexto contexto,
        List<NodoVariableDecl> variables,
        List<NodoComando> comandos,
        int linea) {
}
