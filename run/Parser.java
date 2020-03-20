package net.zerobone.knife.parser;

import java.lang.Integer;
import java.lang.Object;
import java.util.Stack;

public final class Parser {
	public static final int T_EOF = 0;

	public static final int T_MUL = 4;

	public static final int T_ID = 1;

	public static final int T_PLUS = 2;

	public static final int T_MINUS = 3;

	private static final int terminalCount = 5;

	private static final int nonTerminalCount = 2;

	private static final int startSymbol = -1;

	private static final int[] table = {
	0,2,3,4,1,
	0,5,6,7,8};

	private static final int[][] actionTable = {
	{-2},
	{-2},
	{-2},
	{-2},
	{1},
	{2,-2,-2},
	{3,-2,-2},
	{4,-2,-2}};

	private Stack<Integer> stack;

	private Stack<Object> treeStack;

	private boolean success = false;

	private Object parseTree;

	public Parser() {
		reset();
	}

	public void parse(int tokenId, Object token) {
		while (true) {
			if (stack.isEmpty()) {
				if (tokenId == T_EOF) {
					while (!treeStack.isEmpty()) {
						((ParseTreeNode)treeStack.pop()).reduce();
					}
					success = true;
					return;
				}
				throw new RuntimeException("Expected end of input. Got: " + tokenId);
			}
			int top = stack.peek();
			if (top > 0) {
				if (tokenId != top) {
					throw new RuntimeException("Expected: " + top + " Got: " + tokenId);
				}
				stack.pop();
				((ParseTreeTerminalNode)treeStack.peek()).terminal = token;
				treeStack.pop();
				return;
			}
			int actionId = table[(-top - 1) * terminalCount + tokenId];
			if (actionId == 0) {
				throw new RuntimeException("Syntax error. Token: " + token);
			}
			int[] action = actionTable[actionId - 1];
			ParseTreeNode prevRoot = (ParseTreeNode)treeStack.peek();
			while (prevRoot.isParent) {
				prevRoot.isParent = false;
				prevRoot.reduce();
				treeStack.pop();
				prevRoot = (ParseTreeNode)treeStack.peek();
			}
			prevRoot.actionId = actionId;
			prevRoot.isParent = true;
			for (int i = action.length - 1; i >= 0; i--) {
				Object child = action[i] < 0 ? new ParseTreeNode() : new ParseTreeTerminalNode();
				prevRoot.children.add(child);
				treeStack.push(child);
			}
			stack.pop();
			for (int i = action.length - 1; i >= 0; i--) {
				stack.push(action[i]);
			}
		}
	}

	public void reset() {
		success = false;
		stack = new Stack<>();
		stack.push(startSymbol);
		treeStack = new Stack<>();
		parseTree = new ParseTreeNode();
		treeStack.push(parseTree);
	}

	public Object getValue() {
		if (!success) {
			return null;
		}
		return ((ParseTreeNode)parseTree).payload;
	}

	public boolean successfullyParsed() {
		return success;
	}
}
