package net.zerobone.knife.generator;

import com.squareup.javapoet.*;
import net.zerobone.knife.grammar.CFGSymbol;
import net.zerobone.knife.grammar.table.CFGParsingTableProduction;

import javax.lang.model.element.Modifier;
import java.util.HashMap;

class ParseTreeNodeGenerator {

    private ParseTreeNodeGenerator() {}

    private static MethodSpec constructReduceMethod(GeneratorContext context) {

        MethodSpec.Builder b = MethodSpec.methodBuilder("reduce");

        b.returns(void.class);

        b.addStatement("System.out.println(\"Reducing action + \" + actionId + \".\")");

        b.addStatement("Object v");

        b.beginControlFlow("switch (actionId - 1)");

        for (int i = 0; i < context.table.productionActions.length; i++) {

            final CFGParsingTableProduction production = context.table.productionActions[i];

            if (production.code == null) {
                b.addStatement("case $L: break", i);
                continue;
            }

            b.addCode("case $L:\n$>", i);
            b.addCode("{$>\n");

            for (int j = 0; j < production.body.size(); j++) {

                CFGSymbol symbol = production.body.get(j);

                if (symbol.argumentName == null) {
                    continue;
                }

                int childIndex = production.body.size() - 1 - j;

                if (symbol.isTerminal) {
                    b.addStatement("Object " + symbol.argumentName + " = ((ParseTreeTerminalNode)children.get(" + childIndex + ")).terminal");
                }
                else {
                    b.addStatement("Object " + symbol.argumentName + " = ((ParseTreeNode)children.get(" + childIndex + ")).payload");
                }

            }

            // b.addComment("--- user code begin ---");

            b.addCode(production.code);
            b.addCode("\n");

            // b.addComment("--- user code end ---");

            b.addCode("$<}");
            b.addCode("\n");
            b.addStatement("break$<");

        }

        b.addCode("default:\n$>");
        b.addStatement("throw new IllegalStateException()$<");

        b.endControlFlow();

        b.addStatement("payload = v");
        b.addStatement("children = new ArrayList<>()");

        return b.build();

    }

    private static MethodSpec constructConstructor() {

        return MethodSpec.constructorBuilder().build();

    }

    static TypeSpec.Builder generate(GeneratorContext context) {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("ParseTreeNode")
            .addModifiers(Modifier.FINAL);

        // methods

        classBuilder.addField(
            FieldSpec
                .builder(int.class, "actionId")
                .initializer("$L", 0)
                .build()
        );

        classBuilder.addField(
            FieldSpec.builder(boolean.class, "isParent")
                .initializer("$L", false)
                .build()
        );

        classBuilder.addField(
            FieldSpec.builder(Object.class, "payload")
                .initializer("null")
                .build()
        );

        final ClassName arrayList = ClassName.get("java.util", "ArrayList");
        final ClassName nodesClassName = ClassName.get("java.lang", "Object");

        classBuilder.addField(
            FieldSpec.builder(ParameterizedTypeName.get(arrayList, nodesClassName), "children")
                .initializer("new $T<>()", arrayList)
                .build()
        );

        classBuilder.addMethod(constructConstructor());
        classBuilder.addMethod(constructReduceMethod(context));

        return classBuilder;

    }

}