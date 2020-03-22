package net.zerobone.knife.grammar.table;

import net.zerobone.knife.grammar.SymbolMapping;

import java.util.ArrayList;

public class ParsingTableBuilder {

    private final SymbolMapping mapping;

    private int[][] table;

    private int productionCounter = 1;

    private ArrayList<ParsingTableProduction> productionActions = new ArrayList<>();

    public ParsingTableBuilder(final SymbolMapping mapping) {

        this.mapping = mapping;

        table = new int[mapping.nonTerminalCount][mapping.terminalCount];

    }

    public void write(String nonTerminal, String terminal, ParsingTableProduction production) {

        // TODO: compress table by reusing already existing indices

        final int nonTerminalIndex = mapping.nonTerminalToIndex(nonTerminal);
        final int terminalIndex = mapping.terminalToIndex(terminal);

        table[nonTerminalIndex][terminalIndex] = productionCounter;

        productionActions.add(production);

        productionCounter++;

    }

    public ParsingTable getTable() {

        ParsingTableProduction[] productionActionsArray = new ParsingTableProduction[productionCounter - 1];

        productionActions.toArray(productionActionsArray);

        return new ParsingTable(mapping, productionActionsArray, table);

    }

}