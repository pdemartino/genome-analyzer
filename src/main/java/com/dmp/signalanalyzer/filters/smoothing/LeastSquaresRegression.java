package com.dmp.signalanalyzer.filters.smoothing;

import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.signal.Signal;
import org.apache.commons.math.stat.regression.SimpleRegression;

/**
 *
 * @author pdemartino
 */
public class LeastSquaresRegression extends SignalFilter{
   SimpleRegression regression = new SimpleRegression();
   
   @Override
   public Signal filter(Signal signal) {
      Signal filtered = new Signal();
      regression.clear();
      
      for (Signal component : signal){
         regression.addData(component.getTime(), component.getValue());
      }
      
      for (Signal component : signal){
         double x = component.getTime();
         filtered.addComponent(new Signal(x, regression.predict(x)));
      }
      
      return filtered;
   }
   
}
