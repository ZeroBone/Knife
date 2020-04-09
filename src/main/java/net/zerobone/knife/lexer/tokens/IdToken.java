package net.zerobone.knife.lexer.tokens;

import net.zerobone.knife.parser.Parser;

public class IdToken extends Token{

    public final String id;

    public IdToken(int line, String id) {
        super(line, Parser.T_ID);
        this.id = id;
    }

}