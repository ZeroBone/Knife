package net.zerobone.knife.lexer.tokens;

public class Token {

    public final int line;

    public final int type;

    public Token(int line, int type) {
        this.line = line;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Token{" +
            "line=" + line +
            ", type=" + type +
            '}';
    }

}