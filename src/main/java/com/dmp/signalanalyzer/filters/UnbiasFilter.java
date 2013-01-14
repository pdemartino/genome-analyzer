package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.signal.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class UnbiasFilter extends SignalFilter {

    public Signal filter(Signal inputSignal) {
        Signal analysis = new Signal();
        float prevAvg = 0;
        int index = 0;
        for (Signal pulse : inputSignal) {
            prevAvg = moovingAverage(prevAvg, index, pulse.getValue());
            analysis.addComponent(new Signal(pulse.getTime(), 
                    pulse.getValue() - prevAvg));
            index++;
        }
        
        return analysis;
    }

    private float moovingAverage(float prevAvg, int index, float value) {
        // E4*VALUE(A5-1)/A5+D5*VALUE(1/A5)
        float movAvg = value;

        if (index > 0) {
            movAvg =
                    (prevAvg * (index - 1) / index)
                    + (value / index);
        }
        return movAvg;
    }
}
