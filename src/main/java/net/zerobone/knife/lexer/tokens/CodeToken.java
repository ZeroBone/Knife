package net.zerobone.knife.lexer.tokens;

import net.zerobone.knife.parser.Parser;

public class CodeToken extends Token {

    public final String code;

    public CodeToken(int line, String code) {
        super(line, Parser.T_CODE);
        this.code = code;
    }

}