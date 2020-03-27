package net.zerobone.knife.lexer.tokens;

import net.zerobone.knife.parser.Parser;

public class IdToken extends Token{

    public final String identifier;

    public IdToken(int line, String identifier) {
        super(line, Parser.T_ID);
        this.identifier = identifier;
    }

}