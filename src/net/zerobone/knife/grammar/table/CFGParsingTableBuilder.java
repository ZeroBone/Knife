package net.zerobone.knife.grammar.table;

import net.zerobone.knife.grammar.Grammar;
import net.zerobone.knife.grammar.Production;

import java.util.ArrayList;

public class CFGParsingTableBuilder {

    private final Grammar grammar;

    private int[][] table;

    private int productionCounter = 1;

    private ArrayList<Production> productionActions = new ArrayList<>();

    public CFGParsingTableBuilder(final Grammar grammar) {

        this.grammar = grammar;

        table = new int[grammar.getNonTerminalCount()][grammar.getTerminalCount()];

    }

    public void write(int nonTerminal, int terminal, Production production) {

        // TODO: compress table by reusing already existing indices

        final int nonTerminalIndex = Grammar.nonTerminalToIndex(nonTerminal);
        final int terminalIndex = Grammar.terminalToIndex(terminal);

        table[nonTerminalIndex][terminalIndex] = productionCounter;

        productionActions.add(production);

        productionCounter++;

    }

    public ParsingTable getTable() {

        Production[] productionActionsArray = new Production[productionCounter - 1];

        productionActions.toArray(productionActionsArray);

        return new ParsingTable(grammar.getTerminalCount(), grammar.getNonTerminalCount(), productionActionsArray, table);

    }

}