package net.zerobone.knife.grammar;

public class Symbol {

    public String id;

    public boolean isTerminal;

    public final String argumentName;

    public Symbol(String id, boolean isTerminal, String argumentName) {
        this.id = id;
        this.isTerminal = isTerminal;
        this.argumentName = argumentName;
    }

}