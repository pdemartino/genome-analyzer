package com.dmp.signalanalyzer;

import com.dmp.signalanalyzer.analyzers.HighPassFilter;
import com.dmp.signalanalyzer.analyzers.ISignalAnalyzer;
import com.dmp.signalanalyzer.analyzers.MaximumAnalysis;
import com.dmp.signalanalyzer.analyzers.MeanAnalysis;
import com.dmp.signalanalyzer.analyzers.MedianAnalysis;
import com.dmp.signalanalyzer.analyzers.NinetiethPercentileAnalysis;
import com.dmp.signalanalyzer.beans.PositionSortedSignal;
import com.dmp.signalanalyzer.beans.SlicedSignal;
import com.dmp.signalanalyzer.exceptions.SignalLengthMismatch;
import com.dmp.signalanalyzer.utils.CommandLineManager;
import com.dmp.signalanalyzer.utils.Commands;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author paco
 */
public class Main {
    static double stepMult = 1d/40;
    static double sizeMult = 1d/20; 
    static Boolean defaultAnalysisActivationState = true;
    
    static String positionsFileName;
    static String fstFileName;
    static String outputDirectory;
    static Double step = null;
    static Double size = null;
    
    static Map<String,Boolean> activeAnalysis = 
            new HashMap<String, Boolean>();
    
    
    public static void main(String[] args) {
        CommandLine cmdLine = parseCommandLine(args);
        if (cmdLine != null){
            //runAnalysis();
           runPassHigh();
        }
        
    }
    
    private static CommandLine parseCommandLine(String[] args){
        CommandLine cmdLine = null;
        try{
            cmdLine = CommandLineManager.parseCommandLine(args);
            if (cmdLine.hasOption("help")){
                CommandLineManager.printHelp();
                cmdLine = null;
            }else{
                fstFileName = cmdLine.getOptionValue(Commands.fst.name());
                positionsFileName = cmdLine.getOptionValue(Commands.positions.name());
                outputDirectory = cmdLine.getOptionValue(Commands.outputdirectory.name());
                step = cmdLine.hasOption(Commands.step.name()) 
                        ? Double.valueOf(cmdLine.getOptionValue(Commands.step.name())) 
                        : null;
                size = cmdLine.hasOption(Commands.size.name())
                        ? Double.valueOf(cmdLine.getOptionValue(Commands.size.name()))
                        : null;
                
                
            }
        }catch (ParseException ex){
            System.out.println("Error while parsing parameters: " + ex.getMessage());
            CommandLineManager.printHelp();
        }
        
        return cmdLine;
    }
    
    private static void runPassHigh(){
       PositionSortedSignal signal;
       HighPassFilter passH = new HighPassFilter();
        try {
           
            signal = new PositionSortedSignal(positionsFileName, fstFileName);
            passH.setSignal(signal);
                    
            
            // Max<cutoff,analysis>
            Map<Double,Object> resultsByCutOff = new HashMap<Double, Object>();
            
            
            double cutOff = 0.1;
            while (cutOff < 1){
               passH.setCufOffFrequency(cutOff);
               passH.runAnalysis();
                       
               cutOff = cutOff + 0.1;
               
               System.out.println("cufOff:" + cutOff);
               System.out.println(passH.getAnalysis());
            }
            
            
        }catch(FileNotFoundException ex){
           System.out.print("File not found");
        }catch(SignalLengthMismatch ex){
           System.out.println(ex.getMessage());
        }
    }
    
    private static void runAnalysis(){
        PositionSortedSignal signal;
        try {
            signal = new PositionSortedSignal(positionsFileName, fstFileName);
            
            if (size == null){
                size = signal.getStopPosition() * sizeMult;
            }
            
            if (step == null){
                step = signal.getStopPosition() * stepMult;
            }
            
            
            ISignalAnalyzer signalAnalyzer;
            
            signalAnalyzer = new MedianAnalysis(signal, size, step);
            signalAnalyzer.runAnalysis();
            String fileName = outputDirectory + File.separatorChar + "median_analysis.csv";
            System.out.println(String.format("Writing to %s...", fileName));
            try {
                ((SlicedSignal)signalAnalyzer.getAnalysis()).writeToCSV(fileName);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            signalAnalyzer = new MeanAnalysis(signal, size, step);
            signalAnalyzer.runAnalysis();
            fileName = outputDirectory + File.separatorChar + "mean_analysis.csv";
            System.out.println(String.format("Writing to %s...", fileName));
            try {
                ((SlicedSignal)signalAnalyzer.getAnalysis()).writeToCSV(fileName);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            signalAnalyzer = new MaximumAnalysis(signal, size, step);
            signalAnalyzer.runAnalysis();
            fileName = outputDirectory + File.separatorChar + "maximum_analysis.csv";
            System.out.println(String.format("Writing to %s...", fileName));
            try {
                ((SlicedSignal)signalAnalyzer.getAnalysis()).writeToCSV(fileName);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            signalAnalyzer = new NinetiethPercentileAnalysis(signal, size, step);
            signalAnalyzer.runAnalysis();
            fileName = outputDirectory + File.separatorChar + "ninetieth_analysis.csv";
            System.out.println(String.format("Writing to %s...", fileName));
            try {
                ((SlicedSignal)signalAnalyzer.getAnalysis()).writeToCSV(fileName);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SignalLengthMismatch ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
  
    }
   
}
