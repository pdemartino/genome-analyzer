package com.dmp.signalanalyzer.apps.commandline;

import com.dmp.signalanalyzer.apps.logic.Filter;

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
   "map_file:hisical_col:genetic_col",
   "File containing mapping between physical and genetic positions",
   null),
   outputDirectory(false, 1, 1,
   "output_directory",
   "Directory where to put analysis files (one for each analysis)",
   "outputDirectory"),
   analysis(true, 1, Integer.MAX_VALUE,
   "analysis",
   String.format(
   "Analysis and filters to apply on signal: {\n%s}"
   + "\n\tYou can run several analysis on the same input signal separating them with "
   + "%s (eg: %s%s%s)"
   + "\n\tYou can apply two or more analysis in chain separating them with %s "
   + "(eg: %s%s%s will apply %s on the input signal and the %s filter on the %s output", Filter.implodeValues("\n"),
   ConfigurationManager.multipleValuesSeparator, Filter.values()[0], ConfigurationManager.multipleValuesSeparator, Filter.values()[1],
   Filter.CHAIN_SEPARATOR, Filter.values()[0], Filter.CHAIN_SEPARATOR, Filter.values()[1],
   Filter.values()[0], Filter.values()[1], Filter.values()[0]),
   null),
   lowerBound(false, 1, 1,
   "lower_bound",
   "Consider only values with time/position >= lowerBound",
   null),
   upperBound(false, 1, 1,
   "upper_bound",
   "Consider only values with time/position <= lowerBound",
   null),
   skipInputHeader(false, 0, 0,
   " ",
   "Skip header from ALL input csv",
   "skipInputHeader"),
   inputFileSeparator(false, 1, 1,
   "input_file_separator",
   "String used to separate columns inside input file",
   "inputFileSeparator"),
   window(false, 1, 1,
   "sliding_window_size",
   "Sliding window size as percentage of the whole signal size",
   "window");
 
   
   private boolean mandatory = false;
   private int maxNumOfArguments = 0;
   private int minNumOfArguments = 0;
   private String argumentName = "";
   private String description = "";
   private String propertyToOverride = null;

   CommandLineOption(boolean mandatory, int minArgs, int maxArgs, String argName, String description, String propertyToOverride) {
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

   public boolean hasArguments() {
      return minNumOfArguments > 0;
   }

   public boolean hasPropertyToOverride() {
      return this.propertyToOverride != null && !this.propertyToOverride.isEmpty();
   }

   public String getPropertyToOverride() {
      if (hasPropertyToOverride()) {
         return this.propertyToOverride;
      }
      return null;
   }
}
