package com.mycompany.promptzal_3.ast;

public record NodoTermino(
        String clase,
        String valor,
        NodoUnidad unidad,
        NodoCargar cargar,
        int linea) {
}
