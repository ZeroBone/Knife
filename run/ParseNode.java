package net.zerobone.knife.parser;

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
					Object s = ((ParseNode)children.get(1)).payload;
					Object t = ((ParseNode)children.get(0)).payload;
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
					Object nonTerminal = ((ParseNode)children.get(2)).payload;
					Object body = ((ParseNode)children.get(0)).payload;
					v = new ProductionStatementNode(nonTerminal, body.getProduction(), body.getCode());
				}
				break;
			case 4:
				{
					Object code = ((ParseNode)children.get(0)).payload;
					v = new ProductionStatementBody(code);
				}
				break;
			case 5:
				{
					v = null;
				}
				break;
			case 6:
				{
					Object c = ((ParseNode)children.get(0)).payload;
					v = c;
				}
				break;
			default:
				throw new IllegalStateException();
		}
		payload = v;
		children = null;
	}
}
