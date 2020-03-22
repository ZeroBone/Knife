package net.zerobone.knife.grammar;

import java.util.ArrayList;
import java.util.Iterator;

public class Production {

    final String code;

    final ArrayList<Symbol> body;

    public Production(String code, ArrayList<Symbol> body) {
        this.code = code;
        this.body = body;
    }

    public Production(String code) {
        this.code = code;
        this.body = new ArrayList<>();
    }

    public void append(Symbol symbol) {
        body.add(symbol);
    }

    public String getCode() {
        return code;
    }

    public ArrayList<Symbol> getBody() {
        return body;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        Iterator it = body.iterator();

        while (it.hasNext()) {

            Symbol symbol = (Symbol)it.next();

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