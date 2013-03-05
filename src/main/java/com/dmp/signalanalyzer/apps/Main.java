package com.dmp.signalanalyzer.apps;

import com.dmp.signalanalyzer.apps.commandline.CommandLineManager;
import com.dmp.signalanalyzer.apps.commandline.CommandLineOption;
import com.dmp.signalanalyzer.apps.io.InputFilesLoader;
import com.dmp.signalanalyzer.apps.io.SignalIoManager;
import com.dmp.signalanalyzer.configuration.ConfigurationManager;
import com.dmp.signalanalyzer.exceptions.SignalLengthMismatch;
import com.dmp.signalanalyzer.filters.CompositeFilter;
import com.dmp.signalanalyzer.filters.FilterConfiguration;
import com.dmp.signalanalyzer.filters.HighPassFilter;
import com.dmp.signalanalyzer.filters.LowPass;
import com.dmp.signalanalyzer.filters.NinetiethPercentSelector;
import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.filters.UnbiasFilter;
import com.dmp.signalanalyzer.filters.windowed.WindowedMeanAnalysis;
import com.dmp.signalanalyzer.filters.windowed.WindowedMedianAnalysis;
import com.dmp.signalanalyzer.filters.windowed.WindowedNinetiethPercentileAnalysis;
import com.dmp.signalanalyzer.signal.RecombinationMap;
import com.dmp.signalanalyzer.signal.Signal;
import com.dmp.signalanalyzer.signal.SignalStats;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class Main {

   static CommandLineManager clm;
   static ConfigurationManager configurationManager;
   static Signal inputSignal;
   static RecombinationMap recombinationMap;
   static final Logger logger = Logger.getLogger(Main.class.getName());

   public static void main(String[] args) {
      clm = CommandLineManager.getInstance();
      configurationManager = ConfigurationManager.getInstance();

      try {
         clm.parseCommandLine(args);
         loadInputSignal();
         if (clm.getArguments().containsKey(CommandLineOption.stats.name())) {
            SignalStats stats = inputSignal.getStatistics();
            System.out.println(stats);
            System.exit(0);
         }

         if (clm.getArguments().containsKey(CommandLineOption.recombinationMap.name())) {
            loadRecombinationMap();
            inputSignal = recombinationMap.applyRecombinationMap(inputSignal);
         }

         checkOutputDirectory();
         runAnalysis();
      } catch (ParseException ex) {
         logger.error(ex.getMessage());
         CommandLineManager.printHelp();
      } catch (IOException ex) {
         logger.error(String.format("Error on filesystem operation: ", ex.getMessage()));
      } catch (SignalLengthMismatch ex) {
         logger.error(ex.getMessage());
      }

   }

   private static void runAnalysis() throws FileNotFoundException, SignalLengthMismatch, IOException {
      FilterConfiguration filterConfiguration = new FilterConfiguration();
      NinetiethPercentSelector ninetiethPercSelector = new NinetiethPercentSelector();

      //  compute step and window for windowed analysis
      double windowMultiplier = configurationManager.getWindowsMultiplier();
      double window = inputSignal.getTStop() * windowMultiplier;

      double stepMultiplier = configurationManager.getStepMultiplier();
      double step = window * stepMultiplier;

      filterConfiguration.set("window", Double.valueOf(window));
      filterConfiguration.set("step", Double.valueOf(step));

      if (clm.getArguments().containsKey(CommandLineOption.smoothingFactor.name())) {
         filterConfiguration.set("smoothingFactor", Float.valueOf((String) clm.getArguments().get(CommandLineOption.smoothingFactor.name())));
      } else {
         filterConfiguration.set("smoothingFactor", Float.valueOf(configurationManager.getSmoothingFactor()));
      }
      
      filterConfiguration.set("normalizeUsingPosition", Boolean.valueOf(configurationManager.isNormalizeUsingPosition()));

      //  start analysing signal
      String[] analysisToPerform = CommandLineManager.splitMultipleArguments((String) clm.getArguments().get(CommandLineOption.analysis.name()));
      SignalFilter sa = null;
      Signal outSignal;
      String outPutDirectory = ((String) clm.getArguments().get(CommandLineOption.outputdirectory.name()));
      if (!outPutDirectory.endsWith(File.separator)) {
         outPutDirectory += File.separator;
      }

      // Saving input data
      SignalIoManager.writeToFile(inputSignal, recombinationMap, outPutDirectory, "inputSignal", "");
      for (String analysis : analysisToPerform) {
         String fileNameAppend = "";

         //[winmean winmedian winmax win90perc lowpass highpass unbias composite]
         analysis = analysis.toLowerCase();
         if (analysis.startsWith("lowpass")) {
            sa = new LowPass();
            fileNameAppend = retrieveFilterConfigurationByName(analysis, filterConfiguration);
         } else if (analysis.startsWith("highpass")) {
            sa = new HighPassFilter();
         } else if (analysis.startsWith("unbias")) {
            sa = new UnbiasFilter();
         } else if (analysis.startsWith("composite")) {
            sa = new CompositeFilter();
            fileNameAppend = retrieveFilterConfigurationByName(analysis, filterConfiguration);
         } else if (analysis.startsWith("winmean")) {
            sa = new WindowedMeanAnalysis();
         } else if (analysis.startsWith("winmedian")) {
            sa = new WindowedMedianAnalysis();
         } else if (analysis.startsWith("win90perc")) {
            sa = new WindowedNinetiethPercentileAnalysis();
         } else {
            continue; // skip wrong requests
         }
         sa.setFilterConfiguration(filterConfiguration);
         logger.debug(String.format("Running %s with configuration: %s", sa.getName(), sa.getFilterConfiguration()));
         outSignal = sa.filter(inputSignal);

         logger.debug(String.format("%s analysis generated a signal %s items long, writing it to file...", sa.getName(), outSignal.count()));
         SignalIoManager.writeToFile(outSignal, recombinationMap, outPutDirectory, sa.getName(), fileNameAppend);

         // Mark Selected 
         Signal selected = ninetiethPercSelector.filter(outSignal);
         SignalIoManager.writeToFile(selected, recombinationMap, outPutDirectory, sa.getName() + "-SELECTED", fileNameAppend);

         // try to free up some memory calling the garbage collector
         outSignal = null;
         System.gc();
      }

   }

   private static String retrieveFilterConfigurationByName(String name, FilterConfiguration ioConfiguration) {
      String fileNameAppend = "";
      if (name.contains("backward")) {
         ioConfiguration.set("backward", Boolean.TRUE);
         fileNameAppend += "Backward";
      } else {
         ioConfiguration.set("backward", Boolean.FALSE);
      }
      if (name.contains("twoway")) {
         ioConfiguration.set("twoWay", Boolean.TRUE);
         fileNameAppend += "TwoWay";
      } else {
         ioConfiguration.set("twoWay", Boolean.FALSE);
      }

      if (name.contains("positionadapted")) {
         ioConfiguration.set("normalizeUsingPosition", Boolean.TRUE);
         fileNameAppend += "PositionAdapted";
      } else {
         ioConfiguration.set("normalizeUsingPosition", Boolean.FALSE);
      }
      return fileNameAppend;
   }

   private static void loadInputSignal() throws FileNotFoundException, IOException, SignalLengthMismatch {
      logger.debug("Loading input signal...");
      inputSignal = new Signal();

      if (clm.getArguments().containsKey(CommandLineOption.lowerBound.name())) {
         inputSignal.setLowerBound(Double.valueOf((String) clm.getArguments().get(CommandLineOption.lowerBound.name())));
      }

      if (clm.getArguments().containsKey(CommandLineOption.upperBound.name())) {
         inputSignal.setUpperBound(Double.valueOf((String) clm.getArguments().get(CommandLineOption.upperBound.name())));
      }

      Object[] signalArguments = CommandLineManager.splitFileNameAndColumn((String) clm.getArguments().get(CommandLineOption.signal.name()));
      String signalValuesFileName = (String) signalArguments[0];
      Integer signalValuesColumn = (Integer) signalArguments[1];
      logger.debug(String.format("Loading signal values from %s from column %s (ColSeparator: %s)", signalValuesFileName, signalValuesColumn, configurationManager.getInputFileSeparator()));
      Double[] signalArray = InputFilesLoader.csvToDoubleArray(signalValuesFileName, signalValuesColumn.intValue(), configurationManager.getInputFileSeparator(), clm.getArguments().containsKey(CommandLineOption.skipHeader.name()));


      if (clm.getArguments().containsKey(CommandLineOption.positions.name())) {
         Object[] positionArguments = CommandLineManager.splitFileNameAndColumn((String) clm.getArguments().get(CommandLineOption.positions.name()));
         String positionsFileName = (String) positionArguments[0];
         Integer positionsColumn = (Integer) positionArguments[1];
         logger.debug(String.format("Loading positions from %s from column %s (ColSeparator: %s)", positionsFileName, positionsColumn, configurationManager.getInputFileSeparator()));
         Double[] positionsArray = InputFilesLoader.csvToDoubleArray(positionsFileName, positionsColumn, configurationManager.getInputFileSeparator(), clm.getArguments().containsKey(CommandLineOption.skipHeader.name()));
         inputSignal.addComponentsArray(signalArray, positionsArray);
      } else {
         inputSignal.addComponentsArray(signalArray);
      }
      logger.debug(String.format("Loaded a signal %s items long", inputSignal.count()));
   }

   private static void checkOutputDirectory() {
      File fp = new File((String) clm.getArguments().get(CommandLineOption.outputdirectory.name()));
      if (!fp.isDirectory()) {
         fp.mkdirs();
      }
   }

   private static void loadRecombinationMap() throws IOException, SignalLengthMismatch {
      String recombinationMapCommand = (String) clm.getArguments().get(CommandLineOption.recombinationMap.name());
      String[] recombinationParameters = recombinationMapCommand.split(":");

      if (recombinationParameters.length < 3) {
         logger.error("Wrong recombination map parameter");
         System.exit(1);
      } else {
         String filename = recombinationParameters[0];
         int positionColumn = (new Integer(recombinationParameters[1])).intValue();
         int recombinationColumn = (new Integer(recombinationParameters[2])).intValue();

         Double[] positionsArray = InputFilesLoader.csvToDoubleArray(filename, positionColumn, configurationManager.getInputFileSeparator(), clm.getArguments().containsKey(CommandLineOption.skipHeader.name()));
         Double[] recombinationArray = InputFilesLoader.csvToDoubleArray(filename, recombinationColumn, configurationManager.getInputFileSeparator(), clm.getArguments().containsKey(CommandLineOption.skipHeader.name()));

         recombinationMap = new RecombinationMap(positionsArray, recombinationArray);
      }
   }
}
