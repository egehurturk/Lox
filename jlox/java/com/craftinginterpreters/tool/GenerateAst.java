//> Representing Code generate-ast
package com.craftinginterpreters.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: generate_ast <output directory>");
			System.exit(64);
		}

		String outputDir = args[0]; 

		// class_name : fields
		List<String> subclassDesc = Arrays.asList(
			"Binary   : Expr left, Token operator, Expr right",
      		"Grouping : Expr expression",
      		"Literal  : Object value",
      		"Unary    : Token operator, Expr right",
			"Variable : Token name"
		);


		List<String> stmtSubClassDesc = Arrays.asList(
			"Expression : Expr expression",
			"Print      : Expr expression",
			"Var        : Token name, Expr initializer"	
		);

		defineAst(outputDir, "Expr", subclassDesc);
		defineAst(outputDir, "Stmt", stmtSubClassDesc);
    }    

	private static void defineAst(String outputDir, String baseName, List<String> subclassDesc) throws IOException {
		String fullPath = outputDir + "/" + baseName + ".java";
		PrintWriter writer = new PrintWriter(fullPath, "UTF-8");

		writer.println("package com.craftinginterpreters.lox;");
		writer.println();
		writer.println("import java.util.List;");
		writer.println();
		writer.println("public abstract class " + baseName + " {");

		writer.println();
		writer.println("\tpublic abstract <R> R accept(Visitor<R> visitor);");

		// Define the visitor interface
		defineVisitor(writer, baseName, subclassDesc);
		
		for (String subclass: subclassDesc) {
			String subclassName = subclass.split(":")[0].trim();
			String csvFields = subclass.split(":")[1].trim(); 
			defineSubClass(writer, baseName, subclassName, csvFields);
		}

		writer.println("}");
		writer.close();
	}

	private static void defineSubClass(PrintWriter writer, String baseName, String subclassName, String csvFields) throws IOException {
		writer.println();
		writer.println("\tpublic static class " + subclassName + " extends " + baseName + " {");

		writer.println();

		String[] fields = csvFields.split(", ");

    	for (String field : fields) {
      		writer.println("\t\tpublic final " + field + ";");
    	}

		writer.println();

		// Define the constructor
		writer.println("\t\tpublic " + subclassName + "(" + csvFields + ") {");

		
		for (String field: fields) {
			String fieldName = field.split(" ")[1];
			writer.println("\t\t\tthis." + fieldName + " = " + fieldName + ";");
		}

		writer.println("\t\t}");

		writer.println();
		writer.println("\t\t@Override");
		writer.println("\t\tpublic <R> R accept(Visitor<R> visitor) {");
		writer.println("\t\t\treturn visitor.visit" +
			subclassName + baseName + "(this);");
		writer.println("\t\t}");

		writer.println("\t}");
	}

	private static void defineVisitor(PrintWriter writer, String baseName, List<String> subclassDesc) {
		writer.println();
		writer.println("\tpublic interface Visitor<R> {");

		for (String subclass: subclassDesc) {
			String typeName = subclass.split(":")[0].trim();
			writer.println("\t\tpublic R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
		}

		writer.println("\t}");
		writer.println();
	}
}