package net.zerobone.knife.grammar;

import java.util.ArrayList;
import java.util.ListIterator;

class CFGLeftRecursionElimination {

    private final CFG cfg;

    private final CFGEntry[] entries;

    public CFGLeftRecursionElimination(CFG cfg, CFGEntry[] entries) {
        this.cfg = cfg;
        this.entries = entries;
        CFGEntry tmp = entries[1];
        entries[1] = entries[0];
        entries[0] = tmp;
    }

    private void eliminateImmediateLeftRecursion(String label, CFGProductions productions) {

        final String newSymbol = label + "'";

        ArrayList<CFGProduction> alphas = new ArrayList<>();

        ListIterator<CFGProduction> productionListIterator = productions.getProductions().listIterator();

        while (productionListIterator.hasNext()) {

            CFGProduction production = productionListIterator.next();

            ArrayList<CFGSymbol> body = production.getBody();

            if (body.size() == 0) {
                // TODO: throw new IllegalStateException("Epsilon production found while eliminating left recursion.");
                continue;
            }

            CFGSymbol firstSymbol = body.get(0);

            // if the first symbol is a terminal or non-label non-terminal
            if (firstSymbol.isTerminal || !firstSymbol.id.equals(label)) {

                // this is a beta_i symbol

                production.append(new CFGSymbol(newSymbol, false, null));

                continue;

            }

            alphas.add(production);

            productionListIterator.remove();

        }

        // create new productions

        for (CFGProduction alphaProduction : alphas) {

            ArrayList<CFGSymbol> alphaProductionBody = alphaProduction.getBody();

            CFGProduction newProduction = new CFGProduction(null);

            for (int i = 1; i < alphaProductionBody.size(); i++) {

                newProduction.append(alphaProductionBody.get(i));

            }

            newProduction.append(new CFGSymbol(newSymbol, false, null));

            cfg.addProduction(newSymbol, newProduction);

        }

        // add epsilon production
        cfg.addProduction(newSymbol, new CFGProduction(null));

        /*ArrayList<CFGProduction> alphas = new ArrayList<>();
        ArrayList<CFGProduction> betas = new ArrayList<>();

        for (CFGProduction production : productions.getProductions()) {

            ArrayList<CFGSymbol> body = production.getBody();



            CFGSymbol firstSymbol = body.get(0);

            if (firstSymbol.isTerminal) {

                // beta symbol

                betas.add(production);

            }
            else {

                // alpha symbol

                alphas.add(production);

            }

        }

        String newSymbol = label + "'";

        // replace existing production

        productions.clear();

        for (CFGProduction betaProduction : betas) {

            betaProduction.append(new CFGSymbol(newSymbol, false, null));

            productions.addProduction(betaProduction);

        }

        // create new productions

        for (CFGProduction alphaProduction : alphas) {

            ArrayList<CFGSymbol> alphaProductionBody = alphaProduction.getBody();

            CFGProduction newProduction = new CFGProduction(null);

            for (int i = 1; i < alphaProductionBody.size(); i++) {

                newProduction.append(alphaProductionBody.get(i));

            }

            newProduction.append(new CFGSymbol(newSymbol, false, null));

            cfg.addProduction(newSymbol, newProduction);

        }

        // add epsilon production
        cfg.addProduction(newSymbol, new CFGProduction(null));*/

    }

    public void eliminate() {

        // Paull's algorithm

        for (int i = 0; i < entries.length; i++) {

            CFGProductions iProductions = entries[i].productions;
            String iLabel = entries[i].label;

            for (int j = 0; j < i; j++) {

                CFGProductions jProductions = entries[j].productions;

                String jLabel = entries[j].label;

                ListIterator<CFGProduction> iProductionsListIter = iProductions.getProductions().listIterator();

                while (iProductionsListIter.hasNext()) {

                    ArrayList<CFGSymbol> ajAlpha = iProductionsListIter.next().getBody();

                    if (ajAlpha.size() == 0) {
                        continue;
                    }

                    CFGSymbol firstSymbol = ajAlpha.get(0);

                    if (firstSymbol.isTerminal) {
                        continue;
                    }

                    if (!firstSymbol.id.equals(jLabel)) {
                        continue;
                    }

                    iProductionsListIter.remove();

                    // for every production of the form aj -> beta
                    for (CFGProduction prod : jProductions.getProductions()) {

                        CFGProduction newIProduction = new CFGProduction(null);

                        // append beta to new Ai production
                        for (CFGSymbol sym : prod.getBody()) {
                            newIProduction.append(sym);
                        }

                        // append alpha
                        for (int k = 1; k < ajAlpha.size(); k++) {
                            newIProduction.append(ajAlpha.get(k));
                        }

                        iProductionsListIter.add(newIProduction);

                    }

                }

            }

            eliminateImmediateLeftRecursion(iLabel, iProductions);

            // System.out.println("Removing immediate left recursion among " + iProductions);

        }

    }

}