package net.zerobone.knife.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import net.zerobone.knife.grammar.table.CFGParsingTable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Generator {

    private Generator() {}

    public static void generate(CFGParsingTable table, String packageName) throws IOException {

        TypeSpec iParseTreeNodeInterface;

        {

            iParseTreeNodeInterface = IParseTreeNodeGenerator.generate().build();

            JavaFile javaFile = JavaFile
                .builder(packageName, iParseTreeNodeInterface)
                .indent("\t")
                .build();

            BufferedWriter writer = new BufferedWriter(new FileWriter("IParseTreeNode.java"));
            javaFile.writeTo(writer);
            writer.close();
        }

        {

            TypeName interfaceTypeName = ClassName.get(packageName, iParseTreeNodeInterface.name);

            JavaFile javaFile = JavaFile
                .builder(packageName, ParseTreeNodeGenerator.generate(table, interfaceTypeName).build())
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