/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.signalanalyzer.signal;

import org.apache.commons.math.stat.descriptive.rank.Percentile;

/**
 *
 * @author Paco
 */
public class SignalStats {
   Percentile percentile = new Percentile();
   double start,stop,rangeLen;
   double minValue, maxValue, meanValue;
   double medianValue, ninetiethPercValue;
   double minDistance, maxDistance, meanDistance;
   int numberOfItems;
   double minGTZero;
   private double medianDistance;
   private double ninetiethPercDistance;

   public SignalStats(Signal signal) {
      computeRangeStats(signal);
      computeValueStats(signal);
      computeDistanceStats(signal);
      numberOfItems = signal.count();
   }

   private void computeValueStats(Signal signal) {
      double minMethodValue = Double.MAX_VALUE, maxMethodValue = -1 * Double.MAX_VALUE, sumMethodValue = 0f;
      double minMethodGTZero = minMethodValue;

      for (Signal pulse : signal) {
         minMethodValue = Math.min(pulse.getValue(), minMethodValue);
         if (minMethodValue > 0){
            minMethodGTZero = minMethodValue;
         }
         maxMethodValue = Math.max(pulse.getValue(), maxMethodValue);
         sumMethodValue += pulse.getValue();
      }
      
      double[] dArray = signal.toDoubleValuesArray();
      percentile.setQuantile(50);
      this.medianValue = percentile.evaluate(dArray);
      
      percentile.setQuantile(90);
      this.ninetiethPercValue = percentile.evaluate(dArray);

      this.minValue = minMethodValue;
      this.maxValue = maxMethodValue;
      this.minGTZero = minMethodGTZero;
      this.meanValue = sumMethodValue / signal.count();
   }

   private void computeDistanceStats(Signal signal) {
      double minMethodDistance = Double.MAX_VALUE, maxMethodDistance = -1 * Double.MAX_VALUE, sumMethodDistance = 0f;
      
      // Use an array to keep track of distances, needed to easly compute
      // median and 90perc
      int i = 0;
      double[] distances = new double[signal.count()];
      Signal previous = signal.firstEntry();
      for (Signal pulse : signal) {
         if (pulse != previous) {
            double distance = Math.abs(pulse.getTime() - previous.getTime());
            distances[i++] = distance;
            minMethodDistance = Math.min(distance, minMethodDistance);
            maxMethodDistance = Math.max(distance, maxMethodDistance);
            sumMethodDistance += distance;
            previous = pulse;
         }
      }

      Percentile percentile = new Percentile();
      percentile.setQuantile(50);
      this.medianDistance = percentile.evaluate(distances);
      percentile.setQuantile(90);
      this.ninetiethPercDistance = percentile.evaluate(distances);
      
      
      this.minDistance = minMethodDistance;
      this.maxDistance = maxMethodDistance;
      // do not consider the first item of the signal cause of it has no
      // previous item from which to count the distance
      this.meanDistance = sumMethodDistance / (signal.count() - 1);
   }

   @Override
   public String toString() {
      String outString = "{\n";
      outString += String.format("\tRange:{start:%s,stop:%s,length:%s},\n", start,stop, rangeLen);
      outString += String.format("\tValue:{mean:%s,max:%s,min:%s,min>0:%s,median:%s,90perc:%s},\n", meanValue, maxValue, minValue,minGTZero, medianValue,ninetiethPercValue);
      outString += String.format("\tDistance:{mean:%s,max:%s,min:%s,median:%s,90perc:%s},\n", meanDistance, maxDistance, minDistance,medianDistance, ninetiethPercDistance);
      outString += String.format("\tItems:%s\n", numberOfItems);
      outString += "}";
      return outString;
   }

   private void computeRangeStats(Signal signal) {
      this.start = signal.getTStart();
      this.stop = signal.getTStop();
      this.rangeLen = stop - start;
   }
}
