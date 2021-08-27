package com.craftinginterpreters.lox;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    static boolean hadError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }
        else if (args.length == 1) {
            // run the lox script
            runFile(args[0]);
        }
        else {
            // REPL
            runPrompt();
        }
    }

    /**
     * Run the lox file 
     * @param path path of the file
     * @throws IOException if the path does not exist
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(65);
    }

    /**
     * Create an interactive prompt (REPL)
     * @throws IOException if there is any problem with stdin
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            // CTRL-D kills the prompt, resulting in a null line
            if (line == null) break;
            run(line);
            hadError = false;
        }
    }

    /**
     * Run the actual source 
     * @param source stream of characters
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        for (Token token: tokens) 
            System.out.println(token);
    }

    /**
     * Report the error
     * @param line line number the error occurs at
     * @param message message about the error
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println(
            "[line " + line + "] Error" + where + ": " + message
        );
    }
}
