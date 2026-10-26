package com.mycompany.promptzal_3.semantico;

import com.mycompany.promptzal_3.ast.NodoAgente;
import com.mycompany.promptzal_3.ast.NodoComando;
import com.mycompany.promptzal_3.ast.NodoConector;
import com.mycompany.promptzal_3.ast.NodoEjecucion;
import com.mycompany.promptzal_3.ast.NodoExportacion;
import com.mycompany.promptzal_3.ast.NodoExpresion;
import com.mycompany.promptzal_3.ast.NodoPrograma;
import com.mycompany.promptzal_3.ast.NodoTermino;
import com.mycompany.promptzal_3.ast.NodoVariableDecl;
import com.mycompany.promptzal_3.errores.GestorErrores;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ValidadorSemantico {

    private final GestorErrores errores;
    private final TablaSimbolos tabla = new TablaSimbolos();

    public ValidadorSemantico(GestorErrores errores) {
        this.errores = errores == null ? new GestorErrores() : errores;
    }

    public TablaSimbolos validar(NodoPrograma programa) {
        if (programa == null) {
            return tabla;
        }

        registrarAgentes(programa.agentes());
        for (NodoAgente agente : programa.agentes()) {
            validarAgente(agente);
        }

        Set<String> ejecutados = validarEjecuciones(programa.ejecuciones());
        validarExportaciones(programa.exportar(), ejecutados);
        return tabla;
    }

    private void registrarAgentes(List<NodoAgente> agentes) {
        for (NodoAgente agente : agentes) {
            Simbolo anterior = tabla.buscarAgente(agente.nombre());
            if (anterior != null) {
                reportar("El agente '" + agente.nombre()
                        + "' ya fue declarado en la línea " + anterior.linea()
                        + ".", agente.linea());
                continue;
            }
            tabla.agregar(new Simbolo(agente.nombre(), TipoSimbolo.AGENTE,
                    TablaSimbolos.AMBITO_GLOBAL, agente.linea()));
        }
    }

    private void validarAgente(NodoAgente agente) {
        for (NodoVariableDecl variable : agente.variables()) {
            validarExpresion(variable.valor(), agente.nombre(), false);
            tabla.agregar(new Simbolo(variable.nombre(),
                    TipoSimbolo.VARIABLE_DECLARADA, agente.nombre(),
                    variable.linea()));
        }

        for (NodoComando comando : agente.comandos()) {
            boolean esGenerar = "GENERAR".equals(comando.tipo());
            validarExpresion(comando.argumento(), agente.nombre(), esGenerar);
            for (NodoConector conector : comando.conectores()) {
                boolean esIdioma = "TRADUCIR".equals(comando.tipo())
                        && "EN".equals(conector.tipo());
                if (!esIdioma) {
                    validarExpresion(conector.argumento(), agente.nombre(),
                            false);
                }
            }
            if (comando.destino() != null && !comando.destino().isEmpty()) {
                tabla.agregar(new Simbolo(comando.destino(),
                        TipoSimbolo.RESULTADO_COMANDO, agente.nombre(),
                        comando.linea()));
            }
        }
    }

    private void validarExpresion(NodoExpresion expresion, String agente,
            boolean esGenerar) {
        if (expresion == null) {
            return;
        }

        boolean primerTermino = true;
        for (NodoTermino termino : expresion.terminos()) {
            if (termino == null) {
                primerTermino = false;
                continue;
            }
            if (esGenerar && primerTermino) {
                primerTermino = false;
                continue;
            }
            primerTermino = false;

            if ("ID".equals(termino.clase())
                    && tabla.buscarVariable(termino.valor(), agente) == null) {
                reportar("La variable '" + termino.valor()
                        + "' no está declarada antes en el agente '"
                        + agente + "'.", termino.linea());
            }
        }
    }

    private Set<String> validarEjecuciones(List<NodoEjecucion> ejecuciones) {
        Set<String> ejecutados = new LinkedHashSet<>();
        for (NodoEjecucion ejecucion : ejecuciones) {
            if (tabla.buscarAgente(ejecucion.agente()) == null) {
                reportar("El agente '" + ejecucion.agente()
                        + "' no existe.", ejecucion.linea());
            }
            ejecutados.add(ejecucion.agente());
        }
        return ejecutados;
    }

    private void validarExportaciones(List<NodoExportacion> exportaciones,
            Set<String> ejecutados) {
        for (NodoExportacion exportacion : exportaciones) {
            List<Simbolo> encontrados = tabla.buscarVariables(
                    exportacion.variable());
            boolean enAgenteEjecutado = encontrados.stream()
                    .anyMatch(simbolo -> ejecutados.contains(simbolo.ambito()));

            if (enAgenteEjecutado) {
                continue;
            }

            if (encontrados.isEmpty()) {
                reportar("La variable '" + exportacion.variable()
                        + "' no existe en ningún agente.", exportacion.linea());
                continue;
            }

            Set<String> ambitos = new LinkedHashSet<>();
            for (Simbolo simbolo : encontrados) {
                ambitos.add(simbolo.ambito());
            }
            reportar("La variable '" + exportacion.variable()
                    + "' pertenece a un agente que no fue ejecutado: "
                    + String.join(", ", ambitos) + ".", exportacion.linea());
        }
    }

    private void reportar(String descripcion, int linea) {
        errores.semantico(descripcion, linea, 1);
    }
}
