package net.zerobone.knife.ast.entities;

import java.util.LinkedList;

public class ProductionStatementBody {

    private LinkedList<ProductionSymbol> production = new LinkedList<>();

    public ProductionStatementBody() {}

    public void addNonTerminal(String id) {
        production.addFirst(new ProductionSymbol(id, false));
    }

    public void addNonTerminal(String id, String argument) {
        production.addFirst(new ProductionSymbol(id, argument,false));
    }

    public void addTerminal(String id) {
        production.addFirst(new ProductionSymbol(id, true));
    }

    public void addTerminal(String id, String argument) {
        production.addFirst(new ProductionSymbol(id, argument,true));
    }

    public LinkedList<ProductionSymbol> getProduction() {
        return production;
    }

}