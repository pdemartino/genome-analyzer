package com.dmp.signalanalyzer.apps;

import com.dmp.signalanalyzer.beans.Signal;
import com.dmp.signalanalyzer.exceptions.SignalLengthMismatch;
import com.dmp.signalanalyzer.filters.CompositeLpHpUnbiasFilter;
import com.dmp.signalanalyzer.filters.HighPassFilter;
import com.dmp.signalanalyzer.filters.LowPassFilter;
import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.filters.UnbiasFilter;
import com.dmp.signalanalyzer.filters.windowed.WindowedMaximumAnalysis;
import com.dmp.signalanalyzer.filters.windowed.WindowedMeanAnalysis;
import com.dmp.signalanalyzer.filters.windowed.WindowedMedianAnalysis;
import com.dmp.signalanalyzer.filters.windowed.WindowedNinetiethPercentileAnalysis;
import com.dmp.signalanalyzer.utils.CommandLineManager;
import com.dmp.signalanalyzer.utils.Commands;
import com.dmp.signalanalyzer.utils.SignalAnalyzerConstants;
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

   public static void main(String[] args) {
      clm = CommandLineManager.getInstance();
      try {
         clm.parseCommandLine(args);
         checkOutputDirectory();
         runAnalysis();
      } catch (ParseException ex) {
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex){
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      } catch (SignalLengthMismatch ex){
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      }

   }

   private static void runAnalysis() throws FileNotFoundException, SignalLengthMismatch, IOException {
      // Step 1: load signal
      Signal inputSignal = new Signal();
      String signalValuesFileName = (String) clm.getArguments().get(Commands.signal.name());

      if (clm.getArguments().containsKey(Commands.positions.name())) {
         String positionsFileName = (String) clm.getArguments().get(Commands.positions.name());
         inputSignal.loadPulsesFromFile(positionsFileName, signalValuesFileName);
      }

      // Step 2: compute step and window for windowed analysis
      float step = inputSignal.getTStart() * SignalAnalyzerConstants.STEP_MULT;
      float window = inputSignal.getTStop() * SignalAnalyzerConstants.WINDOW_MULT;
      // override with user preferences
      if (clm.getArguments().containsKey(Commands.step.name())) {
         step = ((Float) clm.getArguments().get(Commands.step.name())).floatValue();
      }
      if (clm.getArguments().containsKey(Commands.window.name())) {
         window = ((Float) clm.getArguments().get(Commands.window.name())).floatValue();
      }

      // Step 3: start analysing signal
      List<String> analysisToPerform =(List<String>) clm.getArguments().get(Commands.analysis.name());
      SignalFilter sa = null;
      Signal outSignal;
      String outFileName;
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
         } 
         
         outFileName = ((String)clm.getArguments().get(Commands.outputdirectory.name()))
                 + File.separator + sa.getName() + SignalAnalyzerConstants.CSV_EXTENSION;
         outSignal = sa.filter(inputSignal);
         System.out.println(String.format("Writing %s result into %s ...", sa.getName(),outFileName));
         outSignal.writeToFile(outFileName, SignalAnalyzerConstants.CSV_SEPARATOR);
      }

   }

    private static void checkOutputDirectory() {
        File fp = new File((String)clm.getArguments().get(Commands.outputdirectory.name()));
        if (!fp.isDirectory()){
            fp.mkdirs();
        }
    }

}
