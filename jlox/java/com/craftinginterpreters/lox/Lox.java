package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            // run the lox script
            runFile(args[0]);
        } else {
            // REPL
            runPrompt();
        }
    }

    /**
     * Run the lox file
     * 
     * @param path path of the file
     * @throws IOException if the path does not exist
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError)
            System.exit(65);
        if (hadRuntimeError)
            System.exit(70);    
    }

    /**
     * Create an interactive prompt (REPL)
     * 
     * @throws IOException if there is any problem with stdin
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("lox> ");
            String line = reader.readLine();
            // CTRL-D kills the prompt, resulting in a null line
            if (line == null) {
                System.out.println();
                break;
            }
            run(line);
            hadError = false;
        }
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF)
            report(token.line, " at end", message);
        else
            report(token.line, " at '" + token.lexeme + "'", message);
    }

    /**
     * Run the actual source
     * 
     * @param source stream of characters
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        if (expression == null)
            return;
        if (hadError)
            return;

        interpreter.interpret(expression);
    }

    /**
     * Report the error
     * 
     * @param line    line number the error occurs at
     * @param message message about the error
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    static void runtimeError(RuntimeError error) {
        System.err.println("[line " + error.token.line + "]    " + error.getMessage());
        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
    }
}
