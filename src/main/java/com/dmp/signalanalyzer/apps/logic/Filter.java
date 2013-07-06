package com.dmp.signalanalyzer.apps.logic;

import com.dmp.signalanalyzer.filters.LowPass;
import com.dmp.signalanalyzer.filters.NinetiethPercentSelector;
import com.dmp.signalanalyzer.filters.UnbiasFilter;
import com.dmp.signalanalyzer.filters.MovingAvgFitting;
import com.dmp.signalanalyzer.filters.NeutralFilter;
import com.dmp.signalanalyzer.filters.windowed.WindowedNinetiethPercentileAnalysis;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public enum Filter {

   lowpass(LowPass.class),
   unbias(UnbiasFilter.class),
   movingAvgFitting(MovingAvgFitting.class),
   ninetiethPercSelector(NinetiethPercentSelector.class),
   neutral(NeutralFilter.class),
   winNinetiethPerc(WindowedNinetiethPercentileAnalysis.class);
   
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
