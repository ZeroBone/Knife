package net.zerobone.knife.grammar;

import java.util.ArrayList;
import java.util.Iterator;

public class CFGProduction {

    private final String code;

    private ArrayList<CFGSymbol> body = new ArrayList<>();

    public CFGProduction(String code) {
        this.code = code;
    }

    public void append(CFGSymbol symbol) {
        body.add(symbol);
    }

    public ArrayList<CFGSymbol> getBody() {
        return body;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        Iterator it = body.iterator();

        while (it.hasNext()) {

            CFGSymbol symbol = (CFGSymbol)it.next();

            sb.append(symbol.id);

            if (it.hasNext()) {
                sb.append(' ');
            }

        }

        if (code != null) {

            sb.append("    ");
            sb.append("<code:");
            sb.append(code.length());
            sb.append(">");

        }

        return sb.toString();

    }

}