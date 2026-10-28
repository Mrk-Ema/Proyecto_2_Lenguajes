package com.mycompany.promptzal_3.pruebas;

import com.mycompany.promptzal_3.compilador.Compilador;
import com.mycompany.promptzal_3.compilador.ResultadoCompilacion;
import com.mycompany.promptzal_3.reportes.GeneradorReportes;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

// Genera los tres reportes HTML para revisarlos en el navegador.
public final class PruebaReportes {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Uso: PruebaReportes <archivo.pz>");
            return;
        }

        String codigo = Files.readString(Path.of(args[0]),
                StandardCharsets.UTF_8);
        ResultadoCompilacion resultado = new Compilador().compilar(codigo);
        GeneradorReportes generador = new GeneradorReportes();

        Path errores = generador.errores(resultado.errores());
        Path simbolos = generador.simbolos(resultado.tablaSimbolos());
        Path ast = generador.ast(resultado.programa());

        System.out.println("Errores de compilación: "
                + resultado.errores().cantidad());
        System.out.println("Reporte de errores: " + errores.toAbsolutePath());
        System.out.println("Reporte de símbolos: " + simbolos.toAbsolutePath());
        System.out.println("Reporte AST: " + ast.toAbsolutePath());
    }
}
