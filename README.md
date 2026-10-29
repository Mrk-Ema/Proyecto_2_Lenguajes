# PromptZal — Proyecto 2

Compilador del lenguaje PromptZal para el Laboratorio de Lenguajes Formales y de Programación.

El proyecto transforma un archivo `.pz` en un prompt final listo para usarse con una herramienta de IA. Incluye análisis léxico con JFlex, parser descendente recursivo escrito a mano, AST, tabla de símbolos, validación semántica, generación del prompt y reportes HTML.

## Requisitos

- Java 21
- Maven 3.9 o superior

## Compilar y ejecutar

```bash
mvn clean package
java -jar target/PromptZal_3-1.0-SNAPSHOT.jar
```

También se puede abrir y ejecutar desde NetBeans.

## Uso

1. Abrir un archivo `.pz` o usar el programa de ejemplo que aparece al iniciar.
2. Pulsar **Compilar**.
3. Revisar el prompt en la pestaña **Promptzal**.
4. Revisar los errores en la pestaña **Errores**.
5. Guardar el prompt como `.md` o `.txt`.
6. Entrar a **Reportes** y abrir los reportes HTML.

Los reportes se generan en la carpeta `salidas/`:

- `reporte_errores.html`
- `reporte_simbolos.html`
- `reporte_ast.html`

## Estructura

```text
src/main/jflex/                 Especificación JFlex del lexer
src/main/java/.../lexico/       Lexer generado, tokens y lector de tokens
src/main/java/.../ast/          Nodos del árbol de sintaxis abstracta
src/main/java/.../sintactico/   Parser descendente recursivo
src/main/java/.../semantico/    Tabla de símbolos y validaciones
src/main/java/.../generador/    Generación del prompt
src/main/java/.../compilador/   Orquestación de las fases
src/main/java/.../errores/      Errores léxicos, sintácticos y semánticos
src/main/java/.../reportes/     Generación de reportes HTML
src/main/java/.../ui/           Interfaz Swing
pruebas/                        Programas y archivos de prueba
entrega/                        Programa, prompt y reportes de ejemplo
docs/                           Manuales y capturas
scripts/                        Regeneración del lexer con JFlex
```

## Regenerar el lexer

El archivo `Lexer.java` se genera con JFlex y está versionado. Para regenerarlo:

```bash
./scripts/generar-lexer.sh
```

## Pruebas de consola

```bash
java -cp target/classes com.mycompany.promptzal_3.pruebas.PruebaLexico pruebas/referencia.pz
java -cp target/classes com.mycompany.promptzal_3.pruebas.PruebaParser pruebas/referencia.pz
java -cp target/classes com.mycompany.promptzal_3.pruebas.PruebaSemantica pruebas/semanticos.pz
java -cp target/classes com.mycompany.promptzal_3.pruebas.PruebaGenerador pruebas/referencia.pz pruebas/prompt_esperado.md
java -cp target/classes com.mycompany.promptzal_3.pruebas.PruebaReportes pruebas/referencia.pz
```

## Archivos de ejemplo

- `pruebas/referencia.pz`: programa válido que usa todos los comandos.
- `pruebas/prompt_esperado.md`: prompt esperado del programa de referencia.
- `entrega/ejemplo.pz`: programa único de la entrega. Incluye errores intencionales en un agente no ejecutado; el agente `analista` es válido y genera el prompt.
- `entrega/prompt_ejemplo.md`: prompt generado desde `entrega/ejemplo.pz`.
- `entrega/reportes/`: los tres reportes HTML generados desde `entrega/ejemplo.pz`.

## Documentación

- `docs/manual_tecnico.pdf`
- `docs/manual_usuario.pdf`
