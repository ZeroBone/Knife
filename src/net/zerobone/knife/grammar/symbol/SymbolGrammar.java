package net.zerobone.knife.grammar.symbol;

import net.zerobone.knife.grammar.Grammar;
import net.zerobone.knife.grammar.Production;
import net.zerobone.knife.grammar.Symbol;

import java.util.HashMap;
import java.util.HashSet;

public class SymbolGrammar {

    private Grammar grammar;

    public final String startSymbol;

    private final HashMap<String, Integer> symbolToIdMap = new HashMap<>();

    private final HashMap<Integer, String> idToSymbolMap = new HashMap<>();

    private int nonTerminalCounter = -1;

    private int terminalCounter = 1;

    public SymbolGrammar(String startSymbol, SymbolGrammarProduction startProduction) {

        grammar = new Grammar(nonTerminalCounter, convertProduction(startProduction));

        this.startSymbol = startSymbol;

        registerSymbol(startSymbol, nonTerminalCounter);

        nonTerminalCounter--;

    }

    private void registerSymbol(String symbol, int id) {
        symbolToIdMap.put(symbol, id);
        idToSymbolMap.put(id, symbol);
    }

    public void addProduction(String nonTerminal, SymbolGrammarProduction production) {

        Integer id = symbolToIdMap.get(nonTerminal);

        if (id == null) {

            registerSymbol(nonTerminal, nonTerminalCounter);

            grammar.addProduction(nonTerminalCounter, convertProduction(production));

            nonTerminalCounter--;

            return;
        }

        assert id < 0;

        grammar.addProduction(id, convertProduction(production));

    }

    private Production convertProduction(SymbolGrammarProduction production) {

        Production convertedProduction = new Production(production.code);

        for (SymbolGrammarSymbol symbol : production.body) {

            assert symbol.id != null;

            Integer symbolId = symbolToIdMap.get(symbol.id);

            if (symbolId == null) {

                // new symbol
                // will probably be defined later

                if (symbol.isTerminal) {
                    registerSymbol(symbol.id, terminalCounter);
                    symbolId = terminalCounter;
                    terminalCounter++;
                }
                else {
                    // non-terminal
                    registerSymbol(symbol.id, nonTerminalCounter);
                    symbolId = nonTerminalCounter;
                    nonTerminalCounter--;
                }

            }

            convertedProduction.append(new Symbol(symbolId, symbol.argumentName));

        }

        return convertedProduction;

    }

    public HashMap<String, HashSet<String>> debugConvertFirstFollowSet(HashMap<Integer, HashSet<Integer>> set) {

        HashMap<String, HashSet<String>> debugFirstSets = new HashMap<>();

        for (HashMap.Entry<Integer, HashSet<Integer>> entry : set.entrySet()) {

            HashSet<String> values = new HashSet<>();

            for (int v : entry.getValue()) {
                values.add(idToSymbol(v));
            }

            debugFirstSets.put(idToSymbol(entry.getKey()), values);

        }

        return debugFirstSets;

    }

    public String idToSymbol(int id) {
        return idToSymbolMap.get(id);
    }

    public int symbolToId(String symbol) {
        return symbolToIdMap.get(symbol);
    }

    public HashMap<String, Integer> getSymbolToIdMap() {
        return symbolToIdMap;
    }

    public Grammar getGrammar() {
        return grammar;
    }

}