package net.zerobone.knife;

import net.zerobone.knife.ast.TranslationUnitNode;
import net.zerobone.knife.ast.entities.ProductionSymbol;
import net.zerobone.knife.ast.statements.ProductionStatementNode;
import net.zerobone.knife.ast.statements.StatementNode;
import net.zerobone.knife.grammar.CFG;
import net.zerobone.knife.grammar.CFGParsingTable;
import net.zerobone.knife.grammar.CFGProduction;
import net.zerobone.knife.grammar.CFGSymbol;
import net.zerobone.knife.parser.KnifeParser;
import net.zerobone.knife.parser.ParseException;
import net.zerobone.knife.parser.TokenMgrError;

import java.io.*;

public class Knife {

    public static void main(String[] args) {

        if (args.length == 1) {

            InputStream is;

            try {
                is = new FileInputStream(args[0]);
            }
            catch (FileNotFoundException e) {
                System.out.println("File: " + args[0] + " was not found!");
                return;
            }

            KnifeParser lang = new KnifeParser(is);
            TranslationUnitNode t;

            try {
                t = lang.translationUnit();
            }
            catch (ParseException | TokenMgrError e) {
                System.out.println(e.toString());
                return;
            }

            System.out.println("Creating context-free grammar...");

            generateParser(t);

            System.out.println("Success.");

            return;

        }

        System.out.println("Invalid arguments!");
        System.out.println("Usage: knife filename.kn");

    }

    private static CFGProduction convertProduction(ProductionStatementNode statement) {

        CFGProduction production = new CFGProduction();

        for (ProductionSymbol symbol : statement.production) {
            production.append(new CFGSymbol(symbol.id, symbol.terminal));
        }

        return production;

    }

    private static void generateParser(TranslationUnitNode t) {

        CFG cfg = null;

        for (StatementNode stmt : t.statements) {

            if (stmt instanceof ProductionStatementNode) {

                ProductionStatementNode production = (ProductionStatementNode)stmt;

                if (cfg == null) {
                    cfg = new CFG(production.nonTerminal, convertProduction(production));
                }
                else {
                    cfg.addProduction(production.nonTerminal, convertProduction(production));
                }

            }

        }

        if (cfg == null) {
            throw new RuntimeException("Could not find start symbol.");
        }

        CFGParsingTable table = cfg.constructParsingTable();

        try {

            BufferedWriter debugLogWriter = new BufferedWriter(new FileWriter("debug.log"));

            debugLogWriter.write("Grammar:");
            debugLogWriter.newLine();
            debugLogWriter.newLine();

            debugLogWriter.write(cfg.toString());

            debugLogWriter.newLine();
            debugLogWriter.newLine();

            debugLogWriter.write("First sets: " + cfg.computeFirstSets());
            debugLogWriter.newLine();
            debugLogWriter.write("Follow sets: " + cfg.computeFollowSets());
            debugLogWriter.newLine();
            debugLogWriter.newLine();

            debugLogWriter.write(table.toString());

            debugLogWriter.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

}