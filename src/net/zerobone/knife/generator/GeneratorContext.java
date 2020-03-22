package net.zerobone.knife.generator;

import com.squareup.javapoet.ClassName;
import net.zerobone.knife.grammar.table.CFGParsingTable;

public class GeneratorContext {

    final String packageName;

    final CFGParsingTable table;

    private ClassName tokenTypeName = null;

    public GeneratorContext(String packageName, CFGParsingTable table) {
        this.packageName = packageName;
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