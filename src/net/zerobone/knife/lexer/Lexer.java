package net.zerobone.knife.lexer;

import net.zerobone.knife.lexer.tokens.CodeToken;
import net.zerobone.knife.lexer.tokens.IdToken;
import net.zerobone.knife.lexer.tokens.Token;
import net.zerobone.knife.parser.Parser;

import java.io.IOException;
import java.io.InputStream;

public class Lexer {

    private static final int EOF = -1;

    private final InputStream stream;

    private int line = 1;

    private int current;

    private boolean peeking = false;

    public Lexer(InputStream stream) {
        this.stream = stream;
    }

    private void readChar() throws IOException {

        if (peeking) {
            peeking = false;
            return;
        }

        current = stream.read();

        if (current == '\n') {
            line++;
        }

    }

    private void peekChar() throws IOException {

        // assert !peeking;

        peeking = true;

        current = stream.read();

        if (current == '\n') {
            line++;
        }

    }

    private void advancePeek() {
        assert peeking;
        peeking = false;
    }

    private Token constructPrimitiveToken(int type) {
        return new Token(line, type);
    }

    public Token lex() throws IOException, LexerException {

        for (;;) {

            readChar();

            if (current == -1) {
                return constructPrimitiveToken(Parser.T_EOF);
            }

            if (current == ' ' || current == '\n' || current == '\t' || current == '\r') {
                continue;
            }
            else if (current == '/') {

                peekChar();

                if (current == '/') {

                    advancePeek();

                    // single-line comment start

                    do {
                        readChar();
                    } while (current != '\n' && current != EOF);

                }

                continue;

            }

            break;

        }

        switch (current) {

            case '=':
                return constructPrimitiveToken(Parser.T_ASSIGN);

            case ';':
                return constructPrimitiveToken(Parser.T_SEMICOLON);

            case '(':
                return constructPrimitiveToken(Parser.T_LEFT_PAREN);

            case ')':
                return constructPrimitiveToken(Parser.T_RIGHT_PAREN);

            case '{': {
                // code block

                int nestingLevel = 0;

                StringBuilder sb = new StringBuilder();

                for (;;) {

                    readChar();

                    if (current == EOF) {
                        break;
                    }

                    if (current == '{') {
                        nestingLevel++;
                    }
                    else if (current == '}') {

                        if (nestingLevel == 0) {
                            return new CodeToken(line, sb.toString());
                        }

                        nestingLevel--;

                    }

                    sb.append((char)current);

                }

                throw new LexerException("Invalid code block at line " + line);

            }

            default:
                break;

        }

        if (Character.isLetter(current)) {

            StringBuilder sb = new StringBuilder();

            do {

                sb.append((char)current);

                peekChar();

                if (current == EOF) {
                    break;
                }

            } while (Character.isLetterOrDigit((char)current) || current == '_');

            return new IdToken(line, sb.toString());

        }

        throw new LexerException("Invalid start of lexeme '" + (char)current + "' at line " + line);

    }

}