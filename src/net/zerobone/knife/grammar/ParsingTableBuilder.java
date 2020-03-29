package net.zerobone.knife.grammar;

import net.zerobone.knife.grammar.table.ParsingTable;
import net.zerobone.knife.grammar.table.ParsingTableConflict;
import net.zerobone.knife.grammar.table.ParsingTableProduction;
import net.zerobone.knife.utils.BijectiveMap;

import java.util.ArrayList;

class ParsingTableBuilder {

    private final Grammar grammar;

    public final int nonTerminalCount;

    public final int terminalCount;

    private final BijectiveMap<String, Integer> mapping = new BijectiveMap<>();

    private int[][] table;

    private int productionCounter = 1;

    private ArrayList<ParsingTableProduction> productionActions = new ArrayList<>();

    private ArrayList<ParsingTableConflict> conflicts = new ArrayList<>();

    private InnerProduction lastWrittenProduction = null;

    public ParsingTableBuilder(final Grammar grammar) {

        this.grammar = grammar;

        this.nonTerminalCount = grammar.getNonTerminalCount();

        // + 1 because the eof token is also a terminal in the table
        this.terminalCount = grammar.getTerminalCount() + 1;

        table = new int[nonTerminalCount][terminalCount];

        for (int t = 1; t < terminalCount; t++) {

            // we can use the same indexes because the
            // terminal set of a grammar cannot change
            this.mapping.put(grammar.idToSymbol(t), t);

        }

        int nonTerminalCounter = -1;

        for (int nt : grammar.productions.keySet()) {

            this.mapping.put(grammar.idToSymbol(nt), nonTerminalCounter);

            nonTerminalCounter--;

        }

    }

    private int terminalToIndex(int terminalOrEof) {

        if (terminalOrEof == Grammar.FOLLOW_SET_EOF) {
            return 0;
        }

        String terminalName = grammar.idToSymbol(terminalOrEof);

        Integer terminalIndex = mapping.mapKey(terminalName);

        assert terminalIndex != null;

        return terminalIndex;

    }

    private int nonTerminalToIndex(int nonTerminal) {

        String nonTerminalName = grammar.idToSymbol(nonTerminal);

        Integer nonTerminalIndex = mapping.mapKey(nonTerminalName);

        assert nonTerminalIndex != null;

        return -nonTerminalIndex - 1;

    }

    private ParsingTableProduction convertProduction(int nonTerminal, InnerProduction production) {

        ArrayList<Symbol> body = new ArrayList<>();

        for (InnerSymbol symbol : production.body) {

            body.add(new Symbol(grammar.idToSymbol(symbol.id), symbol.isTerminal(), symbol.argumentName));

        }

        return new ParsingTableProduction(
            grammar.idToSymbol(nonTerminal),
            body,
            production.code == null ? "" : production.code
        );

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

            if (table[nonTerminalIndex][terminalIndex] != 0) {
                conflicts.add(new ParsingTableConflict(nonTerminal, terminalOrEof, grammar));
            }

            table[nonTerminalIndex][terminalIndex] = productionCounter - 1;

            return;

        }

        if (table[nonTerminalIndex][terminalIndex] != 0) {
            conflicts.add(new ParsingTableConflict(nonTerminal, terminalOrEof, grammar));
        }

        table[nonTerminalIndex][terminalIndex] = productionCounter;

        productionActions.add(convertProduction(nonTerminal, production));

        lastWrittenProduction = production;

        productionCounter++;

    }

    public void writeSynchronize(int nonTerminal, int terminalOrEof) {

        final int nonTerminalIndex = nonTerminalToIndex(nonTerminal);
        final int terminalIndex = terminalToIndex(terminalOrEof);

        if (table[nonTerminalIndex][terminalIndex] != 0) {
            // this cell is already occupied by an action
            return;
        }

        /* System.out.println("Synchronized entry at non-terminal: " + grammar.idToSymbol(nonTerminal) +
            " Terminal: " + (terminalOrEof == Grammar.FOLLOW_SET_EOF ? "$" : grammar.idToSymbol(terminalOrEof))); */

        table[nonTerminalIndex][terminalIndex] = ParsingTable.SYNCHRONIZE;

    }

    public ParsingTable getTable() {

        assert productionActions.size() == productionCounter - 1;

        ParsingTableProduction[] productionActionsArray = new ParsingTableProduction[productionCounter - 1];

        productionActions.toArray(productionActionsArray);

        ParsingTableConflict[] conflictsArray = new ParsingTableConflict[conflicts.size()];

        conflicts.toArray(conflictsArray);

        return new ParsingTable(
            mapping,
            nonTerminalCount,
            terminalCount,
            productionActionsArray,
            table,
            grammar.idToSymbol(Grammar.START_SYMBOL_ID),
            conflictsArray
        );

    }

}