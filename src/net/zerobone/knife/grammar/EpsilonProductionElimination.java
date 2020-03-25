package net.zerobone.knife.grammar;

import java.util.ArrayList;
import java.util.Map;

class EpsilonProductionElimination {

    private final Grammar grammar;

    EpsilonProductionElimination(Grammar grammar) {
        this.grammar = grammar;
    }

    private int findNonTerminalWithEpsilonProductionAndRemoveIt() {

        for (Map.Entry<Integer, ArrayList<InnerProduction>> entry : grammar.productions.entrySet()) {

            int nonTerminal = entry.getKey();

            ArrayList<InnerProduction> productions = entry.getValue();

            for (int i = 0; i < productions.size(); i++) {

                InnerProduction production = productions.get(i);

                if (production.body.isEmpty()) {

                    productions.remove(i);

                    return nonTerminal;

                }

            }

        }

        return 0;

    }

    private ArrayList<ArrayList<InnerSymbol>> splitIntoPartsByNonTerminal(InnerProduction production, int nonTerminal) {

        ArrayList<ArrayList<InnerSymbol>> parts = new ArrayList<>();

        parts.add(new ArrayList<>());

        for (InnerSymbol symbol : production.body) {

            if (symbol.id != nonTerminal) {
                // append this to previous part
                parts.get(parts.size() - 1).add(symbol);
                continue;
            }

            assert !symbol.isTerminal();

            if (parts.get(parts.size() - 1).isEmpty()) {
                continue;
            }

            parts.add(new ArrayList<>());

        }

        return parts;

    }

    private boolean containsNonTerminal(InnerProduction production, int nonTerminal) {

        for (InnerSymbol symbol : production.body) {

            if (symbol.id == nonTerminal) {

                assert !symbol.isTerminal();

                return true;

            }

        }

        return false;

    }

    private ArrayList<InnerProduction> createNewProductions(ArrayList<ArrayList<InnerSymbol>> parts, int nonTerminal) {

        ArrayList<InnerProduction> newProductions = new ArrayList<>();

        int combinations = 1 << (parts.size() - 1);

        // System.out.println("Combinations: " + combinations);

        for (int mask = 1; mask < combinations; mask++) {

            InnerProduction newProduction = new InnerProduction(null);

            for (int part = 0; part < parts.size(); part++) {

                ArrayList<InnerSymbol> partSymbols = parts.get(part);

                newProduction.body.addAll(partSymbols);

                if (part != parts.size() - 1) {

                    // after this part either epsilon or the nonTerminal inner symbol must be inserted

                    if ((1 << (parts.size() - 2 - part) & mask) != 0) {
                        // insert epsilon
                        continue;
                    }

                    // insert non-terminal

                    newProduction.body.add(new InnerSymbol(nonTerminal, null));

                }

            }

            newProductions.add(newProduction);

        }

        return newProductions;

    }

    void eliminate() {

        int nonTerminal;

        while ((nonTerminal = findNonTerminalWithEpsilonProductionAndRemoveIt()) != 0) {

            // find all productions containing the found non-terminal

            for (ArrayList<InnerProduction> productions : grammar.productions.values()) {

                final int productionsCount = productions.size();

                for (int i = 0; i < productionsCount; i++) {

                    InnerProduction production = productions.get(i);

                    if (!containsNonTerminal(production, nonTerminal)) {
                        continue;
                    }

                    ArrayList<ArrayList<InnerSymbol>> parts = splitIntoPartsByNonTerminal(production, nonTerminal);

                    productions.addAll(createNewProductions(parts, nonTerminal));

                }

            }

        }

    }

}