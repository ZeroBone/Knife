package net.zerobone.knife.generator;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;

class ParserGenerator {

    private ParserGenerator() {}

    private static void constructParserFields(GeneratorContext context, TypeSpec.Builder classBuilder) {

        // stack type
        final ClassName stack = ClassName.get("java.util", "Stack");
        final ClassName parseNode = ClassName.get(context.packageName, "ParseNode");

        TypeName stackType = ParameterizedTypeName.get(
            stack,
            parseNode
        );

        // error arraylist ty[e

        final ClassName arrayList = ClassName.get("java.util", "ArrayList");
        final ClassName parseError = ClassName.get(context.packageName, "ParseError");
        TypeName errorsType = ParameterizedTypeName.get(
            arrayList,
            parseError
        );

        {
            FieldSpec field = FieldSpec
                .builder(stackType, "stack")
                .addModifiers(Modifier.PRIVATE)
                .build();

            classBuilder.addField(field);
        }

        {
            FieldSpec field = FieldSpec.builder(errorsType, "errors")
                .addModifiers(Modifier.PRIVATE)
                .build();

            classBuilder.addField(field);
        }

        {
            FieldSpec field = FieldSpec.builder(boolean.class, "reachedEof")
                .addModifiers(Modifier.PRIVATE)
                .build();

            classBuilder.addField(field);
        }

        {
            FieldSpec field = FieldSpec.builder(parseNode, "parseTree")
                .addModifiers(Modifier.PRIVATE)
                .build();

            classBuilder.addField(field);
        }

    }

    private static MethodSpec constructParseMethod(GeneratorContext context) {

        MethodSpec.Builder b = MethodSpec.methodBuilder("parse");

        b.addModifiers(Modifier.PUBLIC);
        b.returns(void.class);
        b.addParameter(int.class, "tokenId");
        b.addParameter(context.getTokenTypeName(), "token");

        // method body

        b.beginControlFlow("while (true)");

        b.addStatement("ParseNode prevRoot = stack.peek()");

        // inner loop
        b.beginControlFlow("while (prevRoot.actionId != 0)");

        b.beginControlFlow("if (errors.isEmpty())");
        b.addStatement("prevRoot.reduce()");
        b.nextControlFlow("else");
        b.addStatement("prevRoot.children = null");
        b.endControlFlow();


        b.addStatement("stack.pop()");
        b.beginControlFlow("if (stack.isEmpty())");
        b.beginControlFlow("if (tokenId != T_EOF)");
        b.addStatement("errors.add(new ParseError(T_EOF, tokenId, token))");
        b.addStatement("return");
        b.endControlFlow();
        b.addStatement("reachedEof = true");
        b.addStatement("return");
        b.endControlFlow();

        b.addStatement("prevRoot = stack.peek()");

        b.endControlFlow(); // end of inner loop

        // prevRoot is not a parent node

        b.beginControlFlow("if (prevRoot.symbolId > 0)");
        // if the current symbol is a terminal

        b.beginControlFlow("if (tokenId != prevRoot.symbolId)");
        b.addStatement("stack.pop()");
        b.addStatement("errors.add(new ParseError(prevRoot.symbolId, tokenId, token))");
        b.addStatement("return");
        b.endControlFlow();

        // tokens match
        b.addStatement("prevRoot.payload = token");
        b.addStatement("stack.pop()");
        b.addStatement("return");

        b.endControlFlow();

        // now handle non-terminals

        b.addStatement("int actionId = table[(-prevRoot.symbolId - 1) * terminalCount + tokenId]");

        // empty entry in the table
        b.beginControlFlow("if (actionId == 0)");
        b.addStatement("errors.add(new ParseError(ParseError.ANY, tokenId, token))");
        b.addStatement("return");
        b.endControlFlow();

        // synchronize entry in the table
        b.beginControlFlow("if (actionId == -1)");
        b.addStatement("errors.add(new ParseError(ParseError.ANY, tokenId, token))");
        b.addStatement("stack.pop()");
        b.addStatement("return");
        b.endControlFlow();

        b.addStatement("int[] action = actionTable[actionId - 1]");
        b.addStatement("prevRoot.actionId = actionId");

        b.beginControlFlow("for (int i = action.length - 1; i >= 0; i--)"); // inner loop over action

        b.addStatement("ParseNode child = new ParseNode(action[i])");
        b.addStatement("prevRoot.children.add(child)");
        b.addStatement("stack.push(child)");

        b.endControlFlow(); // end of inner loop over action

        b.endControlFlow(); // end of outer loop

        return b.build();

    }

    private static MethodSpec constructResetMethod() {

        MethodSpec.Builder b = MethodSpec.methodBuilder("reset");

        b.addModifiers(Modifier.PUBLIC);
        b.returns(void.class);

        // method body

        b.addStatement("errors = new ArrayList<>()");
        b.addStatement("reachedEof = false");
        b.addStatement("parseTree = new ParseNode(startSymbol)");
        b.addStatement("stack = new Stack<>()");
        b.addStatement("stack.push(parseTree)");

        return b.build();

    }

    private static MethodSpec constructGetValueMethod() {

        MethodSpec.Builder b = MethodSpec.methodBuilder("getValue");

        b.addModifiers(Modifier.PUBLIC);
        b.returns(Object.class);

        b.addStatement("assert successfullyParsed()");

        b.addStatement("return parseTree.payload");

        return b.build();

    }

    private static MethodSpec constructGetErrorsMethod(GeneratorContext context) {

        final ClassName parseError = ClassName.get(context.packageName, "ParseError");

        MethodSpec.Builder b = MethodSpec.methodBuilder("getErrors");

        b.addModifiers(Modifier.PUBLIC);
        b.returns(ArrayTypeName.of(parseError));

        b.addStatement("ParseError[] parseErrors = new ParseError[errors.size()]");
        b.addStatement("errors.toArray(parseErrors)");
        b.addStatement("return parseErrors");

        return b.build();

    }

    static TypeSpec.Builder generate(GeneratorContext context) {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("Parser")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        // fields

        MetaGenerator.constructConstants(context.table, classBuilder);

        classBuilder.addField(MetaGenerator.constructTable(context.table));
        classBuilder.addField(MetaGenerator.constructActionTable(context.table));

        constructParserFields(context, classBuilder);

        // methods

        classBuilder.addMethod(
            MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("reset()")
                .build()
        );

        classBuilder.addMethod(constructParseMethod(context));

        classBuilder.addMethod(constructResetMethod());
        classBuilder.addMethod(constructGetValueMethod());
        classBuilder.addMethod(constructGetErrorsMethod(context));

        classBuilder.addMethod(
            MethodSpec
                .methodBuilder("successfullyParsed")
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return reachedEof && errors.isEmpty()")
                .build()
        );

        return classBuilder;

    }

}