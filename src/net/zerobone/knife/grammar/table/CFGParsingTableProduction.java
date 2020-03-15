package net.zerobone.knife.grammar.table;

import net.zerobone.knife.grammar.CFGSymbol;

import java.util.ArrayList;
import java.util.Iterator;

public class CFGParsingTableProduction {

    public final String label;

    public final ArrayList<CFGSymbol> body;

    public CFGParsingTableProduction(String label, ArrayList<CFGSymbol> body) {
        this.label = label;
        this.body = body;
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

            if (it.hasNext()) {
                sb.append(' ');
            }

        }

        sb.append(';');

        return sb.toString();
    }
}