package net.zerobone.knife.parser;

import net.zerobone.knife.ast.TranslationUnitNode;
import net.zerobone.knife.ast.entities.ProductionStatementBody;
import net.zerobone.knife.ast.statements.ProductionStatementNode;
import net.zerobone.knife.ast.statements.StatementNode;

import java.lang.Object;
import java.util.ArrayList;

final class ParseNode {
	int actionId = 0;

	final int symbolId;

	Object payload = null;

	ArrayList<Object> children;

	ParseNode(int symbolId) {
		this.symbolId = symbolId;
		children = this.symbolId < 0 ? new ArrayList<>() : null;
	}

	void reduce() {
		Object v;
		switch (actionId - 1) {
			case 0:
				{
					v = new TranslationUnitNode();
				}
				break;
			case 1:
				{
					StatementNode s = (StatementNode) ((ParseNode)children.get(1)).payload;
					TranslationUnitNode t = (TranslationUnitNode) ((ParseNode)children.get(0)).payload;
					t.addStatement(s); v = t;
				}
				break;
			case 2:
				{
					Object s = ((ParseNode)children.get(0)).payload;
					v = s;
				}
				break;
			case 3:
				{
					String nonTerminal = (String) ((ParseNode)children.get(2)).payload;
					ProductionStatementBody body = (ProductionStatementBody) ((ParseNode)children.get(0)).payload;
					v = new ProductionStatementNode(nonTerminal, body.getProduction(), body.getCode());
				}
				break;
			case 4:
				{
					String code = (String) ((ParseNode)children.get(0)).payload;
					v = new ProductionStatementBody(code);
				}
				break;
			case 5:
				{
					String s = (String) ((ParseNode)children.get(2)).payload;
					String arg = (String) ((ParseNode)children.get(1)).payload;
					ProductionStatementBody b = (ProductionStatementBody) ((ParseNode)children.get(0)).payload;
					char firstChar = s.charAt(0);
					    if (Character.isUpperCase(firstChar))
					        if (arg == null) b.addTerminal(s);
					        else b.addTerminal(s, arg);
					    else
					        if (arg == null) b.addNonTerminal(s);
					        else b.addNonTerminal(s, arg);
					    v = b;
				}
				break;
			case 6:
				{
					v = null;
				}
				break;
			case 7:
				{
					Object c = ((ParseNode)children.get(0)).payload;
					v = c;
				}
				break;
			case 8:
				{
					v = null;
				}
				break;
			case 9:
				{
					Object arg = ((ParseNode)children.get(1)).payload;
					v = arg;
				}
				break;
			default:
				throw new IllegalStateException();
		}
		payload = v;
		children = null;
	}
}
