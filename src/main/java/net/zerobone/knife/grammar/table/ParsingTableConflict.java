package net.zerobone.knife.grammar.table;

import net.zerobone.knife.grammar.Grammar;

public class ParsingTableConflict {

    public final String nonTerminal;

    public final String terminal;

    public ParsingTableConflict(int nonTerminal, int terminalOrEof, Grammar grammar) {
        this.nonTerminal = grammar.idToSymbol(nonTerminal);
        terminal = terminalOrEof == Grammar.FOLLOW_SET_EOF ? "<EOF>" : grammar.idToSymbol(terminalOrEof);
    }

    public String getMessage() {

        return "Ambiguous state at non-terminal '" + nonTerminal + "' when encountering '" + terminal + "'. Left factoring the grammar at this point might help.";

    }

}