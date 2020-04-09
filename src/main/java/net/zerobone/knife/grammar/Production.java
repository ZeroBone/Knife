package net.zerobone.knife.grammar;

import java.util.ArrayList;

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

}