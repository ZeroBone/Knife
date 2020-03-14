package net.zerobone.knife.grammar;

public class CFGSymbol {

    public String id;

    public boolean isTerminal;

    public CFGSymbol(String id, boolean isTerminal) {
        this.id = id;
        this.isTerminal = isTerminal;
    }

}