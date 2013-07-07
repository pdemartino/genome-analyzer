/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.signalanalyzer.filters.smoothing;

import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.signal.Signal;
import org.apache.commons.math.stat.descriptive.rank.Percentile;

/**
 *
 * @author pdemartino
 */
public class HoleFiller extends SignalFilter{
   private static Percentile percentile = new Percentile(5.);

   @Override
   public Signal filter(Signal signal) {
      // For each null (zero) point, if the distance from the prevois one is 
      // less then the 5%ile then change the value from zero to the average of 
      // the two points
      Signal filled = new Signal();
      
      double limit = getDistanceLimit(signal);
      Double prev_x = null;
      Double prev_y = null;
      for (Signal component : signal){
         double x = component.getTime();
         double y = component.getValue();
         if (prev_y != null && y <= 0){
            double distance = x - prev_x;
            if (distance <= limit){
               y = prev_y / 2;
            }
         }
         filled.addComponent(new Signal(x, y));
         prev_x = x;
         prev_y = y;
      }
              
      return filled;
   }
   
   private static double getDistanceLimit(Signal signal){
      // get distances array
      double[] distances = new double[signal.count() - 1];
      
      double[] positions = signal.getX();
      for (int i = 1; i < positions.length; i++){
         distances[i-1] = positions[i] -  positions[i-1];
      }
      
      return percentile.evaluate(distances);
   }
   
}
