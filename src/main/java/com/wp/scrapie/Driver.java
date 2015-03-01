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
		Option outputOption = new Option("o", "output", true,
				"The file to output to");
		Option logLevelOption = new Option("v", "verbosity", true,
				"Log Level, trace, debug, info (default)");
		Option recordOption = new Option("r", "record", true,
				"Record this run and stop after N records have been emmited");
		Option typeOption = new Option("t", "type", true,
				"The record type, json or xml (default)");
		Option noCacheLoginOption = new Option("c", "cachelessLogin", true,
				"Always go online for login attempts so that cookies are retrieved and stored.");
		filenameOption.setRequired(true);
		outputOption.setRequired(true);
		options.addOption(filenameOption);
		options.addOption(outputOption);
		options.addOption(logLevelOption);
		options.addOption(recordOption);
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
			String recordValue = cmd.getOptionValue('r', "0");
			boolean noLoginCache = cmd.hasOption('c');
			Emitter emitter = new Emitter();
			Emitter.setRecord(new Integer(recordValue).intValue());
			Emitter.setNoLoginCache(noLoginCache);
			Writer out = null;
			String type = cmd.getOptionValue('t', "xml");
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
