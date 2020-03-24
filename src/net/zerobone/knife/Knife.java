package net.zerobone.knife;

import net.zerobone.knife.ast.TranslationUnitNode;
import net.zerobone.knife.ast.entities.ProductionSymbol;
import net.zerobone.knife.ast.statements.ProductionStatementNode;
import net.zerobone.knife.ast.statements.StatementNode;
import net.zerobone.knife.generator.Generator;
import net.zerobone.knife.generator.GeneratorContext;
import net.zerobone.knife.grammar.Grammar;
import net.zerobone.knife.grammar.table.ParsingTable;
import net.zerobone.knife.grammar.Production;
import net.zerobone.knife.grammar.Symbol;
import net.zerobone.knife.parser.KnifeParser;
import net.zerobone.knife.parser.ParseException;
import net.zerobone.knife.parser.TokenMgrError;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Knife {

    public static void main(String[] args) {

        if (args.length == 1) {

            InputStream is;

            try {
                is = new FileInputStream(args[0]);
            }
            catch (FileNotFoundException e) {
                System.out.println("Error: File '" + args[0] + "' was not found!");
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

            System.out.println("Parser generated successfully.");

            return;

        }

        System.out.println("Invalid arguments!");
        System.out.println("Usage: knife filename.kn");

    }

    private static Production convertProduction(ProductionStatementNode statement) {

        Production production = new Production(statement.code);

        for (ProductionSymbol symbol : statement.production) {
            production.append(new Symbol(symbol.id, symbol.terminal, symbol.argument));
        }

        return production;

    }

    private static void generateParser(TranslationUnitNode t) {

        Grammar grammar = null;

        for (StatementNode stmt : t.statements) {

            if (stmt instanceof ProductionStatementNode) {

                ProductionStatementNode production = (ProductionStatementNode)stmt;

                if (grammar == null) {
                    grammar = new Grammar(production.nonTerminal, convertProduction(production));
                }
                else {
                    grammar.addProduction(production.nonTerminal, convertProduction(production));
                }

            }

        }

        if (grammar == null) {
            throw new RuntimeException("Could not find start symbol.");
        }

        System.out.println("Before eliminating left recursion:");
        System.out.println(grammar);

        grammar.eliminateLeftRecursion();

        System.out.println("Eliminated left recursion:");
        System.out.println(grammar);

        System.out.println("Building parse tables...");

        ParsingTable table = grammar.constructParsingTable();

        try {

            BufferedWriter debugLogWriter = new BufferedWriter(new FileWriter("debug.log"));

            debugLogWriter.write("Grammar:");
            debugLogWriter.newLine();
            debugLogWriter.newLine();

            debugLogWriter.write(grammar.toString());

            debugLogWriter.newLine();
            debugLogWriter.newLine();

            debugLogWriter.write("First sets:");
            debugLogWriter.newLine();

            HashMap<Integer, HashSet<Integer>> firstSets = grammar.computeFirstSets();

            for (HashMap.Entry<Integer, HashSet<Integer>> entry : firstSets.entrySet()) {

                debugLogWriter.write("FIRST(");
                debugLogWriter.write(grammar.idToSymbol(entry.getKey()));
                debugLogWriter.write(") = {");

                Iterator<Integer> firstSetIterator = entry.getValue().iterator();

                if (firstSetIterator.hasNext()) {

                    while (true) {

                        int id = firstSetIterator.next();

                        if (id != Grammar.FIRST_FOLLOW_SET_EPSILON) {

                            debugLogWriter.write(grammar.idToSymbol(id));

                        }

                        if (!firstSetIterator.hasNext()) {
                            break;
                        }

                        debugLogWriter.write(", ");

                    }

                }

                debugLogWriter.write('}');

                debugLogWriter.newLine();

            }

            debugLogWriter.newLine();

            debugLogWriter.write("Follow sets:");
            debugLogWriter.newLine();

            HashMap<Integer, HashSet<Integer>> followSets = grammar.computeFollowSets();

            for (HashMap.Entry<Integer, HashSet<Integer>> entry : followSets.entrySet()) {

                debugLogWriter.write("FOLLOW(");
                debugLogWriter.write(grammar.idToSymbol(entry.getKey()));
                debugLogWriter.write(") = {");

                Iterator<Integer> followSetIterator = entry.getValue().iterator();

                if (followSetIterator.hasNext()) {

                    while (true) {

                        int id = followSetIterator.next();

                        if (id == Grammar.FOLLOW_SET_EOF) {
                            debugLogWriter.write("$");
                        }
                        else if (id != Grammar.FIRST_FOLLOW_SET_EPSILON) {

                            debugLogWriter.write(grammar.idToSymbol(id));

                        }

                        if (!followSetIterator.hasNext()) {
                            break;
                        }

                        debugLogWriter.write(", ");

                    }

                }

                debugLogWriter.write('}');

                debugLogWriter.newLine();

            }

            debugLogWriter.newLine();

            debugLogWriter.write(table.toString());

            debugLogWriter.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        GeneratorContext context = new GeneratorContext("net.zerobone.knife.parser", table);

        try {

            Generator.generate(context);

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

}