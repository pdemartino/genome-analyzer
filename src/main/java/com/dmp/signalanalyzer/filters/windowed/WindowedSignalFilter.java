package com.dmp.signalanalyzer.filters.windowed;

import com.dmp.signalanalyzer.configuration.ConfigurationManager;
import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.signal.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public abstract class WindowedSignalFilter extends SignalFilter {
   public Signal filter(Signal inputSignal) {
      Signal windowedSignal = generateWindowedSignal(inputSignal);
      float winValue;
      for (Signal window : windowedSignal){
         winValue = getSingleWindowValue(window);
         if (Float.isNaN(winValue)) {
            winValue = 0f;
         }
         window.setValue(winValue);
      }
      return windowedSignal;
   }
   
   

   private Signal generateWindowedSignal(Signal inputSignal) {
      ConfigurationManager configurationManager = ConfigurationManager.getInstance();
      Signal windowedSignal = new Signal();
    
      int winSize = filterConfiguration.get("window")!=null 
              ? ((Integer)filterConfiguration.get("window")).intValue()
              : (int)((inputSignal.getTStop() + inputSignal.getTStart()) / 2 * configurationManager.getWindowsMultiplier());
     
      int stSize = filterConfiguration.get("step")!=null 
              ? ((Integer)filterConfiguration.get("step")).intValue()
              : (int) (winSize * configurationManager.getStepMultiplier());
      
      // Create Windows
      int start = inputSignal.getTStart();
      int stop;
      while (start <= inputSignal.getTStop()) {
         stop = Math.min(start + winSize, inputSignal.getTStop());
         Signal window = new Signal(start, stop, 0f);
         windowedSignal.addPulse(window);
         start = start + stSize;
      }

      // Fill Windows
      boolean inserted;
      for (Signal pulse : inputSignal) {
         inserted = false;
         for (Signal window : windowedSignal) {
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
