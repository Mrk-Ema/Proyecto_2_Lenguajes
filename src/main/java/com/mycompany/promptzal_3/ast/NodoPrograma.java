package com.mycompany.promptzal_3.ast;

import java.util.List;

public record NodoPrograma(
        List<NodoDirectiva> directivas,
        List<NodoAgente> agentes,
        List<String> ejecuciones,
        List<String> exportar) {
}
