package net.zerobone.knife.grammar.symbol;

import net.zerobone.knife.grammar.Symbol;

import java.util.ArrayList;
import java.util.Iterator;

public class SymbolGrammarProduction {

    public final String code;

    public final ArrayList<SymbolGrammarSymbol> body = new ArrayList<>();

    public SymbolGrammarProduction(String code) {
        this.code = code;
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