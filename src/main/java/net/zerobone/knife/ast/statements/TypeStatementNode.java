package net.zerobone.knife.ast.statements;

public class TypeStatementNode extends StatementNode {

    public String symbol;

    public String type;

    public TypeStatementNode(String symbol, String type) {
        this.symbol = symbol;
        this.type = type;
    }

}