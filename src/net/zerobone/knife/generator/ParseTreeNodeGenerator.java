package net.zerobone.knife.generator;

import com.squareup.javapoet.*;
import net.zerobone.knife.grammar.table.CFGParsingTable;

import javax.lang.model.element.Modifier;

class ParseTreeNodeGenerator {

    private ParseTreeNodeGenerator() {}

    private static MethodSpec constructReduceMethod(CFGParsingTable table) {

        return MethodSpec.methodBuilder("reduce")
            .returns(void.class)
            .addStatement("$T.out.println($S)", System.class, "optimizing...")
            .build();

    }

    private static MethodSpec constructConstructor() {

        return MethodSpec.constructorBuilder()
            .addParameter(int.class, "nonTerminal")
            .addStatement("this.nonTerminal = nonTerminal")
            .build();

    }

    static TypeSpec.Builder generate(CFGParsingTable table) {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("ParseTreeNode")
            .addModifiers(Modifier.FINAL);

        // methods

        classBuilder.addField(
            FieldSpec.builder(int.class, "nonTerminal")
                .addModifiers(Modifier.FINAL)
                .build()
        );

        classBuilder.addField(
            FieldSpec.builder(boolean.class, "isParent")
                .addModifiers(Modifier.FINAL)
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
        classBuilder.addMethod(constructReduceMethod(table));

        return classBuilder;

    }

}