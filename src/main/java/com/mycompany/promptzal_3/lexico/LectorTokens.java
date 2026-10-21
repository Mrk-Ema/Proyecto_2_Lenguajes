package com.mycompany.promptzal_3.lexico;

import com.mycompany.promptzal_3.errores.GestorErrores;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Ejecuta el lexer y devuelve los tokens hasta EOF.
public final class LectorTokens {

    public List<Token> analizar(String codigo, GestorErrores errores) {
        Objects.requireNonNull(codigo, "El código no puede ser null");
        GestorErrores gestor = errores == null ? new GestorErrores() : errores;
        List<Token> tokens = new ArrayList<>();

        try (StringReader entrada = new StringReader(codigo)) {
            Lexer lexer = new Lexer(entrada, gestor);
            Token token;
            do {
                token = lexer.siguienteToken();
                tokens.add(token);
            } while (token.getTipo() != TipoToken.EOF);
        } catch (IOException ex) {
            gestor.lexico("Error de lectura: " + ex.getMessage(), 1, 1);
        }

        return tokens;
    }

    public List<Token> analizar(String codigo) {
        return analizar(codigo, new GestorErrores());
    }
}
