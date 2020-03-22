package net.zerobone.knife.grammar;

import net.zerobone.knife.grammar.table.ParsingTable;
import net.zerobone.knife.grammar.table.ParsingTableBuilder;
import net.zerobone.knife.grammar.table.ParsingTableProduction;

import java.util.*;

public class Grammar {

    private String startSymbol;

    private HashMap<String, Productions> productions;

    private HashMap<String, HashSet<String>> cachedFirstSets = new HashMap<>();

    private HashMap<String, HashSet<String>> cachedFollowSets = new HashMap<>();

    public Grammar(String startSymbol, Production startProduction) {

        productions = new HashMap<>(32);

        this.startSymbol = startSymbol;

        productions.put(startSymbol, new Productions(startProduction));

    }

    public void addProduction(String symbol, Production production) {

        if (!productions.containsKey(symbol)) {
            productions.put(symbol, new Productions(production));
            return;
        }

        productions.get(symbol).addProduction(production);

    }

    public HashMap<String, HashSet<String>> computeFirstSets() {

        HashMap<String, HashSet<String>> firstSets = new HashMap<>();

        for (HashMap.Entry<String, Productions> pair : productions.entrySet()) {

            String nonTerminal = pair.getKey();

            firstSets.put(nonTerminal, firstSet(nonTerminal));

        }

        return firstSets;

    }

    private HashSet<String> firstSet(String nonTerminal) {

        assert nonTerminal != null;

        if (cachedFirstSets.containsKey(nonTerminal)) {
            return cachedFirstSets.get(nonTerminal);
        }

        Productions nonTerminalProductions = productions.get(nonTerminal);

        if (nonTerminalProductions == null) {
            throw new RuntimeException("Non-terminal " + nonTerminal + " doesn't exist in the grammar.");
        }

        HashSet<String> set = new HashSet<>();

        for (Production prod : nonTerminalProductions.getProductions()) {

            ArrayList<Symbol> body = prod.getBody();

            if (body.size() == 0) {
                // epsilon production
                set.add("");
                continue;
            }

            for (Symbol symbol : body) {

                if (symbol.isTerminal) {
                    set.add(symbol.id);
                    break;
                }

                HashSet<String> firstSetOfNonTerminal = firstSet(symbol.id);

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

        for (HashMap.Entry<String, Productions> pair : productions.entrySet()) {

            String nonTerminal = pair.getKey();

            followSets.put(nonTerminal, followSet(nonTerminal));

        }

        return followSets;

    }

    private HashSet<String> followSet(String nonTerminal) {

        assert nonTerminal != null;

        if (cachedFollowSets.containsKey(nonTerminal)) {
            return cachedFollowSets.get(nonTerminal);
        }

        HashSet<String> set = new HashSet<>();

        if (nonTerminal.equals(startSymbol)) {
            set.add("$");
        }

        for (HashMap.Entry<String, Productions> pair : productions.entrySet()) {

            String productionLabel = pair.getKey();

            if (productionLabel.equals(nonTerminal)) {
                // we only look in productions with other labels
                continue;
            }

            Productions thisLabelProductions = pair.getValue();

            for (Production production : thisLabelProductions.getProductions()) {

                ArrayList<Symbol> body = production.getBody();

                for (int i = 0; i < body.size(); i++) {

                    Symbol symbol = body.get(i);

                    if (symbol.isTerminal || !symbol.id.equals(nonTerminal)) {
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

                    Symbol nextSymbol = body.get(i + 1);

                    if (nextSymbol.isTerminal) {
                        // FIRST(terminal) = { terminal }
                        // so we just add the symbol to the set
                        set.add(nextSymbol.id);
                        break;
                    }

                    // nextSymbol (aka beta) is a nonterminal

                    HashSet<String> nextSymbolFirstSet = firstSet(nextSymbol.id);

                    if (nextSymbolFirstSet.contains("")) {

                        // union with FIRST(beta) \ epsilon
                        set.addAll(nextSymbolFirstSet);
                        set.remove("");

                        // union with FOLLOW(A)
                        set.addAll(followSet(productionLabel));

                    }
                    else {

                        set.addAll(nextSymbolFirstSet);
                        // we don't need to remove epsilon as we already handled this case

                    }

                    break;

                }

            }

        }

        cachedFollowSets.put(nonTerminal, set);

        return set;

    }

    private SymbolMapping mapSymbols() {

        int terminalCounter = 1;
        int nonTerminalCounter = -1;

        HashMap<String, Integer> symbolToId = new HashMap<>();
        HashMap<Integer, String> idToSymbol = new HashMap<>();

        for (HashMap.Entry<String, Productions> entry : productions.entrySet()) {

            String thisLabel = entry.getKey();

            Productions thisLabelProductions = entry.getValue();

            if (!symbolToId.containsKey(thisLabel)) {

                // create entry for left-side nonterminal

                symbolToId.put(thisLabel, nonTerminalCounter);
                idToSymbol.put(nonTerminalCounter, thisLabel);

                nonTerminalCounter--;
            }

            for (Production production : thisLabelProductions.getProductions()) {

                ArrayList<Symbol> body = production.getBody();

                for (Symbol symbol : body) {

                    if (symbolToId.containsKey(symbol.id)) {
                        continue;
                    }

                    if (symbol.isTerminal) {

                        symbolToId.put(symbol.id, terminalCounter);
                        idToSymbol.put(terminalCounter, symbol.id);

                        terminalCounter++;

                    }
                    else {

                        symbolToId.put(symbol.id, nonTerminalCounter);
                        idToSymbol.put(nonTerminalCounter, symbol.id);

                        nonTerminalCounter--;

                    }

                }

            }

        }

        return new SymbolMapping(
            terminalCounter,
            -nonTerminalCounter - 1,
            startSymbol,
            symbolToId,
            idToSymbol
        );

    }

    public ParsingTable constructParsingTable() {

        final HashMap<String, HashSet<String>> firstSets = computeFirstSets();

        final HashMap<String, HashSet<String>> followSets = computeFollowSets();

        final SymbolMapping mapping = mapSymbols();

        final ParsingTableBuilder tableBuilder = new ParsingTableBuilder(mapping);

        for (HashMap.Entry<String, Productions> pair : productions.entrySet()) {

            String productionLabel = pair.getKey();

            Productions thisLabelProductions = pair.getValue();

            for (Production production : thisLabelProductions.getProductions()) {

                ArrayList<Symbol> body = production.getBody();

                if (body.size() == 0) {
                    // epsilon-rule

                    HashSet<String> followSet = followSets.get(productionLabel);

                    for (String follow : followSet) {

                        // System.out.println("[1]: Row: " + productionLabel + " Col: " + follow + " Production: " + productionLabel + " -> ;");

                        tableBuilder.write(productionLabel, follow, new ParsingTableProduction(productionLabel, new ArrayList<>(), production.getCode()));

                    }

                    continue;
                }

                Symbol symbol = body.get(0);

                if (symbol.isTerminal) {

                    // System.out.println("[2]: Row: " + productionLabel + " Col: " + symbol.id + " Production: " + productionLabel + " -> " + production.toString());

                    tableBuilder.write(productionLabel, symbol.id, new ParsingTableProduction(productionLabel, production.getBody(), production.getCode()));

                    continue;
                }

                // non-terminal

                HashSet<String> firstSet = firstSets.get(productionLabel);

                for (String first : firstSet) {

                    if (first.isEmpty()) {
                        continue;
                    }

                    // System.out.println("[3]: Row: " + productionLabel + " Col: " + first + " Production: " + productionLabel + " -> " + production.toString());

                    tableBuilder.write(productionLabel, first, new ParsingTableProduction(productionLabel, production.getBody(), production.getCode()));

                }

            }

        }

        return tableBuilder.getTable();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        Iterator<java.util.Map.Entry<String, Productions>> it = productions.entrySet().iterator();

        while (it.hasNext()) {

            HashMap.Entry<String, Productions> pair = it.next();

            sb
                .append(pair.getKey())
                .append(" -> ")
                .append(pair.getValue())
                .append(';');

            if (it.hasNext()) {
                sb.append('\n');
            }

        }

        return sb.toString();

    }

}