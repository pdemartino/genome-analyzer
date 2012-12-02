package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.signal.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 * =alpha*f[i-1]+beta*(s[i]-s[i-1])
 */
public class HighPassFilter extends SignalFilter{
   private float alpha = 1f;
   private float beta = 4f;

   
   public Signal filter(Signal inputSignal) {
      Signal filteredSignal = new Signal();

      Signal firstItem = inputSignal.get(0);
      Signal previousFiltered = new Signal(firstItem.getTime(), firstItem.getValue());
      // I do not have to add first filtered into out signal cause it will be done
      // on first iteration
      
      Signal previousPulse = firstItem;
      for (Signal pulse : inputSignal.getPulses()){
          float filteredValue = (alpha * previousFiltered.getValue()) +
                  (beta * (pulse.getValue() - previousPulse.getValue()));
          
          previousPulse = pulse;
          previousFiltered = new Signal(pulse.getTime(), filteredValue);
          filteredSignal.addPulse(previousFiltered);
      }
      
      return filteredSignal;
   }

}
