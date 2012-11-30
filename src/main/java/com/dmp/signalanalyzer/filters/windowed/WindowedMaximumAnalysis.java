package com.dmp.signalanalyzer.filters.windowed;

import com.dmp.signalanalyzer.beans.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class WindowedMaximumAnalysis extends WindowedSignalFilter {

   public float getSingleWindowValue(Signal window) {
      float maximum = 0f;

      for (Signal pulse : window.getPulses()){
         if (pulse.getValue() > maximum) {
            maximum = pulse.getValue();
         }
      }
      return maximum / (window.getTStop() - window.getTStart());
   }
}
