package net.zerobone.knife.generator;

import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

class IParseTreeNodeGenerator {

    private IParseTreeNodeGenerator() {}

    static TypeSpec.Builder generate() {

        return TypeSpec.interfaceBuilder("IParseTreeNode")
            .addModifiers(Modifier.FINAL);

    }

}