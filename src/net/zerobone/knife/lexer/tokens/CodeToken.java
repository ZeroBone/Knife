package net.zerobone.knife.lexer.tokens;

import net.zerobone.knife.parser.Parser;

public class CodeToken extends Token {

    public final String code;

    public CodeToken(String code) {
        super(Parser.T_CODE);
        this.code = code;
    }

}