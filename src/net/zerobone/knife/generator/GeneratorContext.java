package net.zerobone.knife.generator;

import com.squareup.javapoet.ClassName;
import net.zerobone.knife.grammar.table.ParsingTable;

public class GeneratorContext {

    final String packageName;

    final ParsingTable table;

    private ClassName tokenTypeName = null;

    public GeneratorContext(String packageName, ParsingTable table) {
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