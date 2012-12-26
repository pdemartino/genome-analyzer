package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.signal.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class CompositePositionAdapted extends SignalFilter{
    
    public Signal filter(Signal signal) {
        PositionAdaptedFilter positionAdaptedFilter = new PositionAdaptedFilter();
        HighPassFilter highPassFilter = new HighPassFilter();
        UnbiasFilter unbiasFilter = new UnbiasFilter();
        
        return unbiasFilter.filter(
                highPassFilter.filter(
                positionAdaptedFilter.filter(signal)));
                
    }
}
