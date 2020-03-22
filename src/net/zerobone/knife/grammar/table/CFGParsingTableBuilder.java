package net.zerobone.knife.grammar.table;

import net.zerobone.knife.grammar.CFGSymbolMapping;

import java.util.ArrayList;

public class CFGParsingTableBuilder {

    private final CFGSymbolMapping mapping;

    private int[][] table;

    private int productionCounter = 1;

    private ArrayList<CFGParsingTableProduction> productionActions = new ArrayList<>();

    public CFGParsingTableBuilder(final CFGSymbolMapping mapping) {

        this.mapping = mapping;

        table = new int[mapping.nonTerminalCount][mapping.terminalCount];

    }

    public void write(String nonTerminal, String terminal, CFGParsingTableProduction production) {

        // TODO: compress table by reusing already existing indices

        final int nonTerminalIndex = mapping.nonTerminalToIndex(nonTerminal);
        final int terminalIndex = mapping.terminalToIndex(terminal);

        table[nonTerminalIndex][terminalIndex] = productionCounter;

        productionActions.add(production);

        productionCounter++;

    }

    public CFGParsingTable getTable() {

        CFGParsingTableProduction[] productionActionsArray = new CFGParsingTableProduction[productionCounter - 1];

        productionActions.toArray(productionActionsArray);

        return new CFGParsingTable(mapping, productionActionsArray, table);

    }

}