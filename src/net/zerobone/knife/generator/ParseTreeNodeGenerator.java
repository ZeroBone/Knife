package net.zerobone.knife.generator;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import net.zerobone.knife.grammar.table.CFGParsingTable;

import javax.lang.model.element.Modifier;

class ParseTreeNodeGenerator {

    private ParseTreeNodeGenerator() {}

    static TypeSpec.Builder generate(CFGParsingTable table, TypeName iParseTreeNodeInterface) {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("ParseTreeNode")
            .addModifiers(Modifier.FINAL)
            .addSuperinterface(iParseTreeNodeInterface);

        // methods

        // classBuilder.addMethod(constructParseMethod(table));

        return classBuilder;

    }

}