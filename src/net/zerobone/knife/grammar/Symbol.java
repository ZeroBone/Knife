package net.zerobone.knife.grammar;

public class Symbol {

    public int id;

    public final String argumentName;

    public Symbol(int id, String argumentName) {
        this.id = id;
        this.argumentName = argumentName;
    }

    public boolean isTerminal() {
        return id > 0;
    }

}