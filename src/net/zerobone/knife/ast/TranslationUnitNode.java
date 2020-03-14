package net.zerobone.knife.ast;

import net.zerobone.knife.ast.statements.StatementNode;

import java.util.LinkedList;

public class TranslationUnitNode extends AstNode {

    public LinkedList<StatementNode> statements = new LinkedList<>();

    public TranslationUnitNode() {}

    public void addStatement(StatementNode s) {
        statements.addFirst(s);
    }

}