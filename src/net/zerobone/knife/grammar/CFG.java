package net.zerobone.knife.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class CFG {

    private String startSymbol;

    private HashMap<String, CFGProductions> productions;

    private HashMap<String, HashSet<String>> cachedFirstSets = new HashMap<>();

    public CFG(String startSymbol, CFGProduction startProduction) {

        productions = new HashMap<>(32);

        this.startSymbol = startSymbol;

        productions.put(startSymbol, new CFGProductions(startProduction));

    }

    public void addProduction(String symbol, CFGProduction production) {

        if (!productions.containsKey(symbol)) {
            productions.put(symbol, new CFGProductions(production));
            return;
        }

        productions.get(symbol).addProduction(production);

    }

    public HashSet<String> firstSet(String nonTerminal) {

        if (cachedFirstSets.containsKey(nonTerminal)) {
            return cachedFirstSets.get(nonTerminal);
        }

        CFGProductions nonTerminalProductions = productions.get(nonTerminal);

        if (nonTerminalProductions == null) {
            throw new RuntimeException("Non-terminal " + nonTerminal + " doesn't exist in the grammar.");
        }

        HashSet<String> set = new HashSet<>();

        for (CFGProduction prod : nonTerminalProductions.getProductions()) {

            ArrayList<CFGSymbol> body = prod.getBody();

            if (body.size() == 0) {
                // epsilon production
                set.add("");
                continue;
            }

            for (CFGSymbol symbol : body) {

                if (symbol.isTerminal) {
                    set.add(symbol.sym);
                    break;
                }

                HashSet<String> firstSetOfNonTerminal = firstSet(symbol.sym);

                set.addAll(firstSetOfNonTerminal);

                if (!firstSetOfNonTerminal.contains("")) {
                    // the current nonterminal doesn't contain epsilon, so we don't need to move on to the next terminal
                    break;
                }

                // the current nonterminal is nullable, so it is possible that the next ones appear at the start
                // so we move on to the next symbol in the production

            }

        }

        cachedFirstSets.put(nonTerminal, set);

        return set;

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        Iterator it = productions.entrySet().iterator();

        while (it.hasNext()) {

            HashMap.Entry pair = (HashMap.Entry)it.next();

            sb
                .append(pair.getKey())
                .append(" -> ")
                .append(pair.getValue())
                .append(';');

            // it.remove(); // to avoid a ConcurrentModificationException

            if (it.hasNext()) {
                sb.append('\n');
            }

        }

        return sb.toString();

    }

}