package net.zerobone.knife.grammar;

import java.util.ArrayList;
import java.util.Iterator;

class InnerProduction {

    final String code;

    ArrayList<InnerSymbol> body = new ArrayList<>();

    public InnerProduction(String code) {
        this.code = code;
    }

    public String toString(Grammar grammar) {

        StringBuilder sb = new StringBuilder();

        Iterator<InnerSymbol> it = body.iterator();

        while (it.hasNext()) {

            InnerSymbol symbol = it.next();

            sb.append(grammar.idToSymbol(symbol.id));

            if (it.hasNext()) {
                sb.append(' ');
            }

        }

        if (code != null) {

            sb.append("    ");
            sb.append("{ ");
            sb.append(code);
            sb.append(" }");

        }

        return sb.toString();

    }

}