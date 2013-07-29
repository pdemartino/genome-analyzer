/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.signalanalyzer.filters.smoothing;

import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.signal.Signal;
import com.dmp.signalanalyzer.utils.SAMath;

/**
 *
 * @author pdemartino
 */
public class HaplotypeDelimiter extends SignalFilter{

   @Override
   public Signal filter(Signal signal) {
      // For each null (zero) point, if the distance from the previous one is 
      // less then the average then change the value from zero to the average of 
      // the two points
      
      // Stage 1: fill holes
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
      
      // Stage 2: define haplotypes
      Signal haplotypes = new Signal();
      Signal haplotype = null;
      double maxValue = 0.0;
      for (Signal snp : filled){
         if (snp.getValue() > 0.0){
            if (haplotype == null){
               haplotype = new Signal();
               haplotypes.addComponent(haplotype);
            }
            // haplotype start and stop are automatically 
            // updated
            haplotype.addComponent(snp);
            maxValue = Math.max(maxValue, snp.getValue());
         }else{
            if (haplotype != null){
               haplotype.setValue(maxValue);
               haplotype = null;
               maxValue = 0.0;
            }
         }
      }
              
      return haplotypes;
   }
   
   private static double getDistanceLimit(Signal signal){
      // get distances array
      double[] distances = new double[signal.count() - 1];
      
      double[] positions = signal.getX();
      for (int i = 1; i < positions.length; i++){
         distances[i-1] = positions[i] -  positions[i-1];
      }
      
      return SAMath.average(distances);
   }
   
}
