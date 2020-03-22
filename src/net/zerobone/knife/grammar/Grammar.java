package net.zerobone.knife.grammar;

import net.zerobone.knife.grammar.table.ParsingTable;
import net.zerobone.knife.grammar.table.CFGParsingTableBuilder;

import java.util.*;

public class Grammar {

    // terminals are represented as positive integers, so this one should be negative
    public static final int FIRST_FOLLOW_SET_EPSILON = -1;

    public static final int FOLLOW_SET_EOF = 0; // aka dollar sign

    private int startSymbol;

    private HashMap<Integer, Productions> productions;

    private HashSet<Integer> terminals = new HashSet<>();

    private HashMap<Integer, HashSet<Integer>> cachedFirstSets = new HashMap<>();

    private HashMap<Integer, HashSet<Integer>> cachedFollowSets = new HashMap<>();

    public Grammar(int startSymbol, Production startProduction) {

        productions = new HashMap<>(32);

        this.startSymbol = startSymbol;

        productions.put(startSymbol, new Productions(startProduction));

    }

    public void addProduction(int nonTerminalId, Production production) {

        assert nonTerminalId < 0;

        for (Symbol sym : production.body) {
            if (sym.isTerminal()) {
                terminals.add(sym.id);
            }
        }

        Productions correspondingProductions = productions.get(nonTerminalId);

        if (correspondingProductions == null) {

            productions.put(nonTerminalId, new Productions(production));

            return;

        }

        correspondingProductions.addProduction(production);

    }

    public HashMap<Integer, HashSet<Integer>> computeFirstSets() {

        HashMap<Integer, HashSet<Integer>> firstSets = new HashMap<>();

        for (HashMap.Entry<Integer, Productions> pair : productions.entrySet()) {

            int nonTerminal = pair.getKey();

            firstSets.put(nonTerminal, firstSet(nonTerminal));

        }

        return firstSets;

    }

    private HashSet<Integer> firstSet(int nonTerminal) {

        assert nonTerminal < 0;

        {
            HashSet<Integer> cache = cachedFirstSets.get(nonTerminal);
            if (cache != null) {
                return cache;
            }
        }

        Productions nonTerminalProductions = productions.get(nonTerminal);

        assert nonTerminalProductions != null; // non-terminal should exist

        HashSet<Integer> set = new HashSet<>();

        for (Production prod : nonTerminalProductions.getProductions()) {

            if (prod.body.size() == 0) {
                // epsilon production
                set.add(FIRST_FOLLOW_SET_EPSILON);
                continue;
            }

            for (Symbol symbol : prod.body) {

                if (symbol.isTerminal()) {
                    set.add(symbol.id);
                    break;
                }

                HashSet<Integer> firstSetOfNonTerminal = firstSet(symbol.id);

                set.addAll(firstSetOfNonTerminal);

                if (!firstSetOfNonTerminal.contains(FIRST_FOLLOW_SET_EPSILON)) {
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

    public HashMap<Integer, HashSet<Integer>> computeFollowSets() {

        HashMap<Integer, HashSet<Integer>> followSets = new HashMap<>();

        for (HashMap.Entry<Integer, Productions> pair : productions.entrySet()) {

            int nonTerminal = pair.getKey();

            followSets.put(nonTerminal, followSet(nonTerminal));

        }

        return followSets;

    }

    private HashSet<Integer> followSet(int nonTerminal) {

        assert nonTerminal < 0;

        if (cachedFollowSets.containsKey(nonTerminal)) {
            return cachedFollowSets.get(nonTerminal);
        }

        HashSet<Integer> set = new HashSet<>();

        if (nonTerminal == startSymbol) {
            set.add(FOLLOW_SET_EOF); // add $
        }

        for (HashMap.Entry<Integer, Productions> pair : productions.entrySet()) {

            int productionLabel = pair.getKey();

            if (productionLabel == nonTerminal) {
                // we only look in productions with other labels
                continue;
            }

            Productions thisLabelProductions = pair.getValue();

            for (Production production : thisLabelProductions.getProductions()) {

                for (int i = 0; i < production.body.size(); i++) {

                    Symbol symbol = production.body.get(i);

                    if (/*symbol.isTerminal() || */symbol.id != nonTerminal) {
                        continue;
                    }

                    // we found a production either of the form alpha A beta
                    // or alpha a
                    // examine the next symbol to find out

                    if (i == production.body.size() - 1) {
                        // beta = epsilon
                        // so we are in the alpha A situation
                        set.addAll(followSet(productionLabel));
                        break;
                    }

                    // we are in the alpha A beta

                    Symbol nextSymbol = production.body.get(i + 1);

                    if (nextSymbol.isTerminal()) {
                        // FIRST(terminal) = { terminal }
                        // so we just add the symbol to the set
                        set.add(nextSymbol.id);
                        break;
                    }

                    // nextSymbol (aka beta) is a nonterminal

                    HashSet<Integer> nextSymbolFirstSet = firstSet(nextSymbol.id);

                    if (nextSymbolFirstSet.contains(FIRST_FOLLOW_SET_EPSILON)) {

                        // union with FIRST(beta) \ epsilon
                        set.addAll(nextSymbolFirstSet);
                        set.remove(FIRST_FOLLOW_SET_EPSILON);

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

    public ParsingTable constructParsingTable() {

        final HashMap<Integer, HashSet<Integer>> firstSets = computeFirstSets();

        final HashMap<Integer, HashSet<Integer>> followSets = computeFollowSets();

        final CFGParsingTableBuilder tableBuilder = new CFGParsingTableBuilder(this);

        for (HashMap.Entry<Integer, Productions> pair : productions.entrySet()) {

            int productionLabel = pair.getKey();

            Productions thisLabelProductions = pair.getValue();

            for (Production production : thisLabelProductions.getProductions()) {

                if (production.body.size() == 0) {
                    // epsilon-rule

                    HashSet<Integer> followSet = followSets.get(productionLabel);

                    for (int follow : followSet) {

                        // System.out.println("[1]: Row: " + productionLabel + " Col: " + follow + " Production: " + productionLabel + " -> ;");

                        tableBuilder.write(productionLabel, follow, production);

                    }

                    continue;
                }

                Symbol symbol = production.body.get(0);

                if (symbol.isTerminal()) {

                    // System.out.println("[2]: Row: " + productionLabel + " Col: " + symbol.id + " Production: " + productionLabel + " -> " + production.toString());

                    tableBuilder.write(productionLabel, symbol.id, production);

                    continue;
                }

                // non-terminal

                HashSet<Integer> firstSet = firstSets.get(productionLabel);

                for (int first : firstSet) {

                    if (first == FIRST_FOLLOW_SET_EPSILON) {
                        continue;
                    }

                    // System.out.println("[3]: Row: " + productionLabel + " Col: " + first + " Production: " + productionLabel + " -> " + production.toString());

                    tableBuilder.write(productionLabel, first, production);

                }

            }

        }

        return tableBuilder.getTable();

    }

    public int getNonTerminalCount() {
        return productions.size();
    }

    public int getTerminalCount() {
        return terminals.size();
    }

    public static int nonTerminalToIndex(int nonTerminal) {

        assert nonTerminal < 0;

        return -nonTerminal - 1;

    }

    public static int terminalToIndex(int terminal) {

        assert terminal >= 0; // can be eof

        return terminal;

    }

    public static int indexToNonTerminal(int index) {

        assert index >= 0;

        return -index - 1;

    }

    public static int indexToTerminal(int index) {

        assert index >= 0;

        return index;

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        Iterator<java.util.Map.Entry<Integer, Productions>> it = productions.entrySet().iterator();

        while (it.hasNext()) {

            HashMap.Entry<Integer, Productions> pair = it.next();

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