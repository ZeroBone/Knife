package net.zerobone.knifedemo;

public class Main {

    public static void main(String[] args) {

        Parser parser = new Parser();

        parser.parse(Parser.T_ID, "id");
        parser.parse(Parser.T_PLUS, "+");
        parser.parse(Parser.T_ID, "id");
        parser.parse(Parser.T_MUL, "*");
        parser.parse(Parser.T_ID, "id");
        parser.parse(Parser.T_EOF, "<eof>");

        System.out.println(parser.treeStack.size());
        parser.parseTree.print(0);

    }

}
