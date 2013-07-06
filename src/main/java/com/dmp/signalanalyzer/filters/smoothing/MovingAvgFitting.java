package com.dmp.signalanalyzer.filters.smoothing;

import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.signal.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class MovingAvgFitting extends SignalFilter {

    public Signal filter(Signal inputSignal) {
        Signal analysis = new Signal();
        double prevAvg = 0;
        int index = 0;
        for (Signal pulse : inputSignal) {
            prevAvg = moovingAverage(prevAvg, index, pulse.getValue());
            analysis.addComponent(new Signal(pulse.getTime(), prevAvg));
            index++;
        }
        
        return analysis;
    }

    private double moovingAverage(double prevAvg, int index, double value) {
        // E4*VALUE(A5-1)/A5+D5*VALUE(1/A5)
        double movAvg = value;

        if (index > 0) {
            movAvg =
                    (prevAvg * (index - 1) / index)
                    + (value / index);
        }
        return movAvg;
    }
}
