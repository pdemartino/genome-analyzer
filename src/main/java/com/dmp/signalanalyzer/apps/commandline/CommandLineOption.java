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
   + "positions",
   null),
   
   signal(true, 1, 1,
          "signal_filename[:column]",
           "File containing signal values as a column (use '.' (dot) as decimal separator)",
           null),
   
   recombinationMap(false, 1, 1,
          "recombination-map_filename:position:genetic-map",
           "File containing mapping between physical and genetic positions",
           null),
   
   outputDirectory(false,1,1,
           "output_directory",
           "Directory where to put analysis files (one for each analysis)",
           "outputDirectory"),
   
   analysis(true,1,Integer.MAX_VALUE,
           "analysis",
           "Analysis and filters to apply on signal [winmean winmedian winmax win90perc lowpass highpass unbias composite]"
           +"\nYou can run several analysis on the same input signal separating them with commas (eg: lowpass,unbias)"
           +"\nYou can apply two or more analysis in chain separating them with a minus (eg: lowpass-unbias will apply "
           + "lowpass filter on the input signal and the unbias filter on the lowpass output"
              + "\nwinmean: windowed filter using window average value"
              + "\nwinmedian: windowed filter using window median value"
              + "\nwinmax: windowed filter using window maximum value"
              + "\nwin90perc: windowed filter using window ninetieth percentile value"
              + "\nlowpass: lowpass frequency filter"
              + "\nhighpass: highpass frequency filter"
              + "\nunbias: frequency filter which reduce each pulse using signal mooving average value"
              + "\ncomposite: combine unbian(highpass(lowpass))"
              + "\nAt least one analysis has to be specified",
           null),
   
  
   lowerBound(false,1,1,
           "lowe_bound",
           "Consider only values with time/position >= lowerBound",
           null),
   
   upperBound(false,1,1,
           "upper_bound",
           "Consider only values with time/position <= lowerBound",
           null),
   
   skipInputHeader(false,0,0,
           " ",
           "Skip header from ALL input csv",
           "skipInputHeader"),
   
   smoothingFactor(false,1,1,
           "smoothingFactor",
           "low pass analysis smoothing factor (float)",
           "smoothingFactor");
   
   private boolean mandatory = false;
   private int maxNumOfArguments = 0;
   private int minNumOfArguments = 0;
   private String argumentName = "";
   private String description = "";
   private String propertyToOverride = null;
   

   CommandLineOption(boolean mandatory, int minArgs, int maxArgs, String argName, String description, String propertyToOverride){
      this.mandatory = mandatory;
      this.maxNumOfArguments = maxArgs;
      this.minNumOfArguments = minArgs;
      this.argumentName = argName;
      this.description = description;
      this.propertyToOverride = propertyToOverride;
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
