package net.zerobone.knife.generator;

import com.squareup.javapoet.ClassName;
import net.zerobone.knife.grammar.table.ParsingTable;

import java.util.HashMap;

public class GeneratorContext {

    final String packageName;

    final ParsingTable table;

    final HashMap<String, String> typeMap;

    private ClassName tokenTypeName = null;

    public GeneratorContext(String packageName, ParsingTable table, HashMap<String, String> typeMap) {
        this.packageName = packageName;
        this.table = table;
        this.typeMap = typeMap;
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