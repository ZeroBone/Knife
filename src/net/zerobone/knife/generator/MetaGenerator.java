package net.zerobone.knife.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import net.zerobone.knife.grammar.Production;
import net.zerobone.knife.grammar.Symbol;

import javax.lang.model.element.Modifier;
import java.util.HashMap;

class MetaGenerator {

    private MetaGenerator() {}

    static void constructConstants(GeneratorContext context, TypeSpec.Builder classBuilder) {

        {
            // T_EOF = 0
            FieldSpec field = FieldSpec.builder(int.class, "T_EOF")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", 0)
                .build();

            classBuilder.addField(field);
        }

        for (HashMap.Entry<String, Integer> entry : context.grammar.getSymbolToIdMap().entrySet()) {

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
                .initializer("$L", context.table.terminalCount)
                .build();

            classBuilder.addField(field);

            field = FieldSpec.builder(int.class, "nonTerminalCount")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", context.table.nonTerminalCount)
                .build();

            classBuilder.addField(field);

            field = FieldSpec.builder(int.class, "startSymbol")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", context.grammar.symbolToId(context.grammar.startSymbol)) // TODO: cleanup
                .build();

            classBuilder.addField(field);

        }

    }

    static FieldSpec constructTable(GeneratorContext context) {

        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (int y = 0; y < context.table.nonTerminalCount; y++) {

            sb.append('\n');

            for (int x = 0; x < context.table.terminalCount; x++) {

                sb.append(context.table.table[y][x]);

                if (x != context.table.terminalCount - 1 || y != context.table.nonTerminalCount - 1) {
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

    static FieldSpec constructActionTable(GeneratorContext context) {

        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (Production productionAction : context.table.productionActions) {

            sb.append('\n');

            sb.append('{');

            for (Symbol symbol : productionAction.getBody()) {

                sb.append(symbol.id);

                sb.append(',');

            }

            if (!productionAction.getBody().isEmpty()) {
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