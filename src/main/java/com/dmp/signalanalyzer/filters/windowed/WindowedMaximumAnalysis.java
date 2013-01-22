package com.dmp.signalanalyzer.filters.windowed;

import com.dmp.signalanalyzer.signal.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class WindowedMaximumAnalysis extends WindowedSignalFilter {

   public double getSingleWindowValue(Signal window) {
      double maximum = 0;

      for (Signal pulse : window){
         if (pulse.getValue() > maximum) {
            maximum = pulse.getValue();
         }
      }
      return maximum;
   }
}
