package com.craftinginterpreters.lox;

import java.util.List;

import javax.management.RuntimeErrorException;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    private Environment environment = new Environment();

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement: statements) 
                execute(statement);
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }


    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
      Object value = null;
      if (stmt.initializer != null) {
        value = evaluate(stmt.initializer);
      }
  
      environment.define(stmt.name.lexeme, value);
      return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
      return environment.get(expr.name);
    }
  

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null; // since null = Void   
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
      Object value = evaluate(stmt.expression);
      System.out.println(stringify(value));
      return null;
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value; // We first converted a literal token to a syntax tree node, now we convert that
                           // node into a runtime type (object)
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double) right; // right must be an instanceof Double
        }

        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {

        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left - (double) right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                if ((double) right == 0.0)
                    throw new RuntimeError(expr.operator, "Division by zero.");
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double) left * (double) right;
            case PLUS:
                // allow "string" + 5 = "string5"
                if (left instanceof String) {
                    String rightStr = right.toString();
                    if (rightStr.endsWith(".0"))
                        rightStr = rightStr.substring(0, rightStr.length() - 2);
                    return left + rightStr;
                }

                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                throw new RuntimeError(expr.operator, "Operands for '+' must be two numbers or strings.");
        }

        return null;
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double)
            return;

        throw new RuntimeError(operator, "Operands for " + "'" + operator.lexeme + "'" + " must be numbers.");
    }

    private void checkNumberOperand(Token op, Object oper) {
        if (oper instanceof Double)
            return;
        throw new RuntimeError(op, "Operand for unary " + "'" + op.lexeme + "'" + " must be a number.");
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null)
            return true;
        if (a == null)
            return false;

        return a.equals(b);
    }

    private boolean isTruthy(Object object) {
        if (object == null)
            return false;
        if (object instanceof Boolean)
            return (boolean) object;
        return true;
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";
    
        if (object instanceof Double) {
          String text = object.toString();
          if (text.endsWith(".0")) {
            text = text.substring(0, text.length() - 2);
          }
          return text;
        }
    
        return object.toString();
      }
    
}
