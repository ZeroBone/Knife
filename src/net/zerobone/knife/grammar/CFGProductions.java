package net.zerobone.knife.grammar;

import java.util.Iterator;
import java.util.LinkedList;

public class CFGProductions {

    private final LinkedList<CFGProduction> productions;

    public CFGProductions(CFGProduction initialProduction) {

        productions = new LinkedList<>();

        productions.add(initialProduction);

    }

    public void addProduction(CFGProduction production) {
        productions.add(production);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        Iterator it = productions.iterator();

        while (it.hasNext()) {

            CFGProduction production = (CFGProduction)it.next();

            sb.append(production);

            it.remove(); // to avoid a ConcurrentModificationException

            if (it.hasNext()) {
                sb.append('\n');
                sb.append("\t| ");
            }

        }

        return sb.toString();

    }

}