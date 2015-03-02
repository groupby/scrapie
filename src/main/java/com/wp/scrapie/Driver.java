package com.wp.scrapie;

import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @internal
 * @author will.warren@groupbyinc.com
 *
 */
public class Driver {

    private static final Logger LOG = Logger.getLogger(Driver.class);
    private static Options options = new Options();

    static {
        Option filenameOption = new Option("f", "file", true,
                "The JavaScript file to use");
        filenameOption.setRequired(true);
        Option outputOption = new Option("o", "output", true,
                "The file to output to");
        outputOption.setRequired(true);
        Option logLevelOption = new Option("v", "verbosity", true,
                "Log Level, trace, debug, info (default)");
        Option noCache = new Option("n", "noCache", false,
                "Do not load URLs from cache");
        Option max = new Option("r", "maxRecords", true,
                "Stop processing after N records");
        Option typeOption = new Option("t", "type", true,
                "The record type, json (default) or xml");
        Option noCacheLoginOption = new Option("l", "loginLive", false,
                "Always go online for login attempts so that cookies are retrieved and stored.");

        options.addOption(filenameOption);
        options.addOption(outputOption);
        options.addOption(logLevelOption);
        options.addOption(noCache);
        options.addOption(max);
        options.addOption(typeOption);
        options.addOption(noCacheLoginOption);
    }

    public static void main(String[] args) {
        CommandLineParser parser = new BasicParser();

        String filename = null;
        try {
            CommandLine cmd = parser.parse(options, args);
            filename = cmd.getOptionValue('f');
            String logLevel = cmd.getOptionValue('v', "info");
            if (logLevel.equals("info")) {
                Logger.getRootLogger().setLevel(Level.INFO);
            } else if (logLevel.equals("debug")) {
                Logger.getRootLogger().setLevel(Level.DEBUG);
            } else if (logLevel.equals("trace")) {
                Logger.getRootLogger().setLevel(Level.TRACE);
            }
            String outputFilename = System.getProperty("workingDir", "")
                    + (System.getProperty("workingDir", null) != null ? "/"
                    : "") + cmd.getOptionValue('o');
            String maxRecords = cmd.getOptionValue('r', "0");
            boolean noLoginCache = cmd.hasOption('l');
            boolean noCache = cmd.hasOption('n');
            Emitter emitter = new Emitter();
            Emitter.setMaxRecords(new Integer(maxRecords).intValue());
            Emitter.setNoLoginCache(noLoginCache);
            Emitter.setNoCache(noCache);
            Writer out = null;
            String type = cmd.getOptionValue('t', "json");
            if (type.equals("xml")) {
                out = new XmlWriter(new FileWriter(outputFilename));
            } else {
                out = new FileWriter(outputFilename);
            }
            emitter.runFile(filename, out);
            out.flush();
            out.close();
        } catch (ParseException e) {
            printError(e);
            usage(e);
        } catch (FileNotFoundException e) {
            printError(e);
            usage(e.getMessage());
        } catch (IOException e) {
            printError(e);
            e.printStackTrace();
        }

    }

    private static void printError(Exception e) {
        if (LOG.isDebugEnabled()) {
            LOG.error(e);
        }
    }

    private static void usage(String poutorMessage) {
        System.out.println(poutorMessage);
        usage();
    }

    private static void usage() {
        System.out.println();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("scrapie", options);
        System.exit(-3);
    }

    private static void usage(ParseException pE) {
        System.out.println(pE.getMessage());
        usage();
    }

}
