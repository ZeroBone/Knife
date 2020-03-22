package net.zerobone.knife.grammar;

public class CFGSymbol {

    public String id;

    public boolean isTerminal;

    public final String argumentName;

    public CFGSymbol(String id, boolean isTerminal, String argumentName) {
        this.id = id;
        this.isTerminal = isTerminal;
        this.argumentName = argumentName;
    }

}