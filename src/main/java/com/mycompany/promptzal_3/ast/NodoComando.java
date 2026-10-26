package com.mycompany.promptzal_3.ast;

import java.util.List;

public record NodoComando(
        String tipo,
        NodoExpresion argumento,
        List<NodoConector> conectores,
        String destino,
        int linea) {
}
