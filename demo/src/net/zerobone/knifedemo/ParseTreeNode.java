package net.zerobone.knifedemo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class ParseTreeNode implements IParseTreeNode {

    public final int nonTerminal;

    public boolean isParent = false;

    private Object payload;

    private LinkedList<IParseTreeNode> subNodes = new LinkedList<>();

    public ParseTreeNode(int nonTerminal) {
        this.nonTerminal = nonTerminal;
    }

    public void add(IParseTreeNode node) {
        subNodes.addFirst(node);
    }

    public void optimize() {

        try {
            FileWriter fw = new FileWriter("debug.log", true);

            System.out.println("Optimizing non-terminal " + nonTerminal + ":");

            fw.write("\nOptimizing non-terminal " + nonTerminal + ": " + this + "\n");
            print(0, fw);

            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void print(int indent, FileWriter w) throws IOException {

        for (int i = 0; i < indent; i++) w.write("  ");
        w.write("NODE(" + nonTerminal + "):");
        w.write('\n');

        for (IParseTreeNode node : subNodes) {

            node.print(indent + 1, w);

        }
    }

}