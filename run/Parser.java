package net.zerobone.knife.parser;

import java.lang.Object;
import java.util.ArrayList;
import java.util.Stack;

public final class Parser {
	public static final int T_EOF = 0;

	public static final int T_MUL = 2;

	public static final int T_RIGHT_PAREN = 4;

	public static final int T_ID = 5;

	public static final int T_LEFT_PAREN = 3;

	public static final int T_PLUS = 1;

	private static final int terminalCount = 6;

	private static final int nonTerminalCount = 5;

	private static final int startSymbol = -1;

	private static final int[] table = {
	-1,0,0,1,-1,1,
	-1,-1,0,2,-1,2,
	4,3,0,0,4,0,
	-1,-1,-1,5,-1,6,
	8,8,7,0,8,0};

	private static final int[][] actionTable = {
	{-2,-3},
	{-4,-5},
	{1,-2,-3},
	{},
	{3,-1,4},
	{5},
	{2,-4,-5},
	{}};

	private Stack<ParseNode> stack;

	private ArrayList<ParseError> errors;

	private boolean reachedEof;

	private ParseNode parseTree;

	public Parser() {
		reset();
	}

	public void parse(int tokenId, Object token) {
		while (true) {
			ParseNode prevRoot = stack.peek();
			while (prevRoot.actionId != 0) {
				if (errors.isEmpty()) {
					prevRoot.reduce();
				} else {
					prevRoot.children = null;
				}
				stack.pop();
				if (stack.isEmpty()) {
					if (tokenId != T_EOF) {
						errors.add(new ParseError(T_EOF, tokenId, token));
						return;
					}
					reachedEof = true;
					return;
				}
				prevRoot = stack.peek();
			}
			if (prevRoot.symbolId > 0) {
				if (tokenId != prevRoot.symbolId) {
					stack.pop();
					errors.add(new ParseError(prevRoot.symbolId, tokenId, token));
					return;
				}
				prevRoot.payload = token;
				stack.pop();
				return;
			}
			int actionId = table[(-prevRoot.symbolId - 1) * terminalCount + tokenId];
			if (actionId == 0) {
				errors.add(new ParseError(ParseError.ANY, tokenId, token));
				return;
			}
			if (actionId == -1) {
				errors.add(new ParseError(ParseError.ANY, tokenId, token));
				stack.pop();
				return;
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
		errors = new ArrayList<>();
		reachedEof = false;
		parseTree = new ParseNode(startSymbol);
		stack = new Stack<>();
		stack.push(parseTree);
	}

	public Object getValue() {
		assert successfullyParsed();
		return parseTree.payload;
	}

	public ParseError[] getErrors() {
		ParseError[] parseErrors = new ParseError[errors.size()];
		errors.toArray(parseErrors);
		return parseErrors;
	}

	public boolean successfullyParsed() {
		return reachedEof && errors.isEmpty();
	}
}
