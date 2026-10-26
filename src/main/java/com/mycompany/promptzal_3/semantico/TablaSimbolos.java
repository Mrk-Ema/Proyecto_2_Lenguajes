package com.mycompany.promptzal_3.semantico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TablaSimbolos {

    public static final String AMBITO_GLOBAL = "global";

    private final List<Simbolo> simbolos = new ArrayList<>();

    public void agregar(Simbolo simbolo) {
        simbolos.add(simbolo);
    }

    public List<Simbolo> getSimbolos() {
        return Collections.unmodifiableList(simbolos);
    }

    public Simbolo buscarAgente(String nombre) {
        for (Simbolo simbolo : simbolos) {
            if (simbolo.tipo() == TipoSimbolo.AGENTE
                    && simbolo.nombre().equals(nombre)) {
                return simbolo;
            }
        }
        return null;
    }

    public Simbolo buscarVariable(String nombre, String agente) {
        for (int i = simbolos.size() - 1; i >= 0; i--) {
            Simbolo simbolo = simbolos.get(i);
            if (esVariable(simbolo)
                    && simbolo.nombre().equals(nombre)
                    && simbolo.ambito().equals(agente)) {
                return simbolo;
            }
        }
        return null;
    }

    public List<Simbolo> buscarVariables(String nombre) {
        List<Simbolo> encontrados = new ArrayList<>();
        for (Simbolo simbolo : simbolos) {
            if (esVariable(simbolo) && simbolo.nombre().equals(nombre)) {
                encontrados.add(simbolo);
            }
        }
        return encontrados;
    }

    private static boolean esVariable(Simbolo simbolo) {
        return simbolo.tipo() == TipoSimbolo.VARIABLE_DECLARADA
                || simbolo.tipo() == TipoSimbolo.RESULTADO_COMANDO;
    }
}
