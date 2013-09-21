package com.dmp.signalanalyzer.apps;

import com.dmp.signalanalyzer.apps.commandline.ConfigurationManager;
import com.dmp.signalanalyzer.apps.commandline.CommandLineOption;
import com.dmp.signalanalyzer.apps.commandline.PropertiesEnum;
import com.dmp.signalanalyzer.apps.io.InputFilesLoader;
import com.dmp.signalanalyzer.apps.io.OutputManager;
import com.dmp.signalanalyzer.apps.logic.FilterRunner;
import com.dmp.signalanalyzer.exceptions.InvalidInputException;
import com.dmp.signalanalyzer.exceptions.SignalLengthMismatch;
import com.dmp.signalanalyzer.filters.FilterConfiguration;
import com.dmp.signalanalyzer.signal.RecombinationMap;
import com.dmp.signalanalyzer.signal.Signal;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class Main {

   static OutputManager outputManager = new OutputManager();
   static ConfigurationManager configurationManager;
   static Signal inputSignal;
   static RecombinationMap recombinationMap;
   static final Logger logger = Logger.getLogger(Main.class.getName());

   public static void main(String[] args) {

      try {
         loadConfiguration(args);
         loadInputSignal();

         runAnalysis();
      } catch (ParseException ex) {
         logger.error(ex.getMessage());
         ConfigurationManager.printHelp();
      } catch (IOException ex) {
         logger.error(String.format("Error on filesystem operation: ", ex.getMessage()));
      } catch (SignalLengthMismatch ex) {
         logger.error(ex.getMessage());
      }catch (InvalidInputException ex){
         logger.error(ex.getMessage());
      }

   }

   private static void loadConfiguration(String[] args) throws ParseException {
      configurationManager = ConfigurationManager.getInstance();
      configurationManager.parseCommandLine(args);

      outputManager.setDirectory((String) configurationManager.getConfigurationValue(PropertiesEnum.outputDirectory.name()));
      outputManager.setValueSeparator((String) configurationManager.getConfigurationValue(PropertiesEnum.outputFileSeparator.name()));
      outputManager.setFileExtension((String) configurationManager.getConfigurationValue(PropertiesEnum.outputFileExtension.name()));

   }

   private static FilterConfiguration loadFilterConfiguration() {
      FilterConfiguration filterConfiguration = new FilterConfiguration();

      if (configurationManager.hasConfigurationValue(PropertiesEnum.normalizeUsingPosition.name())) {
         filterConfiguration.set(PropertiesEnum.normalizeUsingPosition.name(),
                 configurationManager.getConfigurationValue(PropertiesEnum.normalizeUsingPosition.name()));
      }

      if (configurationManager.hasConfigurationValue(PropertiesEnum.smoothingFactor.name())) {
         filterConfiguration.set(PropertiesEnum.smoothingFactor.name(),
                 configurationManager.getConfigurationValue(PropertiesEnum.smoothingFactor.name()));
      }

      if (configurationManager.hasConfigurationValue(PropertiesEnum.step.name())) {
         filterConfiguration.set(PropertiesEnum.step.name(),
                 configurationManager.getConfigurationValue(PropertiesEnum.step.name()));
      }

      if (configurationManager.hasConfigurationValue(PropertiesEnum.window.name())) {
         filterConfiguration.set(PropertiesEnum.window.name(),
                 configurationManager.getConfigurationValue(PropertiesEnum.window.name()));
      }

      if (configurationManager.hasConfigurationValue(PropertiesEnum.backward.name())) {
         filterConfiguration.set(PropertiesEnum.backward.name(),
                 configurationManager.getConfigurationValue(PropertiesEnum.backward.name()));
      }

      if (configurationManager.hasConfigurationValue(PropertiesEnum.twoWay.name())) {
         filterConfiguration.set(PropertiesEnum.twoWay.name(),
                 configurationManager.getConfigurationValue(PropertiesEnum.twoWay.name()));
      }

      logger.info("Running analysis with the following configuration: \n" + filterConfiguration);
      return filterConfiguration;

   }

   private static void runAnalysis() throws FileNotFoundException, SignalLengthMismatch, IOException {
      FilterRunner filterRunner = new FilterRunner();
      filterRunner.setFilterConfiguration(loadFilterConfiguration());


      //  start analysing signal
      List<String> analysisToPerform = (List<String>) configurationManager.getConfigurationValue(CommandLineOption.analysis.name());

      for (String analysis : analysisToPerform) {
         logger.info("Running filter " + analysis +"...");
         Signal outSignal = filterRunner.run(inputSignal, analysis);
         outputManager.writeToFile(outSignal, analysis, false, recombinationMap);  
      }

   }

   private static void loadInputSignal() throws FileNotFoundException, IOException, SignalLengthMismatch, InvalidInputException {
      logger.debug("Loading input signal...");
      inputSignal = new Signal();

      if (configurationManager.getConfigurationValue(CommandLineOption.lowerBound.name()) != null){
    	  inputSignal.setLowerBound(Double.valueOf(configurationManager.getConfigurationValue(CommandLineOption.lowerBound.name()).toString())); 
      }
      
      if (configurationManager.getConfigurationValue(CommandLineOption.upperBound.name()) != null){
    	  inputSignal.setUpperBound(Double.valueOf(configurationManager.getConfigurationValue(CommandLineOption.upperBound.name()).toString())); 
      }
      
      Object[] signalArguments = ConfigurationManager.splitFileNameAndColumn(configurationManager.getConfigurationValue(CommandLineOption.signal.name()).toString());
      String signalValuesFileName = (String) signalArguments[0];
      Integer signalValuesColumn = (Integer) signalArguments[1];
      String inputFileSeparator = (String) configurationManager.getConfigurationValue(PropertiesEnum.inputFileSeparator.name());
      Boolean skipInputHeader = Boolean.valueOf((String) configurationManager.getConfigurationValue(PropertiesEnum.skipInputHeader.name()));
      logger.debug(String.format("Loading signal values from %s from column %s (ColSeparator: %s)", signalValuesFileName, signalValuesColumn, inputFileSeparator));
      Double[] signalArray = InputFilesLoader.csvToDoubleArray(signalValuesFileName, signalValuesColumn.intValue(), inputFileSeparator, skipInputHeader);


      if (configurationManager.hasConfigurationValue(CommandLineOption.positions.name())) {
         Object[] positionArguments = ConfigurationManager.splitFileNameAndColumn((String) configurationManager.getConfigurationValue(CommandLineOption.positions.name()));
         String positionsFileName = (String) positionArguments[0];
         Integer positionsColumn = (Integer) positionArguments[1];
         logger.debug(String.format("Loading positions from %s from column %s (ColSeparator: %s)", positionsFileName, positionsColumn, inputFileSeparator));
         Double[] positionsArray = InputFilesLoader.csvToDoubleArray(positionsFileName, positionsColumn, inputFileSeparator, skipInputHeader);
         inputSignal.addComponentsArray(signalArray, positionsArray);
      } else {
         inputSignal.addComponentsArray(signalArray);
      }
      logger.debug(String.format("Loaded a signal %s items long", inputSignal.count()));

      if (configurationManager.hasConfigurationValue(CommandLineOption.recombinationMap.name())) {
         loadRecombinationMap(inputFileSeparator, skipInputHeader);
         inputSignal = recombinationMap.applyRecombinationMap(inputSignal);
         outputManager.setRecombinationMap(recombinationMap);
      }
   }

   private static void loadRecombinationMap(String inputFileSeparator, Boolean skipInputHeader) throws IOException, SignalLengthMismatch, FileNotFoundException, InvalidInputException {
      String recombinationMapCommand = (String) configurationManager.getConfigurationValue(CommandLineOption.recombinationMap.name());
      String[] recombinationParameters = recombinationMapCommand.split(":");

      if (recombinationParameters.length < 3) {
         logger.error("Wrong recombination map parameter");
         System.exit(1);
      } else {
         String filename = recombinationParameters[0];
         int positionColumn = (new Integer(recombinationParameters[1])).intValue();
         int recombinationColumn = (new Integer(recombinationParameters[2])).intValue();

         Double[] positionsArray = InputFilesLoader.csvToDoubleArray(filename, positionColumn, inputFileSeparator, skipInputHeader);
         Double[] recombinationArray = InputFilesLoader.csvToDoubleArray(filename, recombinationColumn, inputFileSeparator, skipInputHeader);

         recombinationMap = new RecombinationMap(positionsArray, recombinationArray);
      }
   }
}
