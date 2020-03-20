package net.zerobone.knife.parser;

import java.lang.Object;
import java.util.ArrayList;

final class ParseTreeNode {
	int actionId = 0;

	boolean isParent = false;

	Object payload = null;

	ArrayList<Object> children = new ArrayList<>();

	ParseTreeNode() {
	}

	void reduce() {
		System.out.println("Reducing action + " + actionId + ".");
		Object v;
		switch (actionId - 1) {
			case 0:
				{
					Object e = ((ParseTreeNode)children.get(0)).payload;
					v = e;
				}
				break;
			case 1:
				{
					Object e = ((ParseTreeNode)children.get(0)).payload;
					v = e;
				}
				break;
			case 2:
				{
					Object e = ((ParseTreeNode)children.get(0)).payload;
					v = e;
				}
				break;
			case 3:
				{
					Object id = ((ParseTreeTerminalNode)children.get(0)).terminal;
					v = id;
				}
				break;
			case 4:
				{
					Object op1 = ((ParseTreeNode)children.get(1)).payload;
					Object op2 = ((ParseTreeNode)children.get(0)).payload;
					v = (int)op1 + (int)op2;
				}
				break;
			case 5:
				{
					Object op1 = ((ParseTreeNode)children.get(1)).payload;
					Object op2 = ((ParseTreeNode)children.get(0)).payload;
					v = (int)op1 * (int)op2;
				}
				break;
			default:
				throw new IllegalStateException();
		}
		payload = v;
		children = new ArrayList<>();
	}
}
