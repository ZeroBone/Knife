package net.zerobone.knife.ast.statements;

public class TypeStatementNode extends StatementNode {

    public String nonTerminal;

    public String type;

    public TypeStatementNode(String nonTerminal, String type) {
        this.nonTerminal = nonTerminal;
        this.type = type;
    }

}