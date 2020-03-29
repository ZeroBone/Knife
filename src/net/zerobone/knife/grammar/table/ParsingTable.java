package net.zerobone.knife.grammar.table;

import net.zerobone.knife.utils.BijectiveMap;

public class ParsingTable {

    public static final int SYNCHRONIZE = -1;

    public final BijectiveMap<String, Integer> mapping;

    public final int nonTerminalCount;

    public final int terminalCount;

    public final ParsingTableProduction[] productionActions;

    public final int[][] table;

    public final String startSymbol;

    public final ParsingTableConflict[] conflicts;

    public ParsingTable(BijectiveMap<String, Integer> mapping, int nonTerminalCount, int terminalCount, ParsingTableProduction[] productionActions, int[][] table, String startSymbol, ParsingTableConflict[] conflicts) {
        this.mapping = mapping;
        this.nonTerminalCount = nonTerminalCount;
        this.terminalCount = terminalCount;
        this.productionActions = productionActions;
        this.table = table;
        this.startSymbol = startSymbol;
        this.conflicts = conflicts;
    }

    public String idToSymbol(int id) {
        return mapping.mapValue(id);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Terminal count: ");
        sb.append(terminalCount);
        sb.append('\n');

        sb.append("Nonterminal count: ");
        sb.append(nonTerminalCount);
        sb.append('\n');
        sb.append('\n');

        // calculate width
        int width = 10;

        for (int y = 0; y < nonTerminalCount; y++) {

            String nonTerminalSymbol = idToSymbol(-y - 1);

            if (nonTerminalSymbol.length() > width) {
                width = nonTerminalSymbol.length();
            }

        }

        // write header

        sb.append(String.format("%"+(width + 1)+"s   ", "LL(1) TABLE"));

        for (int x = 0; x < terminalCount; x++) {

            sb.append(String.format("%11s", x == 0 ? "$" : idToSymbol(x)));
            sb.append(' ');
            sb.append(' ');

        }

        sb.append('\n');

        // write table body

        for (int y = 0; y < nonTerminalCount; y++) {

            sb.append(String.format("%"+width+"s", idToSymbol(-y - 1)));

            sb.append(" | ");

            for (int x = 0; x < terminalCount; x++) {

                sb.append(String.format("%12d", table[y][x]));
                sb.append(' ');

            }

            sb.append('\n');

        }

        sb.append('\n');
        sb.append("Production actions: \n");

        for (int i = 0; i < productionActions.length; i++) {

            sb.append(i + 1);
            sb.append(": ");
            sb.append(productionActions[i]);
            sb.append('\n');

        }

        return sb.toString();

    }

}