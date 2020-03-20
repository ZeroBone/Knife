package net.zerobone.knife.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

class ParseTreeTerminalNodeGenerator {

    private ParseTreeTerminalNodeGenerator() {}

    static TypeSpec.Builder generate(GeneratorContext context) {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("ParseTreeTerminalNode")
            .addModifiers(Modifier.FINAL);

        // fields

        classBuilder.addField(
            FieldSpec
                .builder(context.getTokenTypeName(), "terminal")
                .initializer("null")
                .build()
        );

        return classBuilder;

    }

}