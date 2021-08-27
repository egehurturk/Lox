package com.craftinginterpreters.lox.exercise;

import com.craftinginterpreters.lox.*;
import com.craftinginterpreters.lox.Expr.Binary;
import com.craftinginterpreters.lox.Expr.Grouping;
import com.craftinginterpreters.lox.Expr.Literal;
import com.craftinginterpreters.lox.Expr.Unary;



public class ReversePolishNotation implements Expr.Visitor<String> {

    String convert(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Binary expr) {
        return polishisize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Grouping expr) {
        return expr.expression.accept(this);
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
        if (expr == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
       return polishisize(expr.operator.lexeme, expr.right);
    }

    private String polishisize(String operator, Expr... expressions) {
        StringBuilder builder = new StringBuilder();

        for (Expr expression: expressions) {
            builder.append(expression.accept(this));
            builder.append(" ");
        }

        builder.append(operator);

        return builder.toString();
    }

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
            new Expr.Grouping(
                new Expr.Binary(
                    new Expr.Literal(1), new Token(TokenType.PLUS, "+", null, 1), new Expr.Literal(2))
            ), 
            new Token(TokenType.STAR, "*", null, 1), 
            new Expr.Grouping(
                new Expr.Binary(
                    new Expr.Literal(4), new Token(TokenType.MINUS, "-", null, 1), new Expr.Literal(3))
            )
        );

        System.out.println(new ReversePolishNotation().convert(expression));
    }
    
}
