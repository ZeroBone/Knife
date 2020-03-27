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
					Object s = ((ParseNode)children.get(1)).payload;
					Object t = ((ParseNode)children.get(0)).payload;
					t.addStatement(s); v = t;
				}
				break;
			case 1:
				{
					Object s = ((ParseNode)children.get(0)).payload;
					v = s;
				}
				break;
			case 2:
				{
					Object nonTerminal = ((ParseNode)children.get(2)).payload;
					Object body = ((ParseNode)children.get(0)).payload;
					v = new ProductionStatementNode(nonTerminal, body.getProduction(), body.getCode());
				}
				break;
			case 3:
				{
					Object code = ((ParseNode)children.get(0)).payload;
					v = new ProductionStatementBody(code);
				}
				break;
			case 4:
				{
					Object s = ((ParseNode)children.get(2)).payload;
					Object arg = ((ParseNode)children.get(1)).payload;
					Object b = ((ParseNode)children.get(0)).payload;
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
			case 7:
				{
					v = null;
				}
				break;
			case 8:
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
