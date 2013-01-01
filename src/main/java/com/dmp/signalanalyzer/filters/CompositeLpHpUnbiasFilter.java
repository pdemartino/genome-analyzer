package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.signal.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class CompositeLpHpUnbiasFilter extends SignalFilter {

   public Signal filter(Signal signal) {
      LowPass lowPassFilter = new LowPass();
      lowPassFilter.setFilterConfiguration(this.filterConfiguration);
      HighPassFilter highPassFilter = new HighPassFilter();
      highPassFilter.setFilterConfiguration(this.filterConfiguration);
      UnbiasFilter unbiasFilter = new UnbiasFilter();
      unbiasFilter.setFilterConfiguration(this.filterConfiguration);

      Signal lowPass = lowPassFilter.filter(signal);
      Signal highPass = highPassFilter.filter(lowPass);
      Signal unBias = unbiasFilter.filter(highPass);
              
      return unBias;
   }
}
