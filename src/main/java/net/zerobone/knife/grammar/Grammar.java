package net.zerobone.knife.grammar;

import net.zerobone.knife.grammar.verification.VerificationError;
import net.zerobone.knife.grammar.table.ParsingTable;
import net.zerobone.knife.utils.BijectiveMap;

import java.util.*;

/**
 * Grammar class.
 *
 * Invariants:
 * 1) The set of terminals cannot change.
 * 2) The set of non-terminals may change.
 */
public class Grammar {

    private BijectiveMap<String, Integer> symbolsMap = new BijectiveMap<>();

    public static final int START_SYMBOL_ID = -1;

    // terminals are represented as positive integers, so this one should be negative
    public static final int FIRST_FOLLOW_SET_EPSILON = -1; // aka empty string

    public static final int FOLLOW_SET_EOF = 0; // aka dollar sign

    private int nonTerminalCounter = -2;

    private int terminalCounter = 1;

    HashMap<Integer, ArrayList<InnerProduction>> productions = new HashMap<>();

    private HashMap<Integer, HashSet<Integer>> firstSets = null;

    private HashMap<Integer, HashSet<Integer>> followSets = null;

    public Grammar(String startSymbol, Production startProduction) {

        symbolsMap.put(startSymbol, START_SYMBOL_ID);

        createFirstProduction(START_SYMBOL_ID, convertProduction(startProduction));

    }

    public boolean symbolDefined(String symbol) {
        return symbolsMap.containsKey(symbol);
    }

    public String idToSymbol(int id) {
        assert id != 0 : "eof-to-symbol convertion is restricted";
        assert symbolsMap.mapValue(id) != null : "id " + id + " is not defined";
        return symbolsMap.mapValue(id);
    }

    private InnerSymbol convertSymbol(Symbol symbol) {

        Integer symbolId = symbolsMap.mapKey(symbol.id);

        if (symbolId == null) {

            if (symbol.isTerminal) {

                symbolId = terminalCounter;

                symbolsMap.put(symbol.id, terminalCounter);

                terminalCounter++;

            }
            else {

                symbolId = nonTerminalCounter;

                symbolsMap.put(symbol.id, nonTerminalCounter);

                nonTerminalCounter--;

            }

        }

        return new InnerSymbol(symbolId, symbol.argumentName);

    }

    private InnerProduction convertProduction(Production production) {

        InnerProduction innerProduction = new InnerProduction(production.getCode());

        for (Symbol symbol : production.getBody()) {
            innerProduction.body.add(convertSymbol(symbol));
        }

        return innerProduction;

    }

    private void createFirstProduction(int id, InnerProduction production) {

        ArrayList<InnerProduction> createdProductions = new ArrayList<>();

        createdProductions.add(production);

        productions.put(id, createdProductions);

    }

    public void addProduction(String symbol, Production production) {

        Integer symbolId = symbolsMap.mapKey(symbol);

        if (symbolId == null) {

            // no such symbol

            symbolsMap.put(symbol, nonTerminalCounter);

            createFirstProduction(nonTerminalCounter, convertProduction(production));

            nonTerminalCounter--;

            return;

        }

        // symbol already exists
        // but it doesn't mean the production exists

        ArrayList<InnerProduction> correspondingProductions = productions.get(symbolId);

        if (correspondingProductions == null) {

            createFirstProduction(symbolId, convertProduction(production));

            return;

        }

        correspondingProductions.add(convertProduction(production));

    }

    int createNonTerminal(int analogyNonTerminal) {

        String newSymbol = idToSymbol(analogyNonTerminal) + "'";

        if (symbolsMap.containsKey(newSymbol)) {

            StringBuilder sb = new StringBuilder(newSymbol);

            do {
                sb.append('\'');
                newSymbol = sb.toString();
            } while (symbolsMap.containsKey(newSymbol));

        }

        symbolsMap.put(newSymbol, nonTerminalCounter);

        return nonTerminalCounter--;

    }

    void addProduction(int symbolId, InnerProduction production) {

        ArrayList<InnerProduction> correspondingProductions = productions.get(symbolId);

        if (correspondingProductions == null) {

            createFirstProduction(symbolId, production);

            return;

        }

        correspondingProductions.add(production);

    }

    // first & follow

    public HashMap<Integer, HashSet<Integer>> getFirstSets() {

        if (firstSets == null) {
            computeFirstSets();
        }

        return firstSets;

    }

    private void computeFirstSets() {

        firstSets = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<InnerProduction>> pair : productions.entrySet()) {

            int nonTerminal = pair.getKey();

            firstSets.put(nonTerminal, initializeFirstSet(nonTerminal));

        }

        while (true) {
            if (!updateFirstSets()) {
                break;
            }
        }

    }

    private HashSet<Integer> initializeFirstSet(int nonTerminal) {

        HashSet<Integer> set = new HashSet<>();

        for (InnerProduction production : productions.get(nonTerminal)) {

            if (production.body.isEmpty()) {

                set.add(FIRST_FOLLOW_SET_EPSILON);

                continue;

            }

            InnerSymbol firstSymbol = production.body.get(0);

            if (firstSymbol.isTerminal()) {
                set.add(firstSymbol.id);
            }

        }

        return set;

    }

    private boolean updateFirstSets() {

        boolean modified = false;

        for (Map.Entry<Integer, ArrayList<InnerProduction>> pair : productions.entrySet()) {

            int label = pair.getKey();

            HashSet<Integer> labelFirstSet = firstSets.get(label);

            ArrayList<InnerProduction> thisLabelProductions = pair.getValue();

            for (InnerProduction production : thisLabelProductions) {

                ArrayList<InnerSymbol> body = production.body;

                if (body.isEmpty()) {
                    continue;
                }

                for (InnerSymbol symbol : body) {

                    if (symbol.isTerminal()) {
                        if (labelFirstSet.add(symbol.id)) {
                            modified = true;
                        }
                        break;
                    }

                    // non-terminal

                    HashSet<Integer> currentFirstSet = firstSets.get(symbol.id);

                    if (labelFirstSet.addAll(currentFirstSet)) {
                        modified = true;
                    }

                    if (!currentFirstSet.contains(FIRST_FOLLOW_SET_EPSILON)) {
                        // the current nonterminal doesn't contain epsilon, so we don't need to move on to the next terminal
                        break;
                    }

                    // the current nonterminal is nullable, so it is possible that the next ones appear at the start
                    // so we move on to the next symbol in the production

                }

            }

        }

        return modified;

    }

    // follow sets

    public HashMap<Integer, HashSet<Integer>> getFollowSets() {

        if (followSets != null) {
            return followSets; // already cached
        }

        // we need to compute first sets in order to compute follow sets

        if (firstSets == null) {
            computeFirstSets();
        }

        assert followSets == null;

        computeFollowSets();

        return followSets;

    }

    private void computeFollowSets() {

        followSets = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<InnerProduction>> pair : productions.entrySet()) {

            int nonTerminal = pair.getKey();

            followSets.put(nonTerminal, initializeFollowSet(nonTerminal));

        }

        followSets
            .get(START_SYMBOL_ID)
            .add(FOLLOW_SET_EOF);

        while (true) {
            if (!updateFollowSets()) {
                break;
            }
        }

    }

    private HashSet<Integer> initializeFollowSet(int nonTerminal) {

        HashSet<Integer> set = new HashSet<>();

        for (ArrayList<InnerProduction> currentProductions : productions.values()) {

            for (InnerProduction production : currentProductions) {

                for (int i = 0; i < production.body.size();) {

                    InnerSymbol symbol = production.body.get(i);

                    if (symbol.id != nonTerminal) {
                        i++;
                        continue;
                    }

                    assert !symbol.isTerminal();

                    if (i == production.body.size() - 1) {
                        // epsilon tokens are never present in follow sets
                        break;
                    }

                    InnerSymbol nextSymbol = production.body.get(i + 1);

                    if (nextSymbol.isTerminal()) {
                        set.add(nextSymbol.id);
                        i += 2;
                    }
                    else {
                        i++;
                    }

                }

            }

        }

        return set;

    }

    private boolean updateFollowSets() {

        boolean modified = false;

        for (Map.Entry<Integer, ArrayList<InnerProduction>> pair : productions.entrySet()) {

            int productionLabel = pair.getKey();

            ArrayList<InnerProduction> thisLabelProductions = pair.getValue();

            for (InnerProduction production : thisLabelProductions) {

                ArrayList<InnerSymbol> body = production.body;

                for (int i = 0; i < body.size();) {

                    InnerSymbol symbol = body.get(i);

                    if (symbol.isTerminal()) {
                        i++;
                        continue;
                    }

                    // we found a production either of the form alpha A beta
                    // or alpha A
                    // examine the next symbol to find out

                    if (i == body.size() - 1) {
                        // beta = epsilon
                        // so we are in the alpha A situation

                        HashSet<Integer> followSet = followSets.get(symbol.id);

                        // System.out.println("Adding followset for " + productionLabel + " -> " + production.toString(this));
                        if (followSet.addAll(followSets.get(productionLabel))) {
                            modified = true;
                        }

                        break;
                    }

                    // we are in the alpha A beta

                    InnerSymbol nextSymbol = body.get(i + 1);

                    if (nextSymbol.isTerminal()) {
                        i += 2;
                        continue;
                    }

                    // nextSymbol (aka beta) is a nonterminal

                    HashSet<Integer> nextSymbolFirstSet = firstSets.get(nextSymbol.id);

                    HashSet<Integer> followSet = followSets.get(symbol.id);

                    int oldSize = followSet.size();

                    // check if there is epsilon in the set
                    if (nextSymbolFirstSet.contains(FIRST_FOLLOW_SET_EPSILON)) {

                        // union with FIRST(beta) \ epsilon
                        followSet.addAll(nextSymbolFirstSet);
                        followSet.remove(FIRST_FOLLOW_SET_EPSILON);

                        // union with FOLLOW(A)
                        followSet.addAll(followSets.get(productionLabel));

                    }
                    else {

                        followSet.addAll(nextSymbolFirstSet);
                        // we don't need to remove epsilon as we already handled this case

                    }

                    if (followSet.size() > oldSize) {
                        modified = true;
                    }

                    i++;

                }

            }

        }

        return modified;

    }

    // end first & follow set methods

    public int getTerminalCount() {
        return terminalCounter - 1;
    }

    public int getNonTerminalCount() {
        return productions.size();
    }

    public ParsingTable constructParsingTable() {

        final HashMap<Integer, HashSet<Integer>> firstSets = getFirstSets();

        final HashMap<Integer, HashSet<Integer>> followSets = getFollowSets();

        final ParsingTableBuilder tableBuilder = new ParsingTableBuilder(this);

        for (Map.Entry<Integer, ArrayList<InnerProduction>> pair : productions.entrySet()) {

            int productionLabel = pair.getKey();

            ArrayList<InnerProduction> thisLabelProductions = pair.getValue();

            for (InnerProduction production : thisLabelProductions) {

                if (production.body.size() == 0) {
                    // epsilon-rule

                    HashSet<Integer> followSet = followSets.get(productionLabel);

                    for (int follow : followSet) {

                        // System.out.println("[1]: Row: " + productionLabel + " Col: " + follow + " Production: " + productionLabel + " -> ;");

                        tableBuilder.write(productionLabel, follow, production);

                    }

                    continue;
                }

                InnerSymbol symbol = production.body.get(0);

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

        // write synchronyzing tokens for error recovery

        for (int nonTerminal : productions.keySet()) {

            for (int follow : followSets.get(nonTerminal)) {

                tableBuilder.writeSynchronize(nonTerminal, follow);

            }

        }

        return tableBuilder.getTable();

    }

    public void eliminateEpsilonProductions() {

        EpsilonProductionElimination epe = new EpsilonProductionElimination(this);

        epe.eliminate();

    }

    public ArrayList<VerificationError> verify() {

        GrammarVerification lrd = new GrammarVerification(this);

        lrd.verify();

        return lrd.getExceptions();

    }

    public void eliminateLeftRecursion() {

        LeftRecursionElimination lre = new LeftRecursionElimination(this);

        lre.eliminate();

    }

    public String toString(boolean debug) {

        StringBuilder sb = new StringBuilder();

        Iterator<Map.Entry<Integer, ArrayList<InnerProduction>>> it = productions.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry<Integer, ArrayList<InnerProduction>> pair = it.next();

            String label = (debug ? "(" + pair.getKey() + ") " : "") + idToSymbol(pair.getKey());

            sb.append(label);
            sb.append(" -> ");

            Iterator<InnerProduction> productionIterator = pair.getValue().iterator();

            // assert productionIterator.hasNext();

            if (productionIterator.hasNext()) {
                while (true) {

                    InnerProduction ip = productionIterator.next();

                    sb.append(ip.toString(this));

                    if (!productionIterator.hasNext()) {
                        break;
                    }

                    sb.append('\n');
                    for (int i = 0; i < label.length(); i++) {
                        sb.append(' ');
                    }
                    sb.append("  | ");

                }
            }

            sb.append(';');

            if (it.hasNext()) {
                sb.append('\n');
            }

        }

        return sb.toString();

    }

    @Override
    public String toString() {
        return toString(false);
    }

}