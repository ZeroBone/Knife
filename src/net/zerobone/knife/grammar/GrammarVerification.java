package net.zerobone.knife.grammar;

import net.zerobone.knife.grammar.verification.LeftRecursiveCycleError;
import net.zerobone.knife.grammar.verification.NonTerminalNotDefinedError;
import net.zerobone.knife.grammar.verification.UnreachableNonTerminalError;
import net.zerobone.knife.grammar.verification.VerificationError;

import java.util.*;

class GrammarVerification {

    private final Grammar grammar;

    private ArrayList<VerificationError> exceptions = new ArrayList<>();

    public GrammarVerification(Grammar grammar) {
        this.grammar = grammar;
    }

    public void verifyLeftRecursionAndUnreachableProductions() {

        HashSet<Integer> unvisitedSet = new HashSet<>(grammar.productions.keySet());

        HashSet<Integer> visitedAndOnTheStackSet = new HashSet<>();

        HashSet<Integer> visitedAndPoppedOutOfStackSet = new HashSet<>();

        HashMap<Integer, Integer> parentMap = new HashMap<>();

        Stack<Integer> stack = new Stack<>();

        // start from the start symbol

        stack.push(Grammar.START_SYMBOL_ID);
        visitedAndOnTheStackSet.add(Grammar.START_SYMBOL_ID);
        unvisitedSet.remove(Grammar.START_SYMBOL_ID);
        parentMap.put(Grammar.START_SYMBOL_ID, null);

        // dfs

        while (!stack.isEmpty()) {

            int currentNonTerminal = stack.peek();

            // find adjacent non-terminal

            int adjacentNonTerminal = 0;

            for (InnerProduction production : grammar.productions.get(currentNonTerminal)) {

                if (production.body.isEmpty()) {
                    continue;
                }

                InnerSymbol firstSymbol = production.body.get(0);

                if (firstSymbol.isTerminal()) {
                    continue;
                }

                if (visitedAndPoppedOutOfStackSet.contains(firstSymbol.id)) {
                    continue;
                }

                if (visitedAndOnTheStackSet.contains(firstSymbol.id)) {
                    // we found a cycle

                    int cycleEnd = firstSymbol.id;

                    int currentNode = currentNonTerminal;

                    LinkedList<Integer> cycle = new LinkedList<>();

                    cycle.addFirst(cycleEnd);
                    cycle.addFirst(currentNode);

                    while (currentNode != cycleEnd) {
                        currentNode = parentMap.get(currentNode);
                        cycle.addFirst(currentNode);
                    }

                    exceptions.add(new LeftRecursiveCycleError(cycle));

                    // cycle ready
                    // make sure there are no nodes that were not visited

                    if (unvisitedSet.isEmpty()) {
                        return;
                    }

                    exceptions.add(new UnreachableNonTerminalError(unvisitedSet));

                    return;

                }

                adjacentNonTerminal = firstSymbol.id;

            }

            if (adjacentNonTerminal == 0) {

                // didn't find any other node

                visitedAndOnTheStackSet.remove(currentNonTerminal);

                visitedAndPoppedOutOfStackSet.add(currentNonTerminal);

                stack.pop();

                continue;
            }

            stack.push(adjacentNonTerminal);

            unvisitedSet.remove(adjacentNonTerminal);

            visitedAndOnTheStackSet.add(adjacentNonTerminal);

            parentMap.put(adjacentNonTerminal, currentNonTerminal);

        }

        // make sure there are no nodes that were not visited

        if (unvisitedSet.isEmpty()) {
            return;
        }

        exceptions.add(new UnreachableNonTerminalError(unvisitedSet));

    }

    private void verifyAllNonterminalsDefined() {

        for (ArrayList<InnerProduction> productions : grammar.productions.values()) {

            for (InnerProduction production : productions) {

                for (InnerSymbol symbol : production.body) {

                    if (symbol.isTerminal()) {
                        continue;
                    }

                    if (!grammar.productions.containsKey(symbol.id)) {
                        exceptions.add(new NonTerminalNotDefinedError(symbol.id));
                    }

                }

            }

        }

    }

    public void verify() {

        verifyAllNonterminalsDefined();

        if (!exceptions.isEmpty()) {
            return;
        }

        verifyLeftRecursionAndUnreachableProductions();

    }

    public ArrayList<VerificationError> getExceptions() {
        return exceptions;
    }

}