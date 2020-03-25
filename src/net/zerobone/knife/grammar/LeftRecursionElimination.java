package net.zerobone.knife.grammar;

import net.zerobone.knife.utils.BijectiveMap;
import net.zerobone.knife.utils.MatrixOrientedGraph;

import java.util.ArrayList;
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

    private void eliminateDirectLeftRecursion(int nonTerminal) {

        // System.out.println("Searching for direct left recursion for " + nonTerminal + "...");

        ArrayList<InnerProduction> alphaProductions = new ArrayList<>();
        ArrayList<InnerProduction> betaProductions = new ArrayList<>();

        ArrayList<InnerProduction> productions = grammar.productions.get(nonTerminal);

        assert productions != null;

        for (InnerProduction production : productions) {

            if (production.body.isEmpty()) {
                betaProductions.add(production);
                continue;
            }

            InnerSymbol firstSymbol = production.body.get(0);

            if (firstSymbol.id != nonTerminal) {
                betaProductions.add(production);
                continue;
            }

            // first symbol is the non-terminal itself
            // so we found direct left recursion

            assert !firstSymbol.isTerminal();

            alphaProductions.add(production);

        }

        if (alphaProductions.isEmpty()) {
            // no left recursion found
            return;
        }

        // System.out.println("Eliminating direct left recursion for " + nonTerminal + "...");

        productions.clear();

        int newSymbol = grammar.createNonTerminal(nonTerminal);

        for (InnerProduction alphaProduction : alphaProductions) {

            InnerProduction newProduction = new InnerProduction(null);

            assert alphaProduction.body.size() >= 2; // there should be no cycles in the grammar

            for (int i = 1; i < alphaProduction.body.size(); i++) {

                newProduction.body.add(alphaProduction.body.get(i));

            }

            newProduction.body.add(new InnerSymbol(newSymbol, null));

            grammar.addProduction(newSymbol, newProduction);

        }

        // add epsilon-production
        grammar.addProduction(newSymbol, new InnerProduction(null));

        for (InnerProduction betaProduction : betaProductions) {

            betaProduction.code = null;
            betaProduction.body.add(new InnerSymbol(newSymbol, null));

            productions.add(betaProduction);

        }

    }

    private void addBetaIntoAlphaSubstitutingProductions(int alpha, int beta, InnerProduction alphaProduction) {

        // for each production of the form A_j -> beta

        ArrayList<InnerProduction> betaProductions = grammar.productions.get(beta);

        assert betaProductions != null;

        for (InnerProduction betaProduction : betaProductions) {

            String code;

            {
                String alphaProductionArgument = alphaProduction.body.get(0).argumentName;

                if (alphaProductionArgument == null) {
                    code = alphaProduction.code;
                }
                else {
                    code =
                        "Object " + alphaProductionArgument + ";" + "\n" +
                        "{" +
                            "Object v;\n" +
                            betaProduction.code + "\n" +
                            alphaProductionArgument + " = v;\n" +
                        " }\n" +
                        alphaProduction.code;
                }

            }

            InnerProduction newAiProduction = new InnerProduction(code);

            // add beta

            for (InnerSymbol betaSymbol : betaProduction.body) {

                newAiProduction.body.add(new InnerSymbol(betaSymbol.id, betaSymbol.argumentName));

            }

            // add alpha

            for (int i = 1; i < alphaProduction.body.size(); i++) {

                InnerSymbol alphaSymbol = alphaProduction.body.get(i);

                newAiProduction.body.add(new InnerSymbol(alphaSymbol.id, alphaSymbol.argumentName));

            }

            grammar.productions.get(alpha).add(newAiProduction);

        }

    }

    private void substituteAlphaIntoBeta(int alpha, int beta) {

        ArrayList<InnerProduction> alphaProductions = grammar.productions.get(alpha);

        assert alphaProductions != null;

        int alphaProductionsSize = alphaProductions.size();

        for (int i = 0; i < alphaProductionsSize;) {

            InnerProduction alphaProduction = alphaProductions.get(i);

            if (alphaProduction.body.isEmpty()) {
                i++;
                continue;
            }

            InnerSymbol alphaFirstSymbol = alphaProduction.body.get(0);

            if (alphaFirstSymbol.id != beta) {
                i++;
                continue;
            }

            // if the first symbol is not a terminal and is aj
            assert !alphaFirstSymbol.isTerminal();

            // we have a production of the form A_i -> A_j alpha
            // remove A_i -> A_j alpha from the grammar

            alphaProductions.remove(i);
            alphaProductionsSize--;

            addBetaIntoAlphaSubstitutingProductions(alpha, beta, alphaProduction);

        }

    }

    void eliminate() {

        // Paull's algorithm

        int[] nonTerminals = orderNonTerminals();

        for (int i = 0; i < nonTerminals.length; i++) {

            for (int j = 0; j < i; j++) {

                // for every production of the form A_i -> A_j alpha

                int ai = nonTerminals[i];

                int aj = nonTerminals[j];

                substituteAlphaIntoBeta(ai, aj);

            }

            eliminateDirectLeftRecursion(nonTerminals[i]);

        }

    }

}