package net.zerobone.knifedemo;

import java.io.FileWriter;
import java.io.IOException;

public class ParseTreeTerminalNode implements IParseTreeNode {

    public String terminal = null;

    @Override
    public void print(int indent, FileWriter w) throws IOException {
        for (int i = 0; i < indent; i++) w.write("  ");
        w.write(terminal);
        w.write('\n');
    }

}