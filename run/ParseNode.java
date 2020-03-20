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
		System.out.println("Reducing action + " + actionId + ".");
		Object v;
		switch (actionId - 1) {
			case 0:
				{
					Object e = ((ParseNode)children.get(0)).payload;
					v = e;
				}
				break;
			case 1:
				{
					Object e = ((ParseNode)children.get(0)).payload;
					v = e;
				}
				break;
			case 2:
				{
					Object e = ((ParseNode)children.get(0)).payload;
					v = e;
				}
				break;
			case 3:
				{
					Object e = ((ParseNode)children.get(0)).payload;
					v = e;
				}
				break;
			case 4:
				{
					Object id = ((ParseNode)children.get(0)).payload;
					v = id;
				}
				break;
			case 5:
				{
					Object op1 = ((ParseNode)children.get(1)).payload;
					Object op2 = ((ParseNode)children.get(0)).payload;
					v = (int)op1 + (int)op2;
				}
				break;
			case 6:
				{
					Object op1 = ((ParseNode)children.get(1)).payload;
					Object op2 = ((ParseNode)children.get(0)).payload;
					v = (int)op1 - (int)op2;
				}
				break;
			case 7:
				{
					Object op1 = ((ParseNode)children.get(1)).payload;
					Object op2 = ((ParseNode)children.get(0)).payload;
					v = (int)op1 * (int)op2;
				}
				break;
			default:
				throw new IllegalStateException();
		}
		payload = v;
		children = null;
	}
}
