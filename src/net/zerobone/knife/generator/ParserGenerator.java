package net.zerobone.knife.generator;

import com.squareup.javapoet.MethodSpec;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.TypeSpec;
import net.zerobone.knife.grammar.table.CFGParsingTable;

class ParserGenerator {

    private ParserGenerator() {}

    private static MethodSpec constructParseMethod(CFGParsingTable table) {

        return MethodSpec.methodBuilder("parse")
            .addModifiers(Modifier.PUBLIC)
            .returns(void.class)
            .addParameter(int.class, "tokenId")
            .addParameter(Object.class, "token")
            .addStatement("$T.out.println($S)", System.class, "lol!!!")
            .build();

    }

    static TypeSpec.Builder generate(CFGParsingTable table) {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("Parser")
            .addModifiers(Modifier.FINAL);

        // fields

        MetaGenerator.constructConstants(table, classBuilder);

        classBuilder.addField(MetaGenerator.constructTable(table));
        classBuilder.addField(MetaGenerator.constructActionTable(table));

        // methods

        classBuilder.addMethod(constructParseMethod(table));

        return classBuilder;

    }

}