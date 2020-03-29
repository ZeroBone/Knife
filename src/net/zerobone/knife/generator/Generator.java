package net.zerobone.knife.generator;

import com.squareup.javapoet.JavaFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Generator {

    private Generator() {}

    public static void generate(GeneratorContext context) throws IOException {

        {

            JavaFile javaFile = JavaFile
                .builder(context.packageName, ParseErrorGenerator.generate(context).build())
                .indent("\t")
                .build();

            BufferedWriter writer = new BufferedWriter(new FileWriter("ParseError.java"));
            javaFile.writeTo(writer);
            writer.close();
        }

        {

            JavaFile javaFile = JavaFile
                .builder(context.packageName, ParseNodeGenerator.generate(context).build())
                .indent("\t")
                .build();

            BufferedWriter writer = new BufferedWriter(new FileWriter("ParseNode.java"));
            javaFile.writeTo(writer);
            writer.close();
        }

        {
            JavaFile javaFile = JavaFile
                .builder(context.packageName, ParserGenerator.generate(context).build())
                .indent("\t")
                .build();

            BufferedWriter writer = new BufferedWriter(new FileWriter("Parser.java"));
            javaFile.writeTo(writer);
            writer.close();
        }

    }

}