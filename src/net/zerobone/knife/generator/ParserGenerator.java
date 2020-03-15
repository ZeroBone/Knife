package net.zerobone.knife.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.TypeSpec;
import net.zerobone.knife.grammar.CFGSymbol;
import net.zerobone.knife.grammar.table.CFGParsingTable;
import net.zerobone.knife.grammar.table.CFGParsingTableProduction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class ParserGenerator {

    private final CFGParsingTable table;

    public ParserGenerator(CFGParsingTable table) {
        this.table = table;
    }

    private void constructConstants(TypeSpec.Builder classBuilder) {

        {
            // T_EOF = 0
            FieldSpec field = FieldSpec.builder(int.class, "T_EOF")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", 0)
                .build();

            classBuilder.addField(field);
        }

        for (HashMap.Entry<String, Integer> entry : table.mapping.getSymbolToIdMap().entrySet()) {

            int id = entry.getValue();

            if (id > 0) {
                // this is a terminal

                FieldSpec field = FieldSpec.builder(int.class, "T_" + entry.getKey())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$L", id)
                    .build();

                classBuilder.addField(field);

            }

        }

        {
            // write terminal count

            FieldSpec field = FieldSpec.builder(int[].class, "terminalCount")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", table.mapping.terminalCount)
                .build();

            classBuilder.addField(field);

            field = FieldSpec.builder(int[].class, "nonTerminalCount")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", table.mapping.nonTerminalCount)
                .build();

            classBuilder.addField(field);

        }

    }

    private FieldSpec constructTable() {

        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (int y = 0; y < table.mapping.nonTerminalCount; y++) {

            sb.append('\n');

            for (int x = 0; x < table.mapping.terminalCount; x++) {

                sb.append(table.table[y][x]);

                if (x != table.mapping.terminalCount - 1 || y != table.mapping.nonTerminalCount - 1) {
                    sb.append(',');
                }

            }

        }

        sb.append('}');

        return FieldSpec.builder(int[].class, "table")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("$L", sb.toString())
            .build();

    }

    private FieldSpec constructActionTable() {

        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (CFGParsingTableProduction productionAction : table.productionActions) {

            sb.append('\n');

            sb.append('{');

            int nonTerminalIndex = table.mapping.nonTerminalToIndex(productionAction.label);

            sb.append(nonTerminalIndex);

            for (CFGSymbol symbol : productionAction.body) {

                sb.append(',');

                int id = table.mapping.map(symbol.id);

                sb.append(id);

            }

            sb.append('}');
            sb.append(',');

        }

        sb.deleteCharAt(sb.length() - 1);

        sb.append('}');

        return FieldSpec.builder(int[][].class, "actionTable")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("$L", sb.toString())
            .build();

    }

    private MethodSpec constructParseMethod() {

        return MethodSpec.methodBuilder("parse")
            .addModifiers(Modifier.PUBLIC)
            .returns(void.class)
            .addParameter(int.class, "tokenId")
            .addParameter(Object.class, "token")
            .addStatement("$T.out.println($S)", System.class, "Hello, ZeroBone!")
            .build();

    }

    public void generate() throws IOException {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("Parser")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        // fields

        constructConstants(classBuilder);

        classBuilder.addField(constructTable());
        classBuilder.addField(constructActionTable());

        // methods

        TypeSpec helloWorld = classBuilder
            .addMethod(constructParseMethod())
            .build();

        // write to file

        JavaFile javaFile = JavaFile
            .builder("net.zerobone.knife.parser", helloWorld)
            .build();

        BufferedWriter mainClassWriter = new BufferedWriter(new FileWriter("Parser.java"));

        javaFile.writeTo(mainClassWriter);

        mainClassWriter.close();

    }

}