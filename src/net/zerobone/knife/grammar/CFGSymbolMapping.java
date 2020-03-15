package net.zerobone.knife.grammar;

import java.util.HashMap;

public class CFGSymbolMapping {

    public final int terminalCount;

    public final int nonTerminalCount;

    private final HashMap<String, Integer> mapping;

    public CFGSymbolMapping(int terminalCount, int nonTerminalCount, HashMap<String, Integer> mapping) {
        this.terminalCount = terminalCount;
        this.nonTerminalCount = nonTerminalCount;
        this.mapping = mapping;
    }

    public int map(String symbol) {
        return mapping.get(symbol);
    }

    public HashMap<Integer, String> getDebugReverseMapping() {

        HashMap<Integer, String> reverseMapping = new HashMap<>();

        for (HashMap.Entry<String, Integer> entry : mapping.entrySet()) {

            String key = entry.getKey();
            int value = entry.getValue();

            reverseMapping.put(value, key);

        }

        return reverseMapping;

    }

    public int terminalToIndex(String symbol) {

        if (symbol.equals("$")) {
            return 0;
        }

        // terminals are represented as positive integers
        int value = mapping.get(symbol);

        if (value < 0) {
            throw new RuntimeException("Symbol " + symbol + " is not a terminal.");
        }

        return value;

    }

    public int nonTerminalToIndex(String symbol) {

        // non-terminals are represented as negative integers
        int value = mapping.get(symbol);

        if (value >= 0) {
            throw new RuntimeException("Symbol " + symbol + " is not a non-terminal.");
        }

        return -value - 1;

    }

    @Override
    public String toString() {
        return "CFGMapping{" +
            "terminalCount=" + terminalCount +
            ", nonTerminalCount=" + nonTerminalCount +
            ", mapping=" + mapping +
            '}';
    }

}