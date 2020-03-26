package net.zerobone.knife.grammar.verification;

import net.zerobone.knife.grammar.Grammar;

public class NonTerminalNotDefinedError extends VerificationError {

    private final int nonTerminal;

    public NonTerminalNotDefinedError(int nonTerminal) {
        super(false);
        this.nonTerminal = nonTerminal;
    }

    @Override
    public String getMessage(Grammar grammar) {
        return "Non-terminal '" + grammar.idToSymbol(nonTerminal) + "' has not been defined.";
    }

}