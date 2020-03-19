package net.zerobone.knife.generator;

import com.squareup.javapoet.JavaFile;
import net.zerobone.knife.grammar.table.CFGParsingTable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Generator {

    private Generator() {}

    public static void generate(CFGParsingTable table, String packageName) throws IOException {

        {

            JavaFile javaFile = JavaFile
                .builder(packageName, ParseTreeNodeGenerator.generate(table).build())
                .indent("\t")
                .build();

            BufferedWriter writer = new BufferedWriter(new FileWriter("ParseTreeNode.java"));
            javaFile.writeTo(writer);
            writer.close();
        }

        {
            JavaFile javaFile = JavaFile
                .builder(packageName, ParserGenerator.generate(table).build())
                .indent("\t")
                .build();

            BufferedWriter writer = new BufferedWriter(new FileWriter("Parser.java"));
            javaFile.writeTo(writer);
            writer.close();
        }

    }

}