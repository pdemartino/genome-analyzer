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
    
      float winSize = filterConfiguration.get("window")!=null 
              ? ((Float)filterConfiguration.get("window")).floatValue()
              : (inputSignal.getTStop() + inputSignal.getTStart()) / 2 * SignalAnalyzerConstants.WINDOW_MULT;
     
      float stSize = filterConfiguration.get("step")!=null 
              ? ((Float)filterConfiguration.get("step")).floatValue()
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
