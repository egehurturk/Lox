package com.craftinginterpreters.lox;

public class Token {
    // Type of the token {@link TokenType}
    public final TokenType type;

    // Raw token
    public final String lexeme;

    // Literal value of the token. This is useful for number, boolean, and string literals. If the token type is not one of 
    // them, value for this field will be {@code null}
    public final Object literal;

    // Line no where the token exists
    public final int line;
    
    public Token(TokenType type, String lexeme, Object literal, 
    int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
