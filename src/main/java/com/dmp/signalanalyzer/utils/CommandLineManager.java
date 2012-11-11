package com.dmp.signalanalyzer.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CommandLineManager {
    // TODO add validation

    CommandLineParser parser = new GnuParser();

    public static CommandLine parseCommandLine(String[] argv) throws ParseException {
        CommandLine cmLine;
        CommandLineParser parser = new PosixParser();
        cmLine = parser.parse(buildOptions(), argv);
        return cmLine;
    }

    public static void printHelp() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Syntax", buildOptions());
    }

    private static Options buildOptions() {
        Options commandLineOptions = new Options();

        commandLineOptions.addOption(
                OptionBuilder
                .hasArg()
                .withArgName("positions filename")
                .isRequired(true)
                .withDescription("File containing positions as a column")
                .create(Commands.positions.name()));

        commandLineOptions.addOption(
                OptionBuilder.withArgName("fst filename")
                .hasArg()
                .isRequired(true)
                .withDescription("File containing fst values as a column")
                .create(Commands.fst.name()));

        commandLineOptions.addOption(
                OptionBuilder
                .isRequired(false)
                .hasArg()
                .withArgName("step")
                .withDescription("Distance between two successive slices")
                .create(Commands.step.name()));

        commandLineOptions.addOption(
                OptionBuilder.withArgName("size")
                .isRequired(false)
                .hasArg()
                .withDescription("Size of a slice")
                .create(Commands.size.name()));

        commandLineOptions.addOption(
                OptionBuilder.withArgName("output directory")
                .isRequired(true)
                .hasArg()
                .withDescription("Directory where to put analysis files (one for each analysis)")
                .create(Commands.outputdirectory.name()));


        return commandLineOptions;
    }
}
