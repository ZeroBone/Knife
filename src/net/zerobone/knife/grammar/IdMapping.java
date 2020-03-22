package net.zerobone.knife.grammar;

import java.util.HashMap;

public class IdMapping {

    public final int terminalCount;

    public final int nonTerminalCount;

    public final String startNonTerminalId;

    private final HashMap<String, Integer> symbolToIdMap;

    private final HashMap<Integer, String> idToSymbolMap;

    public IdMapping(int terminalCount, int nonTerminalCount, String startNonTerminalId, HashMap<String, Integer> symbolToIdMap, HashMap<Integer, String> idToSymbolMap) {
        this.terminalCount = terminalCount;
        this.nonTerminalCount = nonTerminalCount;
        this.startNonTerminalId = startNonTerminalId;
        this.symbolToIdMap = symbolToIdMap;
        this.idToSymbolMap = idToSymbolMap;
    }

    public int map(String symbol) {
        return symbolToIdMap.get(symbol);
    }

    public String idToSymbol(int id) {
        return idToSymbolMap.get(id);
    }

    public HashMap<String, Integer> getSymbolToIdMap() {
        return symbolToIdMap;
    }

    public int terminalToIndex(String symbol) {

        if (symbol.equals("$")) {
            return 0;
        }

        // terminals are represented as positive integers
        int value = symbolToIdMap.get(symbol);

        if (value < 0) {
            throw new RuntimeException("Symbol " + symbol + " is not a terminal.");
        }

        return value;

    }

    public int nonTerminalToIndex(String symbol) {

        // non-terminals are represented as negative integers
        int value = symbolToIdMap.get(symbol);

        if (value >= 0) {
            throw new RuntimeException("Symbol " + symbol + " is not a non-terminal.");
        }

        return -value - 1;

    }

}