package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.signal.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class CompositeFilter extends SignalFilter {

   public Signal filter(Signal signal) {
      LowPass lowPassFilter = new LowPass();
      lowPassFilter.setFilterConfiguration(this.filterConfiguration);
      UnbiasFilter unbiasFilter = new UnbiasFilter();
      unbiasFilter.setFilterConfiguration(this.filterConfiguration);

      Signal lowPass = lowPassFilter.filter(signal);
      Signal unBias = unbiasFilter.filter(lowPass);
              
      return unBias;
   }
}
