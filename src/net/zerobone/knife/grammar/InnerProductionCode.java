package net.zerobone.knife.grammar;

public class InnerProductionCode {

    private String initialCode;

    public InnerProductionCode(String initialCode) {
        this.initialCode = initialCode;
    }

    @Override
    public String toString() {
        return initialCode;
    }

}