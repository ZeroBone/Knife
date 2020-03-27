package net.zerobone.knife.lexer.tokens;

public class Token {

    public final int line;

    public final int id;

    public Token(int line, int id) {
        this.line = line;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Token{" +
            "line=" + line +
            ", id=" + id +
            '}';
    }

}