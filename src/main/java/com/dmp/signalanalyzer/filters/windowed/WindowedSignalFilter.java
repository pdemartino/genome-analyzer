package com.dmp.signalanalyzer.filters.windowed;

import com.dmp.signalanalyzer.signal.Signal;
import com.dmp.signalanalyzer.filters.FilterConfiguration;
import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.utils.SignalAnalyzerConstants;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public abstract class WindowedSignalFilter extends SignalFilter {
   private float stepSize = -1f, windowSize = -1f;
    
  
   public Signal filter(Signal inputSignal) {
      Signal windowedSignal = generateWindowedSignal(inputSignal);
      float winValue;
      for (Signal window : windowedSignal.getPulses()){
         winValue = getSingleWindowValue(window);
         if (Float.isNaN(winValue)) {
            winValue = 0f;
         }
         window.setValue(winValue);
      }
      return windowedSignal;
   }

   private Signal generateWindowedSignal(Signal inputSignal) {
      Signal windowedSignal = new Signal();
    
      float winSize = this.windowSize > 0 ? this.windowSize 
              : (inputSignal.getTStop() + inputSignal.getTStart()) / 2 * SignalAnalyzerConstants.WINDOW_MULT;
     
      float stSize = this.stepSize > 0 ? this.stepSize 
              : winSize * SignalAnalyzerConstants.STEP_MULT;
      
      // Create Windows
      float start = inputSignal.getTStart();
      float stop;
      while (start <= inputSignal.getTStop()) {
         stop = Math.min(start + winSize, inputSignal.getTStop());
         Signal window = new Signal(start, stop, 0f);
         windowedSignal.addPulse(window);
         start = start + stSize;
      }

      // Fill Windows
      boolean inserted;
      for (Signal pulse : inputSignal.getPulses()) {
         inserted = false;
         for (Signal window : windowedSignal.getPulses()) {
            inserted = window.addPulseIfCanContain(pulse);
            if (inserted) {
               break;
            }
         }
      }
      
      return windowedSignal;
   }

   
   

   public abstract float getSingleWindowValue(Signal window);
  
}
