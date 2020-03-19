package net.zerobone.knifedemo;

import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        Parser parser = new Parser();

        parser.parse(Parser.T_ID, "id");
        parser.parse(Parser.T_PLUS, "+");
        parser.parse(Parser.T_ID, "id");
        parser.parse(Parser.T_MUL, "*");
        parser.parse(Parser.T_ID, "id");
        /*parser.parse(Parser.T_PLUS, "+");
        parser.parse(Parser.T_ID, "id");
        parser.parse(Parser.T_PLUS, "+");
        parser.parse(Parser.T_ID, "id");
        parser.parse(Parser.T_MUL, "*");
        parser.parse(Parser.T_ID, "id");*/
        parser.parse(Parser.T_EOF, "<eof>");

        FileWriter treeWriter = new FileWriter("ast.log");

        if (parser.isSuccessfullyParsed())

        parser.getParseTree().print(0, treeWriter);

        treeWriter.close();

    }

}
