package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.beans.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 * =alpha*f[i-1]+beta*(s[i]-s[i-1])
 */
public class HighPassFilter extends SignalFilter{
   private float alpha = 1f;
   private float beta = 4f;

   
   public Signal filter(Signal signal) {
      Signal filteredSignal = new Signal();

      Signal firstItem = signal.get(0);
      Signal previousFiltered = new Signal(firstItem.getTime(), firstItem.getValue());
      filteredSignal.addPulse(previousFiltered);
      
      for (int i = 1; i < signal.size(); i++){
          float filteredValue = (alpha * previousFiltered.getValue()) +
                  (beta * (signal.get(i).getValue() - signal.get(i-1).getValue()));
          
          previousFiltered = new Signal(signal.get(i).getTime(), filteredValue);
          filteredSignal.addPulse(previousFiltered);
      }
      
      return filteredSignal;
   }

}
