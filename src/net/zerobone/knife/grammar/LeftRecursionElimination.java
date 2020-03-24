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

        final String newSymbolArgumentName = "_knife_lre_" + (-newSymbol);

        for (InnerProduction alphaProduction : alphaProductions) {

            InnerProduction newProduction;

            {
                // build code
                StringBuilder csb = new StringBuilder();
                csb.append(newSymbolArgumentName);
                csb.append(".push(new Object[] {");

                int bodySize = alphaProduction.body.size();
                for (int i = 1;;i++) {
                    InnerSymbol symbol = alphaProduction.body.get(i);
                    if (symbol.argumentName == null) {
                        continue;
                    }
                    csb.append(symbol.argumentName);
                    if (i == bodySize - 1) {
                        break;
                    }
                    csb.append(',');
                }

                csb.append("});");
                // csb.append('\n');
                csb.append("v = ");
                csb.append(newSymbolArgumentName);
                csb.append(";");

                newProduction = new InnerProduction(csb.toString());
            }

            assert alphaProduction.body.size() >= 2; // there should be no cycles in the grammar

            for (int i = 1; i < alphaProduction.body.size(); i++) {

                newProduction.body.add(alphaProduction.body.get(i));

            }

            newProduction.body.add(new InnerSymbol(newSymbol, newSymbolArgumentName));

            grammar.addProduction(newSymbol, newProduction);

        }

        // add epsilon-production
        grammar.addProduction(newSymbol, new InnerProduction("v = new Stack<Object>();"));

        for (InnerProduction betaProduction : betaProductions) {

            {
                StringBuilder csb = new StringBuilder();
                csb.append(newSymbolArgumentName);
                csb.append(".push(new Object[] {");
                csb.append("});");
                betaProduction.code = csb.toString();
            }

            betaProduction.body.add(new InnerSymbol(newSymbol, newSymbolArgumentName));

            productions.add(betaProduction);

        }

    }

    private void substituteAjintoAi(int ai, int aj, InnerProduction ajAlphaProduction) {

        // for each production of the form A_j -> beta

        ArrayList<InnerProduction> ajProductions = grammar.productions.get(aj);

        assert ajProductions != null;

        for (InnerProduction ajProduction : ajProductions) {

            ArrayList<InnerSymbol> beta = ajProduction.body;

            InnerProduction newAiProduction = new InnerProduction(null);

            // add beta

            for (InnerSymbol betaSymbol : beta) {

                newAiProduction.body.add(new InnerSymbol(betaSymbol.id, betaSymbol.argumentName));

            }

            // add alpha

            for (int i = 1; i < ajAlphaProduction.body.size(); i++) {

                InnerSymbol alphaSymbol = ajAlphaProduction.body.get(i);

                newAiProduction.body.add(new InnerSymbol(alphaSymbol.id, alphaSymbol.argumentName));

            }

            grammar.productions.get(ai).add(newAiProduction);

        }

    }

    public void eliminate() {

        // Paull's algorithm

        int[] nonTerminals = orderNonTerminals();

        for (int i = 0; i < nonTerminals.length; i++) {

            for (int j = 0; j < i; j++) {

                // for every production of the form A_i -> A_j alpha

                int ai = nonTerminals[i];

                int aj = nonTerminals[j];

                ArrayList<InnerProduction> aiProductions = grammar.productions.get(ai);

                assert aiProductions != null;

                int aiProductionsSize = aiProductions.size();

                for (int k = 0; k < aiProductionsSize; k++) {

                    InnerProduction aiProduction = aiProductions.get(k);

                    if (aiProduction.body.isEmpty()) {
                        continue;
                    }

                    InnerSymbol firstSymbol = aiProduction.body.get(0);

                    if (firstSymbol.id != aj) {

                        continue;

                    }

                    // if the first symbol is not a terminal and is aj

                    assert !firstSymbol.isTerminal();

                    // we have a production of the form A_i -> A_j alpha
                    // remove A_i -> A_j alpha from the grammar

                    aiProductions.remove(k);
                    k++;

                    substituteAjintoAi(ai, aj, aiProduction);

                }

            }

            eliminateDirectLeftRecursion(nonTerminals[i]);

        }

    }

}