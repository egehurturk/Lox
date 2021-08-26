package com.craftinginterpreters.lox;

import static com.craftinginterpreters.lox.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Scans the given source, aggregates tokens from the source. Reports the 
 * error if there is any.
 */
public class Scanner {

    // Source string
    private final String source;

    private final List<Token> tokens = new ArrayList<>();

    // Index of new token start;
    private int start = 0;

    // Current position in the source string
    private int current = 0;

    // Line number (for error reporting)
    private int line = 1;

    // Reserved keywords
    private static final Map<String, TokenType> keywords;    

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
      }
    

    /**
     * Initialize a scanner with the given source
     * @param source a stream of characters
     */
    public Scanner(String source) {
        Objects.requireNonNull(source);
        this.source = source;
    }

    /**
     * Scan the source text for tokens and store them
     * @return list of tokens scanned from the {@code source}
     */
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break; 
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '/':
                // check for comments
                if (match('/')) {
                    // ignore the rest of the line
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    // normal slash operation
                    addToken(SLASH);
                }
                break;
            case ' ': case '\r': case '\t': break; // ignore all white spaces
            case '\n': 
                line++;
                break;
            case '"': string(); break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } 
                else {
                    Lox.error(line, "Unexpected character: " + c);
                }
                break;
        }
    }

    /**
     * Deal with an identifier
     */
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        // if the text is something like orchid, then it is 
        // an identifier. If it is "or", it is the relevant token
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;

        addToken(type);
    }

    /**
     * Deal with a number
     */
    private void number() {
        while (isDigit(peek())) advance();

        if (peek() == '.' && isDigit(peekNext())) {
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(
            source.substring(start, current)
        ));
    }

    /**
     * Deal with a string
     */
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string");
            return;
        }

        // closing "
        advance();

        // Trim the " and "
        String value = source.substring(start+1, current-1);
        addToken(STRING, value);
    }


    // <------------------ UTILITY FUNCTIONS ------------------>

    /**
     * Check if the given character is an alphabetical character
     * @param c source character
     * @return {@code true} if {@code c} is alphabetical, {@code false} otherwise
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
        (c >= 'A' && c <= 'Z') ||
         c == '_';
    }

    /**
     * Check if the given character is an alphanumerical character
     * @param c source character
     * @return {@code true} if {@code c} is alphanumerical, {@code false} otherwise
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

   
    /**
     * Check if the given character is a digit
     * @param c source character
     * @return {@code true} if {@code c} is a digit, {@code false} otherwise
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Peek the 2 next character
     * @return 2 index offset character
     */
    private char peekNext() {
        if (current+1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
    
    /**
     * @return the next character
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    /**
     * Advance {@code current} to the next position in the {@code source}
     * @return the next character at {@code current + 1}
     */
    private char advance() {
        return source.charAt(current++);
    }

    /**
     * Add the token {@code t} with null literal value
     * @param t token type
     */
    private void addToken(TokenType t) {
        addToken(t, null);
    }

    /**
     * Add the token {@code t} to {@code tokens} with the given literal value
     * @param t token type
     * @param literal literal value of the token
     */
    private void addToken(TokenType t, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(t, text, literal, line));
    }


    /**
     * Check if the current position is within the file
     * @return {@code true} if the current character is not beyond the file end (EOF), 
     *         {@code false} otherwise
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

}
