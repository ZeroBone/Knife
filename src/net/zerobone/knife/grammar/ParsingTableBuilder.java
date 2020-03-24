package net.zerobone.knife.grammar;

import net.zerobone.knife.grammar.table.ParsingTable;
import net.zerobone.knife.grammar.table.ParsingTableProduction;
import net.zerobone.knife.utils.BijectiveMap;

import java.util.ArrayList;

class ParsingTableBuilder {

    private final Grammar grammar;

    public final int nonTerminalCount;

    public final int terminalCount;

    private int terminalCounter = 1;

    private int nonTerminalCounter = -1;

    private final BijectiveMap<String, Integer> mapping = new BijectiveMap<>();

    private int[][] table;

    private int productionCounter = 1;

    private ArrayList<ParsingTableProduction> productionActions = new ArrayList<>();

    private InnerProduction lastWrittenProduction = null;

    public ParsingTableBuilder(final Grammar grammar) {

        this.grammar = grammar;

        this.nonTerminalCount = grammar.getNonTerminalCount();

        // + 1 because the eof token is also a terminal in the table
        this.terminalCount = grammar.getTerminalCount() + 1;

        table = new int[nonTerminalCount][terminalCount];

    }

    private int terminalToIndex(int terminalOrEof) {

        if (terminalOrEof == Grammar.FOLLOW_SET_EOF) {
            return 0;
        }

        String terminalName = grammar.idToSymbol(terminalOrEof);

        Integer terminalIndex = mapping.mapKey(terminalName);

        if (terminalIndex == null) {

            mapping.put(terminalName, terminalCounter);

            return terminalCounter++;

        }

        return terminalIndex;

    }

    private int nonTerminalToIndex(int nonTerminal) {

        String nonTerminalName = grammar.idToSymbol(nonTerminal);

        Integer nonTerminalIndex = mapping.mapKey(nonTerminalName);

        if (nonTerminalIndex == null) {

            mapping.put(nonTerminalName, nonTerminalCounter);

            return -nonTerminalCounter-- - 1;

        }

        return -nonTerminalIndex - 1;

    }

    private ParsingTableProduction convertProduction(int nonTerminal, InnerProduction production) {

        ArrayList<Symbol> body = new ArrayList<>();

        for (InnerSymbol symbol : production.body) {

            body.add(new Symbol(grammar.idToSymbol(symbol.id), symbol.isTerminal(), symbol.argumentName));

        }

        return new ParsingTableProduction(grammar.idToSymbol(nonTerminal), body, production.code);

    }

    public void write(int nonTerminal, int terminalOrEof, InnerProduction production) {

        assert production != null;

        assert nonTerminal < 0;
        assert terminalOrEof > 0 || terminalOrEof == Grammar.FOLLOW_SET_EOF; // can be 0 aka eof

        final int nonTerminalIndex = nonTerminalToIndex(nonTerminal);
        final int terminalIndex = terminalToIndex(terminalOrEof);

        assert nonTerminalIndex < nonTerminalCount;
        assert terminalIndex < terminalCount;

        if (lastWrittenProduction == production) {
            table[nonTerminalIndex][terminalIndex] = productionCounter - 1;
            return;
        }

        table[nonTerminalIndex][terminalIndex] = productionCounter;

        productionActions.add(convertProduction(nonTerminal, production));

        lastWrittenProduction = production;

        productionCounter++;

    }

    public ParsingTable getTable() {

        assert productionActions.size() == productionCounter - 1;

        ParsingTableProduction[] productionActionsArray = new ParsingTableProduction[productionCounter - 1];

        productionActions.toArray(productionActionsArray);

        return new ParsingTable(
            mapping,
            nonTerminalCount,
            terminalCount,
            productionActionsArray,
            table,
            grammar.idToSymbol(Grammar.START_SYMBOL_ID)
        );

    }

}