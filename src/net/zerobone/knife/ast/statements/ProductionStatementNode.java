package net.zerobone.knife.ast.statements;

import net.zerobone.knife.ast.entities.ProductionSymbol;

import java.util.LinkedList;

public class ProductionStatementNode extends StatementNode {

    public String nonTerminal;

    public LinkedList<ProductionSymbol> production;

    public String code;

    public ProductionStatementNode(String nonTerminal, LinkedList<ProductionSymbol> production, String code) {
        this.nonTerminal = nonTerminal;
        this.production = production;
        this.code = code;
    }

}