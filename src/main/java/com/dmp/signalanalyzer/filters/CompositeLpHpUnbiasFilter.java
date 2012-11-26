package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.beans.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class CompositeLpHpUnbiasFilter extends SignalFilter{
    
    public Signal filter(Signal signal) {
        LowPassFilter lowPassFilter = new LowPassFilter();
        HighPassFilter highPassFilter = new HighPassFilter();
        UnbiasFilter unbiasFilter = new UnbiasFilter();
        
        return unbiasFilter.filter(
                highPassFilter.filter(
                lowPassFilter.filter(signal)));
                
    }
}
