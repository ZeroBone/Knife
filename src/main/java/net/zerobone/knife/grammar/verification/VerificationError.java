package net.zerobone.knife.grammar.verification;

import net.zerobone.knife.grammar.Grammar;

import java.util.Iterator;

public abstract class VerificationError {

    public final boolean canBeAutoFixed;

    public VerificationError(boolean canBeAutoFixed) {
        this.canBeAutoFixed = canBeAutoFixed;
    }

    public abstract String getMessage(Grammar grammar);

    protected static void appendListOfNonterminals(Iterator<Integer> nonTerminalIterator, StringBuilder sb, Grammar grammar) {

        assert nonTerminalIterator.hasNext();

        while (true) {

            int nonTerminal = nonTerminalIterator.next();

            sb.append('\'');
            sb.append(grammar.idToSymbol(nonTerminal));
            sb.append('\'');

            if (!nonTerminalIterator.hasNext()) {
                break;
            }

            sb.append(", ");

        }

    }

}