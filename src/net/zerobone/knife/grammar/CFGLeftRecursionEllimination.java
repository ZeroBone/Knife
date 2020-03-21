package net.zerobone.knife.grammar;

import java.util.ArrayList;

class CFGLeftRecursionEllimination {

    private final CFGEntry[] entries;

    public CFGLeftRecursionEllimination(CFGEntry[] entries) {
        this.entries = entries;
        /*CFGEntry tmp = entries[0];
        entries[0] = entries[1];
        entries[1] = tmp;*/
    }

    public void elliminate() {

        for (int i = 0; i < entries.length; i++) {

            for (int j = 0; j < i; j++) {

                CFGProductions iProductions = entries[i].productions;
                CFGProductions jProductions = entries[j].productions;

                String jLabel = entries[j].label;
                String iLabel = entries[i].label;

                ArrayList<CFGProduction> cfgProductionsArrayList = iProductions.getProductions();
                int uncheckedProductionsCount = cfgProductionsArrayList.size();

                for (int k = 0; k < uncheckedProductionsCount; k++) {

                    CFGProduction production = cfgProductionsArrayList.get(k);

                    ArrayList<CFGSymbol> body = production.getBody();

                    if (body.size() == 0) {
                        System.out.println("EPSILON PRODUCTION!!! THIS CAN MAKE THE ALGO WORK INCORRECTLY!");
                        continue;
                    }

                    CFGSymbol startSymbol = body.get(0);

                    if (startSymbol.isTerminal) {
                        continue;
                    }

                    if (!startSymbol.id.equals(jLabel)) {
                        continue;
                    }

                    // we have a production of the form Ai => Aj
                    // now we replace in the body of the Ai production

                    System.out.println("Production of the left-recursive form: " + iLabel + " -> " + production);

                    cfgProductionsArrayList.remove(k);

                    k--;

                    uncheckedProductionsCount--;

                    for (CFGProduction jProduction : jProductions.getProductions()) {

                        CFGProduction substitutedProduction = new CFGProduction(null);

                        substitutedProduction.getBody().addAll(jProduction.getBody());

                        // add rest body (gamma)
                        for (int r = 1; r < body.size(); r++) {
                            substitutedProduction.append(body.get(r));
                        }

                        cfgProductionsArrayList.add(substitutedProduction);

                    }

                }

            }

            System.out.println("Removing immediate left recursion among");

        }

    }

}