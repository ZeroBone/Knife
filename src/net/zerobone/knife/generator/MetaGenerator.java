package net.zerobone.knife.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import net.zerobone.knife.grammar.CFGSymbol;
import net.zerobone.knife.grammar.table.CFGParsingTable;
import net.zerobone.knife.grammar.table.CFGParsingTableProduction;

import javax.lang.model.element.Modifier;
import java.util.HashMap;

class MetaGenerator {

    private MetaGenerator() {}

    static void constructConstants(CFGParsingTable table, TypeSpec.Builder classBuilder) {

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

            FieldSpec field = FieldSpec.builder(int.class, "terminalCount")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", table.mapping.terminalCount)
                .build();

            classBuilder.addField(field);

            field = FieldSpec.builder(int.class, "nonTerminalCount")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", table.mapping.nonTerminalCount)
                .build();

            classBuilder.addField(field);

            field = FieldSpec.builder(int.class, "startSymbol")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", table.mapping.map(table.mapping.startNonTerminalId))
                .build();

            classBuilder.addField(field);

        }

    }

    static FieldSpec constructTable(CFGParsingTable table) {

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

    static FieldSpec constructActionTable(CFGParsingTable table) {

        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (CFGParsingTableProduction productionAction : table.productionActions) {

            sb.append('\n');

            sb.append('{');

            for (CFGSymbol symbol : productionAction.body) {

                int id = table.mapping.map(symbol.id);

                sb.append(id);

                sb.append(',');

            }

            if (!productionAction.body.isEmpty()) {
                sb.deleteCharAt(sb.length() - 1);
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

}