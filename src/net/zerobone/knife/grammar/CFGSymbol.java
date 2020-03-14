package net.zerobone.knife.grammar;

public class CFGSymbol {

    public String sym;

    public boolean isTerminal;

    public CFGSymbol(String sym, boolean isTerminal) {
        this.sym = sym;
        this.isTerminal = isTerminal;
    }

}