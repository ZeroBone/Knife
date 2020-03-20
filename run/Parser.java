package net.zerobone.knife.parser;

import java.lang.Integer;
import java.lang.Object;
import java.util.Stack;

final class Parser {
	public static final int T_EOF = 0;

	public static final int T_MUL = 5;

	public static final int T_RIGHT_PAREN = 2;

	public static final int T_ID = 3;

	public static final int T_LEFT_PAREN = 1;

	public static final int T_PLUS = 4;

	private static final int terminalCount = 6;

	private static final int nonTerminalCount = 5;

	private static final int startSymbol = -3;

	private static final int[] table = {
	0,9,0,8,0,0,
	6,0,7,0,5,0,
	0,2,0,1,0,0,
	0,3,0,4,0,0,
	11,0,12,0,13,10};

	private static final int[][] actionTable = {
	{-1,-2},
	{-1,-2},
	{1,-3,2},
	{3},
	{4,-1,-2},
	{},
	{},
	{-4,-5},
	{-4,-5},
	{5,-4,-5},
	{},
	{},
	{}};

	private Stack<Integer> stack;

	private Stack<Object> treeStack;

	private boolean success = false;

	Parser() {
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
			prevRoot.isParent = true;
			for (int i = action.length - 1; i >= 0; i--) {
				IParseTreeNode child = action[i] < 0 ? new ParseTreeNode(action[i]) : new ParseTreeTerminalNode();
				prevRoot.add(child);
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
		parseTree = new ParseTreeNode(startSymbol);
		treeStack.push(parseTree);
	}

	boolean successfullyParsed() {
		return success;
	}
}
