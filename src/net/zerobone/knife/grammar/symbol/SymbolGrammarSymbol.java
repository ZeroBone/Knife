package net.zerobone.knife.grammar.symbol;

public class SymbolGrammarSymbol {

    public String id;

    public boolean isTerminal;

    public final String argumentName;

    public SymbolGrammarSymbol(String id, boolean isTerminal, String argumentName) {
        this.id = id;
        this.isTerminal = isTerminal;
        this.argumentName = argumentName;
    }

}