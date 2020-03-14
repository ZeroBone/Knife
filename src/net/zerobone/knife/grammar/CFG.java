package net.zerobone.knife.grammar;

import java.util.HashMap;
import java.util.Iterator;

public class CFG {

    private String startSymbol;

    private final HashMap<String, CFGProductions> productions;

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

            it.remove(); // to avoid a ConcurrentModificationException

            if (it.hasNext()) {
                sb.append('\n');
            }

        }

        return sb.toString();

    }

}