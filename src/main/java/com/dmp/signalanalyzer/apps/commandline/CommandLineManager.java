package com.dmp.signalanalyzer.apps.commandline;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CommandLineManager {

   private static CommandLineManager singleton = null;
   private Map<String, Object> arguments = null;

   public static CommandLineManager getInstance() {
      if (singleton == null) {
         singleton = new CommandLineManager();
      }
      return singleton;
   }

   private CommandLineManager() {
   }

   public void parseCommandLine(String[] argv) throws ParseException {
      CommandLine cmLine;
      CommandLineParser parser = new PosixParser();
      cmLine = parser.parse(buildOptions(), argv);

      this.arguments = new HashMap<String, Object>();

      for (CommandLineOption option : CommandLineOption.values()) {
         System.out.println("Checking for " + option.name());
         if (cmLine.hasOption(option.name())) {
            arguments.put(option.name(), cmLine.getOptionValue(option.name()));
         }
      }
   }

   public static void printHelp() {
      HelpFormatter helpFormatter = new HelpFormatter();
      helpFormatter.printHelp(120, "syntaxx", "header", buildOptions(), "footer");
   }

   private static Options buildOptions() {
      Options commandLineOptions = new Options();

      for (CommandLineOption option : CommandLineOption.values()) {
         commandLineOptions.addOption(
                 OptionBuilder
                 .hasOptionalArgs(option.getMaxNumOfArguments())
                 .withArgName(option.getArgumentName())
                 .isRequired(option.isMandatory())
                 .withDescription(option.getDescription())
                 .create(option.name()));
      }

      return commandLineOptions;
   }

   public Map<String, Object> getArguments() {
      return this.arguments;
   }

   public static Object[] splitFileNameAndColumn(String optionValue) {
      String separator = ":";
      Object[] outArray = new Object[2]; // file and column
      String[] splittedValue = optionValue.split(separator);

      // set filename
      outArray[0] = splittedValue[0];
      if (outArray.length > 1) {
         try {
            outArray[1] = Integer.valueOf(splittedValue[1]);
            outArray[1] = Integer.valueOf(Math.max(0, (Integer) outArray[1]));
         } catch (NumberFormatException ex) {
            outArray[1] = Integer.valueOf(0);
         }
      } else {
         outArray[1] = Integer.valueOf(0);
      }

      return outArray;
   }
   
   public static String[] splitMultipleArguments(String optionValue){
      String separator = " ";
      return optionValue.split(separator);
   }
}
