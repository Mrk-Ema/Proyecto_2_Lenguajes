package com.mycompany.promptzal_3.lexico;

import java.util.Set;

public final class Diccionario {

    public enum Categoria {
        DIRECTIVA,
        RESERVADA,
        COMANDO,
        FUNCION,
        CONECTOR,
        DESCONOCIDA
    }

    public static final Set<String> DIRECTIVAS = Set.of(
            "@modelo", "@rol", "@formato");

    public static final Set<String> RESERVADAS = Set.of(
            "AGENTE", "contexto", "variable", "EJECUTAR", "EXPORTAR");

    public static final Set<String> COMANDOS = Set.of(
            "PREGUNTAR", "GENERAR", "RESUMIR", "ANALIZAR",
            "TRADUCIR", "CLASIFICAR", "EXTRAER");

    public static final Set<String> FUNCIONES = Set.of(
            "CARGAR");

    public static final Set<String> CONECTORES = Set.of(
            "SOBRE", "DESDE", "EN", "COMO");

    private Diccionario() {
        // clase de utilidades
    }

    // Categoría de una palabra
    public static Categoria categoriaDe(String palabra) {
        if (palabra == null || palabra.isEmpty()) {
            return Categoria.DESCONOCIDA;
        }
        if (DIRECTIVAS.contains(palabra)) {
            return Categoria.DIRECTIVA;
        }
        if (RESERVADAS.contains(palabra)) {
            return Categoria.RESERVADA;
        }
        if (COMANDOS.contains(palabra)) {
            return Categoria.COMANDO;
        }
        if (FUNCIONES.contains(palabra)) {
            return Categoria.FUNCION;
        }
        if (CONECTORES.contains(palabra)) {
            return Categoria.CONECTOR;
        }
        return Categoria.DESCONOCIDA;
    }
}
