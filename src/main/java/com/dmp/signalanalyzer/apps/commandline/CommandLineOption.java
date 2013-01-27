package com.dmp.signalanalyzer.apps.commandline;


/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public enum CommandLineOption {
   // Command struncture:
   // enumName(mandatory:bool,minNumOfArguments,maxNumOfArguments,argumentsName,description) 

   positions(false, 1, 1,
   "positions_filename[:column]",
   "File containing positions as a column (use '.' (dot) as decimal "
   + "separator); if no file is specified then ordinals are used as "
   + "positions"),
   
   signal(true, 1, 1,
          "signal_filename[:column]",
           "File containing signal values as a column (use '.' (dot) as decimal separator)"),
   
   recombinationMap(false, 1, 1,
          "recombination-map_filename:position:genetic-map",
           "File containing mapping between physical and genetic positions"),
   
   outputdirectory(true,1,1,
           "output_directory",
           "Directory where to put analysis files (one for each analysis)"),
   analysis(true,1,Integer.MAX_VALUE,
           "analysis",
           "Analysis and filters to apply on signal [winmean winmedian winmax win90perc lowpass highpass unbias composite]"
              + "\nwinmean: windowed filter using window average value"
              + "\nwinmedian: windowed filter using window median value"
              + "\nwinmax: windowed filter using window maximum value"
              + "\nwin90perc: windowed filter using window ninetieth percentile value"
              + "\nlowpass: lowpass frequency filter"
              + "\nhighpass: highpass frequency filter"
              + "\nunbias: frequency filter which reduce each pulse using signal mooving average value"
              + "\ncomposite: combine unbian(highpass(lowpass))"
              + "\nAt least one analysis has to be specified"),
   
   lowerBound(false,1,1,
           "lowe_bound",
           "Consider only values with time/position >= lowerBound"),
   upperBound(false,1,1,
           "upper_bound",
           "Consider only values with time/position <= lowerBound"),
   
   stats(false,0,0,
           " ",
           "Show some statistics about the input signal and exit"),
   
   skipHeader(false,0,0,
           " ",
           "Skip header from ALL input csv");
   
   private boolean mandatory = false;
   private int maxNumOfArguments = 0;
   private int minNumOfArguments = 0;
   private String argumentName = "";
   private String description = "";
   

   CommandLineOption(boolean mandatory, int minArgs, int maxArgs, String argName, String description){
      this.mandatory = mandatory;
      this.maxNumOfArguments = maxArgs;
      this.minNumOfArguments = minArgs;
      this.argumentName = argName;
      this.description = description;
   }

   public boolean isMandatory() {
      return mandatory;
   }

   public int getMaxNumOfArguments() {
      return maxNumOfArguments;
   }

   public int getMinNumOfArguments() {
      return minNumOfArguments;
   }

   public String getArgumentName() {
      return argumentName;
   }

   public String getDescription() {
      return description;
   }
   
   public boolean hasArguments(){
      return minNumOfArguments > 0;
   }
   
}
