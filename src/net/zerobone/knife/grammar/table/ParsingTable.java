package net.zerobone.knife.grammar.table;

import net.zerobone.knife.grammar.SymbolMapping;

public class ParsingTable {

    public final SymbolMapping mapping;

    public final ParsingTableProduction[] productionActions;

    public final int[][] table;

    public ParsingTable(SymbolMapping mapping, ParsingTableProduction[] productionActions, int[][] table) {
        this.mapping = mapping;
        this.productionActions = productionActions;
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

        sb.append(String.format("%12s ", "LL(1) TABLE"));

        for (int x = 0; x < mapping.terminalCount; x++) {

            sb.append(String.format("%12s", x == 0 ? "$" : mapping.idToSymbol(x)));
            sb.append(' ');

        }

        sb.append('\n');

        for (int y = 0; y < mapping.nonTerminalCount; y++) {

            sb.append(String.format("%10s", mapping.idToSymbol(-y - 1)));

            sb.append(" | ");

            for (int x = 0; x < mapping.terminalCount; x++) {

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