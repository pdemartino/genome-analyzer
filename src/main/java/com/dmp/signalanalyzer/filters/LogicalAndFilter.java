package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.signal.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class LogicalAndFilter extends SignalFilter{
   private Signal maskSignal;

   @Override
   public Signal filter(Signal signal) {
      
      if (maskSignal == null){
         return signal;
      }
      
      Signal outSignal = new Signal();
      for (Signal component : signal){
         Signal maskComponent = maskSignal.get(component.getTime());
         if (maskComponent == null || maskComponent.getValue() <= 0){
            outSignal.addComponent(new Signal(component.getTime(), 0.));
         }else{
            outSignal.addComponent(new Signal(component.getTime(), component.getValue()));
         }
      }
      return outSignal;
   }
   
   public void setMaskSignal(Signal maskSignal){
      
   }
   
}
