package net.zerobone.knife.grammar.verification;

import net.zerobone.knife.grammar.Grammar;

import java.util.Iterator;
import java.util.Set;

public class UnreachableNonTerminalsError extends VerificationError {

    private final Set<Integer> unreachableNonTerminals;

    public UnreachableNonTerminalsError(Set<Integer> unreachableNonTerminals) {
        super(false);
        this.unreachableNonTerminals = unreachableNonTerminals;
    }

    @Override
    public String getMessage(Grammar grammar) {

        StringBuilder sb = new StringBuilder();

        sb.append("Non-terminal(s) ");

        Iterator<Integer> nonTerminalIterator = unreachableNonTerminals.iterator();

        assert nonTerminalIterator.hasNext();

        appendListOfNonterminals(nonTerminalIterator, sb, grammar);

        sb.append(" cannot be reached from the start symbol '");
        sb.append(grammar.idToSymbol(Grammar.START_SYMBOL_ID));
        sb.append("'.");

        return sb.toString();

    }

}