package com.dmp.signalanalyzer.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
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
      //mandatory
      arguments.put(Commands.signal.name()
              ,(String)cmLine.getOptionValue(Commands.signal.name()));
      arguments.put(Commands.outputdirectory.name()
              ,(String)cmLine.getOptionValue(Commands.outputdirectory.name()));
      
      // optional
      if (cmLine.hasOption(Commands.positions.name())){
         arguments.put(Commands.positions.name()
                 , (String) cmLine.getOptionValue(Commands.positions.name()));
      }
      
      if (cmLine.hasOption(Commands.step.name())){
         arguments.put(Commands.step.name()
                 , Double.valueOf(cmLine.getOptionValue(Commands.step.name())));
      }
      
      if (cmLine.hasOption(Commands.window.name())){
         arguments.put(Commands.window.name()
                 , Double.valueOf(cmLine.getOptionValue(Commands.window.name())));
      }
      
      // analysis
      String[] analysis = cmLine.getOptionValues(Commands.analysis.name());
      List<String> anList = new ArrayList<String>();
      for (String ancur : analysis){
         anList.add(ancur);
      }
      arguments.put(Commands.analysis.name(), anList);
      
  
      
 
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
              .isRequired(false)
              .withDescription("File containing positions as a column (use '.' (dot) as decimal separator); if no file is specified then ordinals are used as positions")
              .create(Commands.positions.name()));

      commandLineOptions.addOption(
              OptionBuilder.withArgName("signal filename")
              .hasArg()
              .isRequired(true)
              .withDescription("File containing signal values as a column (use '.' (dot) as decimal separator)")
              .create(Commands.signal.name()));
      
      commandLineOptions.addOption(
              OptionBuilder.withArgName("analysis")
              .hasArgs(1)
              .hasOptionalArgs(SignalAnalyzerConstants.NUM_ANALYSIS -1)
              .isRequired(true)
              .withDescription("Analysis and filters to apply on signal [winmean winmedian winmax win90perc lowpass highpass unbias composite]"
              + "\nwinmean: windowed filter using window average value"
              + "\nwinmedian: windowed filter using window median value"
              + "\nwinmax: windowed filter using window maximum value"
              + "\nwin90perc: windowed filter using window ninetieth percentile value"
              + "\nlowpass: lowpass frequency filter"
              + "\nhighpass: highpass frequency filter"
              + "\nunbias: frequency filter which reduce each pulse using signal mooving average value"
              + "\ncomposite: combine unbian(highpass(lowpass))"
              + "\nAt least one analysis has to be specified"
              + "\nFor windowed analysis you can specify step and window size")
              .create(Commands.analysis.name()));


      commandLineOptions.addOption(
              OptionBuilder
              .isRequired(false)
              .hasArg()
              .withArgName("step")
              .withDescription("Distance between two successive windows (used for windowed filters)")
              .create(Commands.step.name()));

      commandLineOptions.addOption(
              OptionBuilder.withArgName("window")
              .isRequired(false)
              .hasArg()
              .withDescription("Size of a window (used for windowed filters)")
              .create(Commands.window.name()));

      commandLineOptions.addOption(
              OptionBuilder.withArgName("output directory")
              .isRequired(true)
              .hasArg()
              .withDescription("Directory where to put analysis files (one for each analysis)")
              .create(Commands.outputdirectory.name()));


      return commandLineOptions;
   }
   
   public Map<String,Object> getArguments(){
      return this.arguments;
   }
}
