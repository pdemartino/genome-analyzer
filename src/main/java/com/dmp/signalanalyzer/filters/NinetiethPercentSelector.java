package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.signal.Signal;
import org.apache.commons.math.stat.descriptive.rank.Percentile;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class NinetiethPercentSelector extends SignalFilter {

   private static final double quantile = 90.;
   private static Percentile percentile = new Percentile();

   @Override
   public Signal filter(Signal signal) {
      Signal filteredSignal = new Signal();
      // 1 compute 90°perc 
      percentile.setQuantile(quantile);
      float percentileVal = (float) percentile.evaluate(signal.toDoubleValuesArray());
      
      // 2 select pulses >= 90° Perc
      float filteredValue = 0; //{0,1}
      for (Signal pulse : signal){
         filteredValue = pulse.getValue() >= percentileVal ? pulse.getValue() : 0;
         filteredSignal.addComponent(new Signal(pulse.getTStart(),pulse.getTStop(), filteredValue));
      }
      
      return filteredSignal;
   }
}
