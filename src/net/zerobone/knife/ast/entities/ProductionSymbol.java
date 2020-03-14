package net.zerobone.knife.ast.entities;

public class ProductionSymbol {

    public String id;

    public String argument;

    public boolean terminal;

    public ProductionSymbol(String id, String argument, boolean terminal) {
        this.id = id;
        this.argument = argument;
        this.terminal = terminal;
    }

    public ProductionSymbol(String id, boolean terminal) {
        this.id = id;
        this.argument = null;
        this.terminal = terminal;
    }

}