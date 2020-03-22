package net.zerobone.knife.grammar;

import java.util.ArrayList;
import java.util.Iterator;

public class CFGProductions {

    private final ArrayList<CFGProduction> productions;

    public CFGProductions(CFGProduction initialProduction) {

        productions = new ArrayList<>();

        productions.add(initialProduction);

    }

    public void addProduction(CFGProduction production) {
        productions.add(production);
    }

    public ArrayList<CFGProduction> getProductions() {
        return productions;
    }

    public void clear() {
        productions.clear();
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        Iterator it = productions.iterator();

        while (it.hasNext()) {

            CFGProduction production = (CFGProduction)it.next();

            sb.append(production);

            // it.remove(); // to avoid a ConcurrentModificationException

            if (it.hasNext()) {
                sb.append('\n');
                sb.append("\t| ");
            }

        }

        return sb.toString();

    }

}