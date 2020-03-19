package net.zerobone.knife.grammar.table;

import net.zerobone.knife.grammar.CFGSymbol;

import java.util.ArrayList;
import java.util.Iterator;

public class CFGParsingTableProduction {

    public final String label;

    public final ArrayList<CFGSymbol> body;

    public final String code;

    public CFGParsingTableProduction(String label, ArrayList<CFGSymbol> body, String code) {
        this.label = label;
        this.body = body;
        this.code = code;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(label);
        sb.append(" -> ");

        Iterator it = body.iterator();

        while (it.hasNext()) {

            CFGSymbol symbol = (CFGSymbol)it.next();

            sb.append(symbol.id);

            if (symbol.argumentName != null) {

                sb.append('(');
                sb.append(symbol.argumentName);
                sb.append(')');

            }

            if (it.hasNext()) {
                sb.append(' ');
            }

        }

        sb.append(';');

        if (code != null) {

            sb.append("    ");
            sb.append("{ ");
            sb.append(code);
            sb.append(" }");

        }

        return sb.toString();
    }
}