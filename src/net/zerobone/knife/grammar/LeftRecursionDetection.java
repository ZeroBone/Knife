package net.zerobone.knife.grammar;

import java.util.*;

class LeftRecursionDetection {

    private final Grammar grammar;

    public LeftRecursionDetection(Grammar grammar) {
        this.grammar = grammar;
    }

    public LinkedList<Integer> detect() {

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

                    return cycle;

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

        return null;

    }

}