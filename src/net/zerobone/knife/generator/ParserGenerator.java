package net.zerobone.knife.generator;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;

class ParserGenerator {

    private ParserGenerator() {}

    private static void constructParserFields(TypeSpec.Builder classBuilder) {

        ClassName stack = ClassName.get("java.util", "Stack");

        TypeName stackType = ParameterizedTypeName.get(
            stack,
            ClassName.get("java.lang", "Integer")
        );

        TypeName treeStackType = ParameterizedTypeName.get(
            stack,
            ClassName.get("java.lang", "Object")
        );

        {
            FieldSpec field = FieldSpec.builder(stackType, "stack")
                .addModifiers(Modifier.PRIVATE)
                .build();

            classBuilder.addField(field);
        }

        {
            FieldSpec field = FieldSpec.builder(treeStackType, "treeStack")
                .addModifiers(Modifier.PRIVATE)
                .build();

            classBuilder.addField(field);
        }

        {
            FieldSpec field = FieldSpec.builder(boolean.class, "success")
                .addModifiers(Modifier.PRIVATE)
                .initializer("$L", false)
                .build();

            classBuilder.addField(field);
        }

        {
            FieldSpec field = FieldSpec.builder(Object.class, "parseTree")
                .addModifiers(Modifier.PRIVATE)
                .build();

            classBuilder.addField(field);
        }

    }

    private static void inlineTreeParseEof(MethodSpec.Builder b) {

        b.beginControlFlow("while (!treeStack.isEmpty())");
        b.addStatement("((ParseTreeNode)treeStack.pop()).reduce()");
        b.endControlFlow();

    }

    private static void inlineTreeMatchToken(MethodSpec.Builder b, String argumentName) {

        b.addStatement("((ParseTreeTerminalNode)treeStack.peek()).terminal = " + argumentName);
        b.addStatement("treeStack.pop()");

    }

    private static void inlineTreeApplyAction(MethodSpec.Builder b, String argumentName) {

        b.addStatement("ParseTreeNode prevRoot = (ParseTreeNode)treeStack.peek()");

        b.beginControlFlow("while (prevRoot.isParent)");
        b.addStatement("prevRoot.isParent = false");
        b.addStatement("prevRoot.reduce()");
        b.addStatement("treeStack.pop()");
        b.addStatement("prevRoot = (ParseTreeNode)treeStack.peek()");
        b.endControlFlow();

        b.addStatement("prevRoot.isParent = true");

        // action iteration loop
        b.beginControlFlow("for (int i = " + argumentName + ".length - 1; i >= 0; i--)");
        b.addStatement("Object child = " + argumentName + "[i] < 0 ? new ParseTreeNode(" + argumentName + "[i]) : new ParseTreeTerminalNode()");
        b.addStatement("prevRoot.children.add(child)");
        b.addStatement("treeStack.push(child)");
        b.endControlFlow();

    }

    private static MethodSpec constructParseMethod(GeneratorContext context) {

        MethodSpec.Builder b = MethodSpec.methodBuilder("parse");

        b.addModifiers(Modifier.PUBLIC);
        b.returns(void.class);
        b.addParameter(int.class, "tokenId");
        b.addParameter(context.getTokenTypeName(), "token");

        b.beginControlFlow("while (true)");

        b.beginControlFlow("if (stack.isEmpty())");
        b.beginControlFlow("if (tokenId == T_EOF)");

        // call treeParseEof()
        inlineTreeParseEof(b);

        b.addStatement("success = true");
        b.addStatement("return");
        b.endControlFlow();

        b.addStatement("throw new RuntimeException(\"Expected end of input. Got: \" + tokenId)");

        b.endControlFlow();

        // handle non-eof state

        b.addStatement("int top = stack.peek()");

        // if top is a terminal

        b.beginControlFlow("if (top > 0)");

        b.beginControlFlow("if (tokenId != top)");
        b.addStatement("throw new RuntimeException(\"Expected: \" + top + \" Got: \" + tokenId)");
        b.endControlFlow();

        // pop both the stack as well a terminal from the the input buffer
        b.addStatement("stack.pop()");

        // call treeMatchToken(token)
        inlineTreeMatchToken(b, "token");

        b.addStatement("return");

        b.endControlFlow();

        // if top is not a terminal
        // perform action

        b.addStatement("int actionId = table[(-top - 1) * terminalCount + tokenId]");

        b.beginControlFlow("if (actionId == 0)");
        b.addStatement("throw new RuntimeException(\"Syntax error. Token: \" + token)");
        b.endControlFlow();

        b.addStatement("int[] action = actionTable[actionId - 1]");

        // call treeApplyAction(action);
        inlineTreeApplyAction(b, "action");

        b.addStatement("stack.pop()");

        b.beginControlFlow("for (int i = action.length - 1; i >= 0; i--)");
        b.addStatement("stack.push(action[i])");
        b.endControlFlow();

        b.endControlFlow(); // end of while

        return b.build();

    }

    private static void inlineTreeReset(MethodSpec.Builder b) {

        b.addStatement("treeStack = new Stack<>()");
        b.addStatement("parseTree = new ParseTreeNode(startSymbol)");
        b.addStatement("treeStack.push(parseTree)");

    }

    private static MethodSpec constructResetMethod() {

        MethodSpec.Builder b = MethodSpec.methodBuilder("reset");

        b.addModifiers(Modifier.PUBLIC);
        b.returns(void.class);

        b.addStatement("success = false");
        b.addStatement("stack = new Stack<>()");
        b.addStatement("stack.push(startSymbol)");

        inlineTreeReset(b);

        return b.build();

    }

    private static MethodSpec constructGetValueMethod() {

        MethodSpec.Builder b = MethodSpec.methodBuilder("getValue");

        b.addModifiers(Modifier.PUBLIC);
        b.returns(Object.class);

        b.beginControlFlow("if (!success)");
        b.addStatement("return null");
        b.endControlFlow();

        b.addStatement("return parseTree");

        return b.build();

    }

    static TypeSpec.Builder generate(GeneratorContext context) {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("Parser")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        // fields

        MetaGenerator.constructConstants(context.table, classBuilder);

        classBuilder.addField(MetaGenerator.constructTable(context.table));
        classBuilder.addField(MetaGenerator.constructActionTable(context.table));

        constructParserFields(classBuilder);

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

        classBuilder.addMethod(
            MethodSpec
                .methodBuilder("successfullyParsed")
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return success")
                .build()
        );

        return classBuilder;

    }

}