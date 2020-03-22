package net.zerobone.knife.grammar.table;

import net.zerobone.knife.grammar.Production;
import net.zerobone.knife.grammar.symbol.SymbolGrammar;

public class ParsingTable {

    public final int terminalCount;

    public final int nonTerminalCount;

    public final Production[] productionActions;

    public final int[][] table;

    public ParsingTable(int terminalCount, int nonTerminalCount, Production[] productionActions, int[][] table) {
        this.terminalCount = terminalCount;
        this.nonTerminalCount = nonTerminalCount;
        this.productionActions = productionActions;
        this.table = table;
    }

    public String toString(SymbolGrammar grammar) {

        StringBuilder sb = new StringBuilder();

        sb.append("Terminal count: ");
        sb.append(terminalCount);
        sb.append('\n');

        sb.append("Nonterminal count: ");
        sb.append(nonTerminalCount);
        sb.append('\n');
        sb.append('\n');

        sb.append(String.format("%12s ", "LL(1) TABLE"));

        for (int x = 0; x < terminalCount; x++) {

            sb.append(String.format("%12s", x == 0 ? "$" : grammar.idToSymbol(x)));
            sb.append(' ');

        }

        sb.append('\n');

        for (int y = 0; y < nonTerminalCount; y++) {

            sb.append(String.format("%10s", grammar.idToSymbol(-y - 1)));

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