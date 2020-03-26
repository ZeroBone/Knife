package net.zerobone.knife.grammar.verification;

import net.zerobone.knife.grammar.Grammar;

import java.util.Iterator;
import java.util.LinkedList;

public class LeftRecursiveCycleError extends VerificationError {

    public final LinkedList<Integer> cycle;

    public LeftRecursiveCycleError(LinkedList<Integer> cycle) {
        super(true);
        this.cycle = cycle;
    }

    @Override
    public String getMessage(Grammar grammar) {

        StringBuilder sb = new StringBuilder();

        sb.append("Non-terminal(s) ");

        Iterator<Integer> cycleIterator = cycle.iterator();

        appendListOfNonterminals(cycleIterator, sb, grammar);

        sb.append(" form a left-recursive cycle.");

        return sb.toString();

    }

}