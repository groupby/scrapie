package com.wp.scrapie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
/**
 * @internal
 * @author will
 *
 */
public class Driver {

	private static Options options = new Options();

	static {
		Option filenameOption = new Option("f", "file", true,
				"The JavaScript file to use");
		Option outputOption = new Option("o", "output", true,
				"The file to output to");
		Option logLevelOption = new Option("v", "verbosity", true,
				"Log Level, trace, debug, info (default)");
		Option recordOption = new Option("r", "record", true,
				"Record this run and stop after N flushes");
		Option typeOption = new Option("t", "type", true,
				"The record type, json or xml (default)");
		filenameOption.setRequired(true);
		outputOption.setRequired(true);
		options.addOption(filenameOption);
		options.addOption(outputOption);
		options.addOption(logLevelOption);
		options.addOption(recordOption);
		options.addOption(typeOption);
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
			String outputFilename = cmd.getOptionValue('o');
			String recordValue = cmd.getOptionValue('r', "0");
			Js js = new Js();
			Js.setRecord(new Integer(recordValue).intValue());
			Writer out = null;
			String type = cmd.getOptionValue('t', "xml");
			if (type.equals("xml")) {
				out = new XmlWriter(new FileWriter(outputFilename));
			} else {
				out = new FileWriter(outputFilename);
			}
			js.runFile(filename, out);
			out.flush();
			out.close();
		} catch (ParseException e) {
			usage(e);
		} catch (FileNotFoundException e) {
			usage("Couldn't find file: " + new File(filename).getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
