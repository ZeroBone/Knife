package net.zerobone.knife.grammar;

import java.util.ArrayList;
import java.util.Iterator;

class InnerProduction {

    String code;

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

            if (symbol.argumentName != null) {

                sb.append('(');
                sb.append(symbol.argumentName);
                sb.append(')');

            }

            if (it.hasNext()) {
                sb.append(' ');
            }

        }

        if (code != null) {

            sb.append("    ");
            sb.append("{ ");
            sb.append(code.replace('\n', ' '));
            sb.append(" }");

        }

        return sb.toString();

    }

}