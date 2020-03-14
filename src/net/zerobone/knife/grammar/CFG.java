package net.zerobone.knife.grammar;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class CFG {

    private String startSymbol;

    private HashMap<String, CFGProductions> productions;

    private HashMap<String, HashSet<String>> cachedFirstSets = new HashMap<>();

    private HashMap<String, HashSet<String>> cachedFollowSets = new HashMap<>();

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

    public HashMap<String, HashSet<String>> computeFirstSets() {

        HashMap<String, HashSet<String>> firstSets = new HashMap<>();

        for (HashMap.Entry<String, CFGProductions> pair : productions.entrySet()) {

            String nonTerminal = pair.getKey();

            firstSets.put(nonTerminal, firstSet(nonTerminal));

        }

        return firstSets;

    }

    private HashSet<String> firstSet(@NotNull String nonTerminal) {

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

    public HashMap<String, HashSet<String>> computeFollowSets() {

        HashMap<String, HashSet<String>> followSets = new HashMap<>();

        for (HashMap.Entry<String, CFGProductions> pair : productions.entrySet()) {

            String nonTerminal = pair.getKey();

            followSets.put(nonTerminal, followSet(nonTerminal));

        }

        return followSets;

    }

    private HashSet<String> followSet(@NotNull String nonTerminal) {

        if (cachedFollowSets.containsKey(nonTerminal)) {
            return cachedFollowSets.get(nonTerminal);
        }

        HashSet<String> set = new HashSet<>();

        if (nonTerminal.equals(startSymbol)) {
            set.add("$");
        }

        for (HashMap.Entry<String, CFGProductions> pair : productions.entrySet()) {

            String productionLabel = pair.getKey();

            if (productionLabel.equals(nonTerminal)) {
                // we only look in productions with other labels
                continue;
            }

            CFGProductions thisLabelProductions = pair.getValue();

            for (CFGProduction production : thisLabelProductions.getProductions()) {

                ArrayList<CFGSymbol> body = production.getBody();

                for (int i = 0; i < body.size(); i++) {

                    CFGSymbol symbol = body.get(i);

                    if (symbol.isTerminal || !symbol.sym.equals(nonTerminal)) {
                        continue;
                    }

                    // we found a production either of the form alpha A beta
                    // or alpha a
                    // examine the next symbol to find out

                    if (i == body.size() - 1) {
                        // beta = epsilon
                        // so we are in the alpha A situation
                        set.addAll(followSet(productionLabel));
                        break;
                    }

                    // we are in the alpha A beta

                    CFGSymbol nextSymbol = body.get(i + 1);

                    if (nextSymbol.isTerminal) {
                        // FIRST(terminal) = { terminal }
                        // so we just add the symbol to the set
                        set.add(nextSymbol.sym);
                        break;
                    }

                    // nextSymbol (aka beta) is a nonterminal

                    HashSet<String> nextSymbolFirstSet = firstSet(nextSymbol.sym);

                    if (nextSymbolFirstSet.contains("")) {

                        // union with FIRST(beta) \ epsilon
                        set.addAll(nextSymbolFirstSet);
                        set.remove("");

                        // union with FOLLOW(A)
                        set.addAll(followSet(productionLabel));

                    }
                    else {

                        set.addAll(nextSymbolFirstSet);
                        set.remove(""); // epsilon could be in the first set, but epsilon can never be in the follow set

                    }

                    // set.addAll(nextSymbolFirstSet);
                    // set.remove(""); // epsilon could be in the first set, but epsilon can never be in the follow set

                    break;

                }

            }

        }

        cachedFollowSets.put(nonTerminal, set);

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