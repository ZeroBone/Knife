package net.zerobone.knife.generator;

import com.squareup.javapoet.JavaFile;
import net.zerobone.knife.grammar.table.CFGParsingTable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Generator {

    private Generator() {}

    public static void generate(GeneratorContext context) throws IOException {

        {

            JavaFile javaFile = JavaFile
                .builder(context.packageName, ParseTreeNodeGenerator.generate(context).build())
                .indent("\t")
                .build();

            BufferedWriter writer = new BufferedWriter(new FileWriter("ParseTreeNode.java"));
            javaFile.writeTo(writer);
            writer.close();
        }

        {

            JavaFile javaFile = JavaFile
                .builder(context.packageName, ParseTreeTerminalNodeGenerator.generate(context).build())
                .indent("\t")
                .build();

            BufferedWriter writer = new BufferedWriter(new FileWriter("ParseTreeTerminalNode.java"));
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