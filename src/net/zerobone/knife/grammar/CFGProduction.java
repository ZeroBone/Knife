package net.zerobone.knife.grammar;

import java.util.ArrayList;
import java.util.Iterator;

public class CFGProduction {

    private ArrayList<CFGSymbol> body = new ArrayList<>();

    public void append(CFGSymbol symbol) {
        body.add(symbol);
    }

    public ArrayList<CFGSymbol> getBody() {
        return body;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        Iterator it = body.iterator();

        while (it.hasNext()) {

            CFGSymbol symbol = (CFGSymbol)it.next();

            sb.append(symbol.id);

            // it.remove(); // to avoid a ConcurrentModificationException

            if (it.hasNext()) {
                sb.append(' ');
            }

        }

        return sb.toString();

    }

}