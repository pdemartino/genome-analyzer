package com.dmp.signalanalyzer.apps.logic;

import com.dmp.signalanalyzer.filters.LowPass;
import com.dmp.signalanalyzer.filters.NeutralFilter;
import com.dmp.signalanalyzer.filters.NinetiethPercentSelector;
import com.dmp.signalanalyzer.filters.UnbiasFilter;
import com.dmp.signalanalyzer.filters.smoothing.HaplotypeDelimiter;
import com.dmp.signalanalyzer.filters.windowed.WindowedDensity;
import com.dmp.signalanalyzer.filters.windowed.WindowedMeanAnalysis;
import com.dmp.signalanalyzer.filters.windowed.WindowedMedianAnalysis;
import com.dmp.signalanalyzer.filters.windowed.WindowedNinetiethPercentileAnalysis;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public enum Filter {

   
   // smooting
   lowpass(LowPass.class),
   // Component selection
   unbias(UnbiasFilter.class),
   ninetiethPercSelector(NinetiethPercentSelector.class),
   
   // Regions selector
   holeFiller(HaplotypeDelimiter.class),
      
   //Windowed
   winNinetiethPerc(WindowedNinetiethPercentileAnalysis.class),
   winMean(WindowedMeanAnalysis.class),
   winMedian(WindowedMedianAnalysis.class),
   winDensity(WindowedDensity.class),
		   
   neutral(NeutralFilter.class);
   
   public static String CHAIN_SEPARATOR = "-";
   Class className;
   Filter(Class className) {
      this.className = className;
   }
   
   public static String implodeValues(String separator){
      String implode = "";
      for (Filter filter : Filter.values()){
         implode += filter.name() + separator;
      }
      return implode;
   }
}
