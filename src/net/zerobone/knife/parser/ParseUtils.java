package net.zerobone.knife.parser;

public class ParseUtils {

    public static String convertTerminal(int terminal) {

        switch (terminal) {

            case Parser.T_EOF:
                return "<end-of-source>";

            case Parser.T_CODE:
                return "<code-block>";

            case Parser.T_SEMICOLON:
                return ";";

            case Parser.T_RIGHT_PAREN:
                return ")";

            case Parser.T_ID:
                return "<identifier>";

            case Parser.T_ASSIGN:
                return "=";

            case Parser.T_LEFT_PAREN:
                return "(";

            case Parser.T_TYPE:
                return "%type";

            default:
                return "<" + terminal + ">";

        }

    }

}