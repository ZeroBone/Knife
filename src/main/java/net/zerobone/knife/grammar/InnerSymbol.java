package net.zerobone.knife.grammar;

class InnerSymbol {

    int id;

    public final String argumentName;

    public InnerSymbol(int id, String argumentName) {
        this.id = id;
        this.argumentName = argumentName;
    }

    boolean isTerminal() {
        return id > 0;
    }

}