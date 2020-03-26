package net.zerobone.knife.parser;

import java.lang.Object;
import java.util.Stack;

public final class Parser {
	public static final int T_EOF = 0;

	public static final int T_ID = 1;

	private static final int terminalCount = 2;

	private static final int nonTerminalCount = 10;

	private static final int startSymbol = -1;

	private static final int[] table = {
	0,1,
	0,2,
	0,3,
	0,4,
	0,6,
	0,7,
	0,0,
	0,0,
	0,9,
	11,10};

	private static final int[][] actionTable = {
	{-2},
	{-3},
	{-4},
	{-5},
	{-6},
	{-7},
	{-9},
	{1,-10},
	{-7,1,-10},
	{1,-10},
	{}};

	private Stack<ParseNode> stack;

	private boolean success = false;

	private ParseNode parseTree;

	public Parser() {
		reset();
	}

	public void parse(int tokenId, Object token) {
		while (true) {
			ParseNode prevRoot = stack.peek();
			while (prevRoot.actionId != 0) {
				prevRoot.reduce();
				stack.pop();
				if (stack.isEmpty()) {
					if (tokenId != T_EOF) {
						throw new RuntimeException("Expected end of input. Got: " + tokenId);
					}
					success = true;
					return;
				}
				prevRoot = stack.peek();
			}
			if (prevRoot.symbolId > 0) {
				if (tokenId != prevRoot.symbolId) {
					throw new RuntimeException("Expected: " + prevRoot.symbolId + " Got: " + tokenId);
				}
				prevRoot.payload = token;
				stack.pop();
				return;
			}
			int actionId = table[(-prevRoot.symbolId - 1) * terminalCount + tokenId];
			if (actionId == 0) {
				throw new RuntimeException("Syntax error. Token: " + token);
			}
			int[] action = actionTable[actionId - 1];
			prevRoot.actionId = actionId;
			for (int i = action.length - 1; i >= 0; i--) {
				ParseNode child = new ParseNode(action[i]);
				prevRoot.children.add(child);
				stack.push(child);
			}
		}
	}

	public void reset() {
		success = false;
		parseTree = new ParseNode(startSymbol);
		stack = new Stack<>();
		stack.push(parseTree);
	}

	public Object getValue() {
		if (!success) {
			return null;
		}
		return parseTree.payload;
	}

	public boolean successfullyParsed() {
		return success;
	}
}
