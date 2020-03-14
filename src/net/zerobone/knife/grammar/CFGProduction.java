package net.zerobone.knife.grammar;

import java.util.Iterator;
import java.util.LinkedList;

public class CFGProduction {

    private LinkedList<CFGSymbol> body = new LinkedList<>();

    public void append(CFGSymbol symbol) {
        body.add(symbol);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        Iterator it = body.iterator();

        while (it.hasNext()) {

            CFGSymbol symbol = (CFGSymbol)it.next();

            sb.append(symbol.sym);

            it.remove(); // to avoid a ConcurrentModificationException

            if (it.hasNext()) {
                sb.append(' ');
            }

        }

        return sb.toString();

    }

}