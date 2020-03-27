package net.zerobone.knife.lexer;

import net.zerobone.knife.lexer.tokens.Token;
import net.zerobone.knife.parser.Parser;

import java.io.IOException;
import java.io.InputStream;

public class Lexer {

    private final InputStream stream;

    private int line = 1;

    private int peek;

    public Lexer(InputStream stream) {
        this.stream = stream;
    }

    private boolean readChar(char expected) throws IOException {
        return stream.read() == expected;
    }

    public Token lex() throws IOException, LexerException {

        while (true) {

            if (peek == -1) {
                return new Token(Parser.T_EOF);
            }

            if (peek == ' ' || peek == '\t') {
                peek = stream.read();
                continue;
            }
            else if (peek == '\n') {
                line++;
                peek = stream.read();
                continue;
            }

            break;

        }

        switch (peek) {

            case '=':
                return new Token(Parser.T_ASSIGN);

            case ';':
                return new Token(Parser.T_SEMICOLON);

            case '(':
                return new Token(Parser.T_LEFT_PAREN);

            case ')':
                return new Token(Parser.T_RIGHT_PAREN);

            case '/':

                if (readChar('/')) {

                    // comment start
                    do {
                        peek = stream.read();
                    } while (peek != -1 && peek != '\n');

                    if (peek == '\n') {
                        line++;
                    }

                }

            default:
                break;

        }

        throw new LexerException("Invalid lexeme at line " + line);

    }

    public int getLine() {
        return line;
    }
}