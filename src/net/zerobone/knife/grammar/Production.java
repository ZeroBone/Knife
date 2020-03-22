package net.zerobone.knife.grammar;

import java.util.ArrayList;
import java.util.Iterator;

public class Production {

    private final String code;

    private ArrayList<Symbol> body = new ArrayList<>();

    public Production(String code) {
        this.code = code;
    }

    public void append(Symbol symbol) {
        body.add(symbol);
    }

    public ArrayList<Symbol> getBody() {
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

            Symbol symbol = (Symbol)it.next();

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