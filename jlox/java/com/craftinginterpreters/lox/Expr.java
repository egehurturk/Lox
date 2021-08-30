package com.craftinginterpreters.lox;

import java.util.List;

public abstract class Expr {

	public abstract <R> R accept(Visitor<R> visitor);

	public interface Visitor<R> {
		public R visitBinaryExpr(Binary expr);
		public R visitGroupingExpr(Grouping expr);
		public R visitLiteralExpr(Literal expr);
		public R visitUnaryExpr(Unary expr);
	}


	public static class Binary extends Expr {

		public final Expr left;
		public final Token operator;
		public final Expr right;

		public Binary(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitBinaryExpr(this);
		}

		@Override
		public String toString() {
			return left.toString() + " " + operator.lexeme + " " + right.toString();
		}
	}

	public static class Grouping extends Expr {

		public final Expr expression;

		public Grouping(Expr expression) {
			this.expression = expression;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitGroupingExpr(this);
		}

		@Override
		public String toString() {
			return "(" + expression.toString() + ")";
		}
	}

	public static class Literal extends Expr {

		public final Object value;

		public Literal(Object value) {
			this.value = value;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitLiteralExpr(this);
		}

		@Override
		public String toString() {
			return value.toString();
		}
	}

	public static class Unary extends Expr {

		public final Token operator;
		public final Expr right;

		public Unary(Token operator, Expr right) {
			this.operator = operator;
			this.right = right;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitUnaryExpr(this);
		}

		@Override
		public String toString() {
			return operator.lexeme + right.toString();
		}
	}
}
