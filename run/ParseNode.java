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
					Object term = ((ParseNode)children.get(1)).payload;
					Object rest = ((ParseNode)children.get(0)).payload;
					v = new Node(term, rest);
				}
				break;
			case 1:
				{
					Object term = ((ParseNode)children.get(1)).payload;
					Object rest = ((ParseNode)children.get(0)).payload;
					v = new Node(term, rest);
				}
				break;
			case 2:
				{
					Object e = ((ParseNode)children.get(1)).payload;
					v = e;
				}
				break;
			case 3:
				{
					Object id = ((ParseNode)children.get(0)).payload;
					v = new IdNode(id);
				}
				break;
			case 4: break;
			case 5: break;
			case 6: break;
			case 7: break;
			case 8: break;
			case 9: break;
			case 10: break;
			case 11: break;
			case 12: break;
			default:
				throw new IllegalStateException();
		}
		payload = v;
		children = null;
	}
}
