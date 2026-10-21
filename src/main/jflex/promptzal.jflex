// Lexer de PromptZal.
// Regenerar con: ./scripts/generar-lexer.sh

package com.mycompany.promptzal_3.lexico;

import com.mycompany.promptzal_3.errores.GestorErrores;

%%

%class Lexer
%public
%function siguienteToken
%type Token
%line
%column
%unicode

%{
    private GestorErrores errores;

    public Lexer(java.io.Reader entrada, GestorErrores errores) {
        this(entrada);
        this.errores = errores;
    }

    private Token tok(TipoToken tipo) {
        // JFlex cuenta desde 0; el token usa base 1.
        return new Token(tipo, yytext(), yyline + 1, yycolumn + 1);
    }
%}

ESPACIO = [ \t\r\n]+
LETRA   = [a-zA-ZáéíóúÁÉÍÓÚñÑüÜ_]
ID_CHAR = [a-zA-Z0-9áéíóúÁÉÍÓÚñÑüÜ_]

%%

// Espacios y comentarios.
{ESPACIO}                    { /* ignorar */ }
"//"[^\r\n]*                 { /* ignorar comentario de línea */ }
"/*"([^*]|\*+[^*/])*\*+"/"   { /* ignorar comentario de bloque */ }

// Palabras reservadas, directivas y conectores.
"@modelo"|"@rol"|"@formato"  { return tok(TipoToken.DIRECTIVA); }
"AGENTE"                     { return tok(TipoToken.AGENTE); }
"contexto"                   { return tok(TipoToken.CONTEXTO); }
"variable"                   { return tok(TipoToken.VARIABLE); }
"EJECUTAR"                   { return tok(TipoToken.EJECUTAR); }
"EXPORTAR"                   { return tok(TipoToken.EXPORTAR); }
"PREGUNTAR"|"GENERAR"|"RESUMIR"|"ANALIZAR"|"TRADUCIR"|"CLASIFICAR"|"EXTRAER"
                             { return tok(TipoToken.COMANDO_IA); }
"CARGAR"                     { return tok(TipoToken.CARGAR); }
"SOBRE"|"DESDE"|"EN"|"COMO"  { return tok(TipoToken.CONECTOR); }

// Símbolos.
"->"                         { return tok(TipoToken.FLECHA); }
"="                          { return tok(TipoToken.IGUAL); }
"+"                          { return tok(TipoToken.MAS); }
"{"                          { return tok(TipoToken.LLAVE_A); }
"}"                          { return tok(TipoToken.LLAVE_C); }
"("                          { return tok(TipoToken.PAR_A); }
")"                          { return tok(TipoToken.PAR_C); }
","                          { return tok(TipoToken.COMA); }

// Literales e identificadores.
\"[^\"\r\n]*\"               { return tok(TipoToken.CADENA); }
[0-9]+("."[0-9]+)?           { return tok(TipoToken.NUMERO); }
{LETRA}({ID_CHAR})*          { return tok(TipoToken.ID); }

// Fin de archivo.
<<EOF>>                      { return new Token(TipoToken.EOF, "", yyline + 1, yycolumn + 1); }

// Error léxico: reportar y continuar.
[^]                          {
                               if (errores != null) {
                                   errores.lexico(
                                       "Carácter no reconocido: '" + yytext() + "'",
                                       yyline + 1, yycolumn + 1, yytext());
                               }
                             }
