package net.zerobone.knifedemo;

import java.io.FileWriter;
import java.io.IOException;

public interface IParseTreeNode {

    void print(int indent, FileWriter w) throws IOException;

}