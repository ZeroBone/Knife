package net.zerobone.knife.grammar;

import net.zerobone.knife.grammar.verification.LeftRecursiveCycleError;
import net.zerobone.knife.grammar.verification.NonTerminalNotDefinedError;
import net.zerobone.knife.grammar.verification.UnreachableNonTerminalsError;
import net.zerobone.knife.grammar.verification.VerificationError;

import java.util.*;

class GrammarVerification {

    private final Grammar grammar;

    private ArrayList<VerificationError> exceptions = new ArrayList<>();

    public GrammarVerification(Grammar grammar) {
        this.grammar = grammar;
    }

    public void verifyNoLeftRecursion() {

        HashSet<Integer> unvisitedSet = new HashSet<>(grammar.productions.keySet());

        HashSet<Integer> visitedAndOnTheStackSet = new HashSet<>();

        HashSet<Integer> visitedAndPoppedOutOfStackSet = new HashSet<>();

        HashMap<Integer, Integer> parentMap = new HashMap<>();

        Stack<Integer> stack = new Stack<>();

        while (!unvisitedSet.isEmpty()) {

            int start = unvisitedSet.iterator().next();

            stack.clear();
            visitedAndOnTheStackSet.clear();
            parentMap.clear(); // actually this is not needed

            stack.push(start);
            visitedAndOnTheStackSet.add(start);
            unvisitedSet.remove(start);
            parentMap.put(start, null);

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

        }

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

    private void verifyNoUnreachableProduction() {

        HashSet<Integer> unreachableProductions = new HashSet<>(grammar.productions.keySet());

        Stack<Integer> stack = new Stack<>();

        stack.push(Grammar.START_SYMBOL_ID);

        do {

            int nonTerminal = stack.pop();

            unreachableProductions.remove(nonTerminal);

            for (InnerProduction production : grammar.productions.get(nonTerminal)) {

                for (InnerSymbol symbol : production.body) {

                    if (symbol.isTerminal()) {
                        continue;
                    }

                    if (!unreachableProductions.contains(symbol.id)) {
                        continue;
                    }

                    stack.push(symbol.id);

                }

            }

        } while (!stack.isEmpty());

        if (unreachableProductions.isEmpty()) {
            return;
        }

        exceptions.add(new UnreachableNonTerminalsError(unreachableProductions));

    }

    public void verify() {

        verifyAllNonterminalsDefined();

        if (!exceptions.isEmpty()) {
            return;
        }

        verifyNoUnreachableProduction();

        if (!exceptions.isEmpty()) {
            return;
        }

        verifyNoLeftRecursion();

    }

    public ArrayList<VerificationError> getExceptions() {
        return exceptions;
    }

}