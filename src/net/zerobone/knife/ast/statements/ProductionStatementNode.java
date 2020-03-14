package net.zerobone.knife.ast.statements;

import net.zerobone.knife.ast.entities.ProductionSymbol;

import java.util.LinkedList;

public class ProductionStatementNode extends StatementNode {

    public String nonTerminal;

    public String argument;

    public LinkedList<ProductionSymbol> production;

    public ProductionStatementNode(String nonTerminal, String argument, LinkedList<ProductionSymbol> production) {
        this.nonTerminal = nonTerminal;
        this.argument = argument;
        this.production = production;
    }

}