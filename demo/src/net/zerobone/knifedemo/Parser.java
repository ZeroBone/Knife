package net.zerobone.knifedemo;

import java.util.LinkedList;
import java.util.Stack;

public final class Parser {

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

    // TODO: make all these helper classes private

    public interface IParseTreeNode {

        void print(int indent);

    }

    public static class ParseTreeTerminalNode implements IParseTreeNode {

        public String terminal = null;

        @Override
        public void print(int indent) {
            for (int i = 0; i < indent; i++) System.out.print("    ");
            System.out.println(terminal);
        }
    }

    public static class ParseTreeNode implements IParseTreeNode {

        private LinkedList<IParseTreeNode> subNodes = new LinkedList<>();

        private void add(IParseTreeNode node) {
            subNodes.addFirst(node);
        }

        @Override
        public void print(int indent) {

            for (int i = 0; i < indent; i++) System.out.print("    ");
            System.out.println("NODE");

            for (IParseTreeNode node : subNodes) {

                node.print(indent + 1);

            }
        }
    }

    private Stack<Integer> stack;

    public Stack<IParseTreeNode> treeStack;

    public IParseTreeNode parseTree = null;

    public Parser() {
        reset();
    }

    public void reset() {

        stack = new Stack<>();
        treeStack = new Stack<>();

        stack.push(startSymbol);

        treeInit();

    }

    public void parse(int tokenId, String token) {

        while (true) {

            if (stack.isEmpty()) {

                if (tokenId == T_EOF) {
                    System.out.println("successful parse");
                    return;
                }

                throw new RuntimeException("Expected end of input. Got: " + tokenId);

            }

            int top = stack.peek();

            if (top > 0) {
                // top is a terminal

                if (tokenId != top) {
                    throw new RuntimeException("Expected: " + top + " Got: " + tokenId);
                }

                // pop both the stack as well a terminal from the the input buffer
                stack.pop();

                System.out.println("--> MATCH " + token);
                treeMatchToken(token);

                return;

            }

            // perform action

            int actionId = table[(-top - 1) * terminalCount + tokenId];

            if (actionId == 0) {
                throw new RuntimeException("error");
            }

            int[] action = actionTable[actionId - 1];

            System.out.println("--> Applying action " + actionId);
            treeApplyAction(action);

            stack.pop();

            // the first element of the array is production header, so we don't consider it
            // we are pushing elements in reverse order because we need the element from which we are
            // going to do left derivation to be on the top of the stack
            for (int i = action.length - 1; i >= 0; i--) {

                stack.push(action[i]);

            }

        }

    }

    private void treeInit() {
        parseTree = new ParseTreeNode();
        treeStack.push(parseTree);
    }

    private void treeMatchToken(String token) {

        ParseTreeTerminalNode node = (ParseTreeTerminalNode)treeStack.peek();
        node.terminal = token;

        treeStack.pop();

    }

    private void treeApplyAction(int[] action) {

        ParseTreeNode prevRoot = (ParseTreeNode)treeStack.peek();

        treeStack.pop();

        for (int i = action.length - 1; i >= 0; i--) {

            IParseTreeNode child = action[i] < 0 ? new ParseTreeNode() : new ParseTreeTerminalNode();

            prevRoot.add(child);

            treeStack.push(child);

        }

    }

}
