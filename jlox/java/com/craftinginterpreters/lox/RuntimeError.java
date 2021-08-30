package com.craftinginterpreters.lox;

public class RuntimeError extends RuntimeException {
    final Token token;

    public RuntimeError(Token tok, String msg) {
        super(msg);
        this.token = tok;
    }
}
