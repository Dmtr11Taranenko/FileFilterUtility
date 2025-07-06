package org.example;

import org.apache.commons.cli.*;
import org.example.filter.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Dmitrii Taranenko
 */
public class Main {
    public static void main(String[] args) {
        Options opts = new Options();
        opts.addOption("o", true, "output directory");
        opts.addOption("p", true, "file prefix");
        opts.addOption("a", false, "append mode");
        opts.addOption("s", false, "short stats");
        opts.addOption("f", false, "full stats");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(opts, args);
            String[] files = cmd.getArgs();
            if (files.length == 0) throw new ParseException("No input files specified");

            String outDir = cmd.getOptionValue("o", ".");
            String prefix = cmd.getOptionValue("p", "");
            boolean append = cmd.hasOption("a");
            boolean shortStats = cmd.hasOption("s");
            boolean fullStats = cmd.hasOption("f");
            if (shortStats && fullStats) throw new ParseException("Options -s and -f are mutually exclusive");
            boolean needFull = fullStats;

            FileWriterManager fw = new FileWriterManager(outDir, prefix, append);
            Statistics intStats = new Statistics();
            Statistics floatStats = new Statistics();
            Statistics strStats = new Statistics();

            for (String fname : files) {
                try (BufferedReader br = new BufferedReader(new FileReader(fname))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String trimmed = line.trim();
                        if (trimmed.isEmpty()) continue;
                        try {
                            if (trimmed.matches("[+-]?\\d+")) {
                                BigInteger v = new BigInteger(trimmed);
                                fw.write(DataType.INTEGER, trimmed);
                                intStats.addInteger(v);
                            } else {
                                BigDecimal bd = new BigDecimal(trimmed);
                                fw.write(DataType.FLOAT, trimmed);
                                floatStats.addFloat(bd);
                            }
                        } catch (NumberFormatException e) {
                            fw.write(DataType.STRING, line);
                            strStats.addString(line);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error reading file " + fname + ": " + e.getMessage());
                }
            }
            fw.closeAll();

            printStats("Integers", intStats, needFull);
            printStats("Floats", floatStats, needFull);
            printStats("Strings", strStats, needFull);

        } catch (ParseException e) {
            System.err.println("Argument error: " + e.getMessage());
        }
    }

    private static void printStats(String name, Statistics stats, boolean full) {
        long count = stats.getCount();
        if (count == 0) return;
        System.out.println(name + ":");
        System.out.println("  Count = " + count);
        if (full) {
            if (!name.equals("Strings")) {
                System.out.println("  Min = " + stats.getMin().get());
                System.out.println("  Max = " + stats.getMax().get());
                System.out.println("  Sum = " + stats.getSum());
                System.out.println("  Avg = " + stats.getAverage());
            } else {
                System.out.println("  MinLength = " + stats.getMinLength().get());
                System.out.println("  MaxLength = " + stats.getMaxLength().get());
            }
        }
    }
}