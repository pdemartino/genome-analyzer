package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.signal.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com> Apply a low pass filter to
 * the input signal Each pulse p[i] is translated as lp(i-1) + (p[i] - lp(i-1))
 * / alfa
 */
public class LowPassFilter extends SignalFilter {

   float smoothingFactor = 200f;

   public Signal filter(Signal inputSignal) {
      Signal filteredSignal = new Signal();

      Signal firstItem = inputSignal.get(0);
      Signal prevFiltered = new Signal(firstItem.getTime(), firstItem.getValue());
      filteredSignal.addPulse(prevFiltered);

      float filteredValue;
      for (Signal pTmp : inputSignal.getPulses()) {
         // skip first item
         if (pTmp != firstItem) {

            filteredValue = prevFiltered.getValue()
                    + (pTmp.getValue() - prevFiltered.getValue()) / smoothingFactor;

            prevFiltered = new Signal(pTmp.getTime(), filteredValue);
            filteredSignal.addPulse(prevFiltered);
         }
      }

      return filteredSignal;
   }
}
