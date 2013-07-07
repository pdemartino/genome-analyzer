package com.dmp.signalanalyzer.filters.smoothing;

import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.signal.Signal;
import umontreal.iro.lecuyer.functionfit.SmoothingCubicSpline;


/**
 *
 * @author pdemartino
 */
public class CubicSplineSmoothing extends SignalFilter{
   static double rho = 0.0;
   
   @Override
   public Signal filter(Signal signal) {
      Signal filtered = new Signal();
      
      double[] x = signal.getX();
      double[] y = signal.getY();
      SmoothingCubicSpline smoother = new SmoothingCubicSpline(x, y, rho);
      
      for (double x_i : x){
         filtered.addComponent(new Signal(x_i, smoother.evaluate(x_i)));
      }
      
      return filtered;
   }
   
}
