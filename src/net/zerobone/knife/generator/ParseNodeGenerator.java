package net.zerobone.knife.generator;

import com.squareup.javapoet.*;
import net.zerobone.knife.grammar.Symbol;
import net.zerobone.knife.grammar.table.ParsingTableProduction;

import javax.lang.model.element.Modifier;

class ParseNodeGenerator {

    private ParseNodeGenerator() {}

    private static MethodSpec constructReduceMethod(GeneratorContext context) {

        MethodSpec.Builder b = MethodSpec.methodBuilder("reduce");

        b.returns(void.class);

        // b.addStatement("System.out.println(\"Reducing action + \" + actionId + \".\")");

        b.addStatement("Object v");

        b.beginControlFlow("switch (actionId - 1)");

        for (int i = 0; i < context.table.productionActions.length; i++) {

            final ParsingTableProduction production = context.table.productionActions[i];

            if (production.code == null) {
                b.addStatement("case $L: break", i);
                continue;
            }

            b.addCode("case $L:\n$>", i);
            b.addCode("{$>\n");

            for (int j = 0; j < production.body.size(); j++) {

                Symbol symbol = production.body.get(j);

                if (symbol.argumentName == null) {
                    continue;
                }

                int childIndex = production.body.size() - 1 - j;

                b.addStatement("Object " + symbol.argumentName + " = ((ParseNode)children.get(" + childIndex + ")).payload");

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
        b.addStatement("children = null");

        return b.build();

    }

    private static MethodSpec constructConstructor() {

        MethodSpec.Builder b = MethodSpec.constructorBuilder();

        b.addParameter(int.class, "symbolId");

        b.addStatement("this.symbolId = symbolId");
        b.addStatement("children = this.symbolId < 0 ? new ArrayList<>() : null");

        return b.build();

    }

    static TypeSpec.Builder generate(GeneratorContext context) {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("ParseNode")
            .addModifiers(Modifier.FINAL);

        // methods

        classBuilder.addField(
            FieldSpec
                .builder(int.class, "actionId")
                .initializer("0")
                .build()
        );

        classBuilder.addField(
            FieldSpec.builder(int.class, "symbolId")
                .addModifiers(Modifier.FINAL)
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
            FieldSpec
                .builder(ParameterizedTypeName.get(arrayList, nodesClassName), "children")
                .build()
        );

        classBuilder.addMethod(constructConstructor());
        classBuilder.addMethod(constructReduceMethod(context));

        return classBuilder;

    }

}