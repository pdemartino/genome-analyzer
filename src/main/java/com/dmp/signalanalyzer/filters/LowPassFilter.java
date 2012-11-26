package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.beans.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 * Apply a low pass filter to the input signal
 * Each pulse p[i] is translated as lp(i-1) + (p[i] - lp(i-1)) / alfa 
 */
public class LowPassFilter extends SignalFilter{
    float smoothingFactor = 200f;


    public Signal filter(Signal inputSignal) {
        Signal filteredSignal = new Signal();
        
        Signal firstItem = inputSignal.get(0);
        Signal prevFiltered = new Signal(firstItem.getTime(),firstItem.getValue());
        filteredSignal.addPulse(prevFiltered);
        
        for (int i = 1; i < inputSignal.size(); i++){
            Signal pTmp = inputSignal.get(i);

            float filteredValue = prevFiltered.getValue() + 
                    (pTmp.getValue() - prevFiltered.getValue())/ smoothingFactor;
            
            prevFiltered = new Signal(pTmp.getTime(), filteredValue);
            filteredSignal.addPulse(prevFiltered);
        }
        
        return filteredSignal;
    }

}
