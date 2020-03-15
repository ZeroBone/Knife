package net.zerobone.knife.grammar;

import java.util.HashMap;

public class CFGParsingTable {

    public final CFGSymbolMapping mapping;

    public final CFGParsingTableProduction[] productions;

    public final int[][] table;

    public CFGParsingTable(CFGSymbolMapping mapping, CFGParsingTableProduction[] productions, int[][] table) {
        this.mapping = mapping;
        this.productions = productions;
        this.table = table;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Terminal count: ");
        sb.append(mapping.terminalCount);
        sb.append('\n');

        sb.append("Nonterminal count: ");
        sb.append(mapping.nonTerminalCount);
        sb.append('\n');
        sb.append('\n');

        HashMap<Integer, String> debugReverseMapping = mapping.getDebugReverseMapping();

        sb.append(String.format("%12s ", "LL(1) TABLE"));

        for (int x = 0; x < mapping.terminalCount; x++) {

            sb.append(String.format("%12s", x == 0 ? "$" : debugReverseMapping.get(x)));
            sb.append(' ');

        }

        sb.append('\n');

        for (int y = 0; y < mapping.nonTerminalCount; y++) {

            sb.append(String.format("%10s", debugReverseMapping.get(-y - 1)));

            sb.append(" | ");

            for (int x = 0; x < mapping.terminalCount; x++) {

                sb.append(String.format("%12d", table[y][x]));
                sb.append(' ');

            }

            sb.append('\n');

        }

        sb.append('\n');
        sb.append("Production actions: \n");

        for (int i = 0; i < productions.length; i++) {

            sb.append(i + 1);
            sb.append(": ");
            sb.append(productions[i]);
            sb.append('\n');

        }

        return sb.toString();

    }

}