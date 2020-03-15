package net.zerobone.knife.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.TypeSpec;
import net.zerobone.knife.grammar.CFGParsingTable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ParserGenerator {

    private CFGParsingTable table;

    public ParserGenerator(CFGParsingTable table) {
        this.table = table;
    }

    public void generate() throws IOException {

        System.out.println("Generating...");

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("HelloWorld")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (int i = 0; i < 100; i++) {

            FieldSpec field = FieldSpec.builder(int.class, "T_" + i)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", i)
                .build();

            classBuilder.addField(field);

        }

        MethodSpec main = MethodSpec.methodBuilder("main")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(void.class)
            .addParameter(String[].class, "args")
            .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
            .build();

        TypeSpec helloWorld = classBuilder
            .addMethod(main)
            .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
            .build();

        BufferedWriter mainClassWriter = new BufferedWriter(new FileWriter("Parser.java"));

        javaFile.writeTo(mainClassWriter);

        mainClassWriter.close();

    }

}