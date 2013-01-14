package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.signal.Signal;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class TopFivePercent extends SignalFilter {

    
    public static Signal staticFilter(Signal signal) {
        return TopFivePercent.topFivePercent(signal);
    }

    private static Signal topFivePercent(Signal signal) {
        // cloneSignal
        Signal reverseValueOrderSignal = signal.cloneAtLevel(1); // clone root and first level
        int fivePercentCount = (int) Math.ceil((float) reverseValueOrderSignal.numberOfComponents() / 20);
        List<Signal> windows = reverseValueOrderSignal.toList();
        
        Collections.sort(windows,Collections.reverseOrder(Signal.comparatorByValue()));
        
        int i = 1;
        for (Signal window : windows){
            if (i <= fivePercentCount){
                window.setValue(1); // Selected
            }else{
                window.setValue(0); // Not Selected
            }
            i++;
        }
        
        return reverseValueOrderSignal;
    }

    @Override
    public Signal filter(Signal signal) {
        return TopFivePercent.staticFilter(signal);
    }
}
