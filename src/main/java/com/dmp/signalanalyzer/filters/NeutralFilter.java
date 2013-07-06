package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.signal.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 * This filter does... NOTHING!
 */
public class NeutralFilter extends SignalFilter{
   
   public Signal filter(Signal inputSignal) {
      return inputSignal;
   }

}
