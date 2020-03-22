package net.zerobone.knife.generator;

import com.squareup.javapoet.ClassName;
import net.zerobone.knife.grammar.symbol.SymbolGrammar;
import net.zerobone.knife.grammar.table.ParsingTable;

public class GeneratorContext {

    final String packageName;

    final SymbolGrammar grammar;

    final ParsingTable table;

    private ClassName tokenTypeName = null;

    public GeneratorContext(String packageName, SymbolGrammar grammar, ParsingTable table) {
        this.packageName = packageName;
        this.grammar = grammar;
        this.table = table;
    }

    public void setTokenTypeName(String basePackage, String className) {
        tokenTypeName = ClassName.get(basePackage, className);
    }

    public ClassName getTokenTypeName() {

        if (tokenTypeName == null) {
            return ClassName.get("java.lang", "Object");
        }

        return tokenTypeName;

    }
}