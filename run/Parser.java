package net.zerobone.knife.parser;

import java.lang.Object;
import java.util.Stack;

public final class Parser {
	public static final int T_EOF = 0;

	public static final int T_CODE = 4;

	public static final int T_SEMICOLON = 3;

	public static final int T_ID = 1;

	public static final int T_ASSIGN = 2;

	private static final int terminalCount = 5;

	private static final int nonTerminalCount = 5;

	private static final int startSymbol = -1;

	private static final int[] table = {
	1,2,0,0,0,
	0,3,0,0,0,
	0,4,0,0,0,
	0,0,0,5,0,
	6,6,0,0,7};

	private static final int[][] actionTable = {
	{},
	{-2,-1},
	{-3},
	{1,2,-4},
	{3,-5},
	{},
	{4}};

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
