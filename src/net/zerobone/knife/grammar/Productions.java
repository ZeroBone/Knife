package net.zerobone.knife.grammar;

import java.util.ArrayList;
import java.util.Iterator;

public class Productions {

    private final ArrayList<Production> productions;

    public Productions(Production initialProduction) {

        productions = new ArrayList<>();

        productions.add(initialProduction);

    }

    public void addProduction(Production production) {
        productions.add(production);
    }

    public ArrayList<Production> getProductions() {
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

            Production production = (Production)it.next();

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