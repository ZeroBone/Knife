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
					Object sa = ((ParseNode)children.get(1)).payload;
					Object at = ((ParseNode)children.get(0)).payload;
					v = SNode(sa, at);
				}
				break;
			case 1:
				{
					Object b = ((ParseNode)children.get(0)).payload;
					v = SNode(b);
				}
				break;
			case 2:
				{
					Object _knife_lre_3 = ((ParseNode)children.get(0)).payload;
					_knife_lre_3.push(new Object[] {});
				}
				break;
			case 3:
				{
					Object b = ((ParseNode)children.get(2)).payload;
					Object d = ((ParseNode)children.get(1)).payload;
					Object _knife_lre_3 = ((ParseNode)children.get(0)).payload;
					_knife_lre_3.push(new Object[] {b,
					});
				}
				break;
			case 4:
				{
					Object c = ((ParseNode)children.get(1)).payload;
					Object _knife_lre_3 = ((ParseNode)children.get(0)).payload;
					_knife_lre_3.push(new Object[] {
					c});
					v = _knife_lre_3;
				}
				break;
			case 5:
				{
					Object at = ((ParseNode)children.get(2)).payload;
					Object d = ((ParseNode)children.get(1)).payload;
					Object _knife_lre_3 = ((ParseNode)children.get(0)).payload;
					_knife_lre_3.push(new Object[] {
					at,
					d});
					v = _knife_lre_3;
				}
				break;
			case 6:
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
