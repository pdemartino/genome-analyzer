package com.dmp.signalanalyzer.apps;

import com.dmp.signalanalyzer.configuration.ConfigurationManager;
import com.dmp.signalanalyzer.exceptions.SignalLengthMismatch;
import com.dmp.signalanalyzer.filters.CompositeLpHpUnbiasFilter;
import com.dmp.signalanalyzer.filters.CompositePositionAdapted;
import com.dmp.signalanalyzer.filters.FilterConfiguration;
import com.dmp.signalanalyzer.filters.HighPassFilter;
import com.dmp.signalanalyzer.filters.LowPassFilter;
import com.dmp.signalanalyzer.filters.PositionAdaptedFilter;
import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.filters.UnbiasFilter;
import com.dmp.signalanalyzer.filters.windowed.WindowedMaximumAnalysis;
import com.dmp.signalanalyzer.filters.windowed.WindowedMeanAnalysis;
import com.dmp.signalanalyzer.filters.windowed.WindowedMedianAnalysis;
import com.dmp.signalanalyzer.filters.windowed.WindowedNinetiethPercentileAnalysis;
import com.dmp.signalanalyzer.signal.Signal;
import com.dmp.signalanalyzer.utils.CommandLineManager;
import com.dmp.signalanalyzer.utils.Commands;
import com.dmp.signalanalyzer.utils.InputFilesLoader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class Main {

   static CommandLineManager clm;
   static ConfigurationManager configurationManager;

   public static void main(String[] args) {
      clm = CommandLineManager.getInstance();
      configurationManager = ConfigurationManager.getInstance();
      try {
         clm.parseCommandLine(args);
         checkOutputDirectory();
         runAnalysis();
      } catch (ParseException ex) {
         System.err.println(ex.getMessage());
         clm.printHelp();
      } catch (IOException ex){
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      } catch (SignalLengthMismatch ex){
         System.err.println(ex.getMessage());
      }

   }

   private static void runAnalysis() throws FileNotFoundException, SignalLengthMismatch, IOException {
      // Step 1: load signal
      Signal inputSignal = loadInputSignal();
      
      
      // Step 2: compute step and window for windowed analysis
      float windowMultiplier = clm.getArguments().containsKey(Commands.windowMultiplier.name()) 
              ? ((Float) clm.getArguments().get(Commands.windowMultiplier.name())).floatValue() 
              : configurationManager.getWindowsMultiplier();
      
      float window = clm.getArguments().containsKey(Commands.window.name())
              ? ((Float) clm.getArguments().get(Commands.window.name())).floatValue()
              : inputSignal.getTStop() * windowMultiplier;
      
      float stepMultiplier = clm.getArguments().containsKey(Commands.stepMultiplier.name()) 
              ? ((Float) clm.getArguments().get(Commands.stepMultiplier.name())).floatValue() 
              : configurationManager.getStepMultiplier();
      
      float step = clm.getArguments().containsKey(Commands.step.name())
              ? ((Float) clm.getArguments().get(Commands.step.name())).floatValue()
              : window * stepMultiplier;
      
      
      FilterConfiguration filterConfiguration = new FilterConfiguration();
      filterConfiguration.set("window", new Float(window));
      filterConfiguration.set("step", new Float(step));
      
     
      System.out.println(String.format("Windowed Analysis [window size: %s loci, step: %s loci]", 
              filterConfiguration.get("window"),
              filterConfiguration.get("step")));
      

      // Step 3: start analysing signal
      List<String> analysisToPerform =(List<String>) clm.getArguments().get(Commands.analysis.name());
      SignalFilter sa = null;
      Signal outSignal;
      String outPutDirectory = ((String)clm.getArguments().get(Commands.outputdirectory.name()));
      if (!outPutDirectory.endsWith(File.separator)){
         outPutDirectory += File.separator;
      }
      String outFilePath;
      for (String analysis : analysisToPerform){
         //[winmean winmedian winmax win90perc lowpass highpass unbias composite]
         if (analysis.equals("winmean")){
            sa = new WindowedMeanAnalysis();
         }else if (analysis.equals("winmax")){
            sa = new WindowedMaximumAnalysis();
         }else if (analysis.equals("win90perc")){
            sa = new WindowedNinetiethPercentileAnalysis();
         }else if (analysis.equals("lowpass")){
            sa = new LowPassFilter();
         }else if (analysis.equals("highpass")){
            sa = new HighPassFilter();
         }else if (analysis.equals("unbias")){
            sa = new UnbiasFilter();
         }else if (analysis.equals("winmedian")){
            sa = new WindowedMedianAnalysis();
         }else if (analysis.equals("composite")){
            sa = new CompositeLpHpUnbiasFilter();
         }else if (analysis.equals("positionAdapted")){
            sa = new PositionAdaptedFilter();
         }else if (analysis.equals("positionAdaptedComposite")){
            sa = new CompositePositionAdapted();
         }
         else {
             continue; // skip wrong requests
         } 
         
         sa.setFilterConfiguration(filterConfiguration);
         outSignal = sa.filter(inputSignal);
         outFilePath = outPutDirectory + sa.getName() + configurationManager.getOutputFileExtension();
         System.out.println(String.format("Writing %s result into %s ...", sa.getName(), outFilePath));
         outSignal.writeToFile(outFilePath, configurationManager.getOutputFileSeparatorChar());
         // Select TopFivePercent
         //outSignal = TopFivePercent.staticFilter(outSignal);
         //outFilePath = outPutDirectory + sa.getName() + "-Top5Percent" +SignalAnalyzerConstants.CSV_EXTENSION;
         //System.out.println(String.format("Writing %s result into %s ...", sa.getName(), outFilePath));
         //outSignal.writeToFile(outFilePath, SignalAnalyzerConstants.CSV_SEPARATOR);
         
         // try to free up some memory calling the garbage collector
         outSignal = null;
         System.gc();
      }

   }
   
   private static Signal loadInputSignal() throws FileNotFoundException, IOException, SignalLengthMismatch{
      Signal inputSignal = new Signal();
      
      if (clm.getArguments().containsKey(Commands.lowerBound.name())){
          inputSignal.setLowerBound(((Float)clm.getArguments().get(Commands.lowerBound.name())).floatValue());
      }
      
      if (clm.getArguments().containsKey(Commands.upperBound.name())){
          inputSignal.setUpperBound(((Float)clm.getArguments().get(Commands.upperBound.name())).floatValue());
      }
      
      String signalValuesFileName = (String) clm.getArguments().get(Commands.signal.name());
      Integer signalValuesColumn = (Integer) clm.getArguments().get(Commands.signalColumn.name());
      float[] signalArray = InputFilesLoader.csvToFloatArray(signalValuesFileName, signalValuesColumn.intValue(), configurationManager.getInputFileSeparatorChar());
      

      if (clm.getArguments().containsKey(Commands.positions.name())) {
         String positionsFileName = (String) clm.getArguments().get(Commands.positions.name());
         Integer positionsColumn = (Integer) clm.getArguments().get(Commands.positionsColumn.name());
         inputSignal.addPulsesArray(signalArray, InputFilesLoader.csvToFloatArray(positionsFileName, positionsColumn, configurationManager.getInputFileSeparatorChar()));
      }else{
         inputSignal.addPulsesArray(signalArray);
      }
      
      return inputSignal;
   }

    private static void checkOutputDirectory() {
        File fp = new File((String)clm.getArguments().get(Commands.outputdirectory.name()));
        if (!fp.isDirectory()){
            fp.mkdirs();
        }
    }

}
