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
					Object t = ((ParseNode)children.get(1)).payload;
					Object _knife_lre_4 = ((ParseNode)children.get(0)).payload;
					_knife_lre_4.push(new Object[] {});
				}
				break;
			case 1:
				{
					Object factor = ((ParseNode)children.get(1)).payload;
					Object _knife_lre_5 = ((ParseNode)children.get(0)).payload;
					_knife_lre_5.push(new Object[] {});
				}
				break;
			case 2:
				{
					Object expr = ((ParseNode)children.get(1)).payload;
					v = expr;
				}
				break;
			case 3:
				{
					Object id = ((ParseNode)children.get(0)).payload;
					v = id;
				}
				break;
			case 4:
				{
					Object right = ((ParseNode)children.get(1)).payload;
					Object _knife_lre_4 = ((ParseNode)children.get(0)).payload;
					_knife_lre_4.push(new Object[] {right});v = _knife_lre_4;
				}
				break;
			case 5:
				{
					v = new Stack<Object>();
				}
				break;
			case 6:
				{
					Object r = ((ParseNode)children.get(1)).payload;
					Object _knife_lre_5 = ((ParseNode)children.get(0)).payload;
					_knife_lre_5.push(new Object[] {r});v = _knife_lre_5;
				}
				break;
			case 7:
				{
					v = new Stack<Object>();
				}
				break;
			default:
				throw new IllegalStateException();
		}
		payload = v;
		children = null;
	}
}
