package net.zerobone.knife.generator;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;

class ParseErrorGenerator {

    private ParseErrorGenerator() {}

    private static MethodSpec constructConstructor() {

        MethodSpec.Builder b = MethodSpec.constructorBuilder();

        b.addParameter(int.class, "expected");
        b.addParameter(int.class, "got");
        b.addParameter(Object.class, "token");

        b.addStatement("this.expected = expected");
        b.addStatement("this.got = got");
        b.addStatement("this.token = token");

        return b.build();

    }

    static TypeSpec.Builder generate(GeneratorContext context) {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("ParseError")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        // fields

        classBuilder.addField(
            FieldSpec
                .builder(int.class, "ANY")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("-1")
                .build()
        );

        classBuilder.addField(
            FieldSpec.builder(int.class, "expected")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .build()
        );

        classBuilder.addField(
            FieldSpec.builder(int.class, "got")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .build()
        );

        classBuilder.addField(
            FieldSpec.builder(Object.class, "token")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .build()
        );

        // methods

        classBuilder.addMethod(constructConstructor());

        return classBuilder;

    }

}