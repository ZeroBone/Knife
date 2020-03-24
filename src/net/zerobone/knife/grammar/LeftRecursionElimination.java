package net.zerobone.knife.grammar;

import net.zerobone.knife.utils.BijectiveMap;
import net.zerobone.knife.utils.MatrixOrientedGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

class LeftRecursionElimination {

    private final Grammar grammar;

    LeftRecursionElimination(Grammar grammar) {
        this.grammar = grammar;
    }

    private int[] orderNonTerminals() {

        final int nonTerminalCount = grammar.getNonTerminalCount();

        int nonTerminalCounter = 0;
        BijectiveMap<Integer, Integer> nonTerminalToIndex = new BijectiveMap<>();

        MatrixOrientedGraph graph = new MatrixOrientedGraph(nonTerminalCount);

        for (HashMap.Entry<Integer, ArrayList<InnerProduction>> entry : grammar.productions.entrySet()) {

            int nonTerminalId = entry.getKey();

            Integer nonTerminalIndex = nonTerminalToIndex.mapKey(nonTerminalId);

            if (nonTerminalIndex == null) {

                nonTerminalToIndex.put(nonTerminalId, nonTerminalCounter);

                nonTerminalIndex = nonTerminalCounter;

                nonTerminalCounter++;

            }

            for (InnerProduction production : entry.getValue()) {

                if (production.body.isEmpty()) {
                    continue;
                }

                InnerSymbol firstSymbol = production.body.get(0);

                if (firstSymbol.isTerminal()) {
                    continue;
                }

                int firstSymbolId = firstSymbol.id;

                Integer firstSymbolIndex = nonTerminalToIndex.mapKey(firstSymbolId);

                if (firstSymbolIndex == null) {

                    nonTerminalToIndex.put(firstSymbolId, nonTerminalCounter);

                    firstSymbolIndex = nonTerminalCounter;

                    nonTerminalCounter++;

                }

                // draw an edge from a to b if there is
                // a production of the form a -> b ...
                graph.addEdge(nonTerminalIndex, firstSymbolIndex);

            }

        }

        // now compute the transitive closure of the graph

        graph.transitiveClosure();

        int[] outcomingDegrees = new int[nonTerminalCount];

        for (int i = 0; i < nonTerminalCount; i++) {
            outcomingDegrees[i] = graph.outcomingDegree(i);
        }

        for (int i = 0; i < nonTerminalCount; i++) {

            int maxIndex = i;

            for (int j = i + 1; j < nonTerminalCount; j++) {

                if (outcomingDegrees[j] > outcomingDegrees[maxIndex]) {
                    maxIndex = j;
                }

            }

            if (i != maxIndex) {
                outcomingDegrees[maxIndex] = outcomingDegrees[i];
            }

            // map index to value
            outcomingDegrees[i] = nonTerminalToIndex.mapValue(maxIndex);

        }

        return outcomingDegrees; // at this point this is already an array out of non-terminal ids

    }

    public void eliminate() {

        int[] order = orderNonTerminals();

        System.out.println(Arrays.toString(order));

    }

}