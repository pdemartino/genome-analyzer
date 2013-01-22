package com.dmp.signalanalyzer.filters.windowed;

import com.dmp.signalanalyzer.configuration.ConfigurationManager;
import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.signal.Signal;
import com.dmp.signalanalyzer.utils.SAMath;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public abstract class WindowedSignalFilter extends SignalFilter {

   public Signal filter(Signal inputSignal) {
      Signal windowedSignal = generateWindowedSignal(inputSignal);
      // get statistics
      double[] minMaxLociSize = getMinMaxLociCount(windowedSignal);
      double[] minMaxComponentsSize = getMinMaxComponentsCount(windowedSignal);
      double[] minMaxComponentsDistance = getMinMaxComponentsDistance(windowedSignal);

      double winValue;
      for (Signal window : windowedSignal) {
         winValue = getSingleWindowValue(window);
         winValue = windowGeneticAdjustment(winValue, window,
                 minMaxComponentsDistance, minMaxComponentsSize, minMaxLociSize);
         if (Double.isNaN(winValue)) {
            winValue = 0f;
         }
         window.setValue(winValue);
      }
      return windowedSignal.flat();
   }

   private Signal generateWindowedSignal(Signal inputSignal) {
      ConfigurationManager configurationManager = ConfigurationManager.getInstance();
      Signal windowedSignal = new Signal();

      
      double winSize = filterConfiguration.get("window") != null
              ? ((Double) filterConfiguration.get("window")).intValue()
              : (double) ((inputSignal.getTStop() + inputSignal.getTStart()) / 2 * configurationManager.getWindowsMultiplier());

      double stSize = filterConfiguration.get("step") != null
              ? ((Double) filterConfiguration.get("step")).intValue()
              : (double) (winSize * configurationManager.getStepMultiplier());

      // Create Windows
      double start = inputSignal.getTStart();
      double stop;
      while (start <= inputSignal.getTStop()) {
         stop = Math.min(start + winSize, inputSignal.getTStop());
         Signal window = new Signal(start, stop, 0f);
         windowedSignal.addComponent(window);
         start = start + stSize;
      }

      // Fill Windows
      boolean inserted;
      for (Signal pulse : inputSignal) {
         inserted = false;
         for (Signal window : windowedSignal) {
            inserted = window.addComponentIfCanContain(pulse);
            if (inserted) {
               break;
            }
         }
      }

      return windowedSignal;
   }

   private static double[] getMinMaxComponentsCount(Signal signal) {
      double[] minMax = {Double.MAX_VALUE, -1 * Double.MAX_VALUE};
      for (Signal window : signal) {
         minMax[0] = Math.min(window.count(), minMax[0]);
         minMax[1] = Math.max(window.count(), minMax[1]);
      }
      return minMax;
   }

   private static double[] getMinMaxLociCount(Signal signal) {
      double[] minMax = {Double.MAX_VALUE, -1 * Double.MAX_VALUE};
      for (Signal window : signal) {
         double lociCount = window.getTStop() - window.getTStart();
         minMax[0] = Math.min(lociCount, minMax[0]);
         minMax[1] = Math.max(lociCount, minMax[1]);
      }
      return minMax;
   }

   private static double[] getMinMaxComponentsDistance(Signal signal) {
      double[] minMax = {Double.MAX_VALUE, -1 * Double.MAX_VALUE};

      for (Signal window : signal) {
         double[] windowDistances = window.distances();
         minMax[0] = Math.min(SAMath.min(windowDistances), minMax[0]);
         minMax[1] = Math.max(SAMath.max(windowDistances), minMax[1]);
      }
      return minMax;
   }

   private static double windowGeneticAdjustment(double winValue, Signal window,
           double[] mMCompDistance, double[] mMCompCount, double[] mMLociCount) {

      // the more the window is large, the less you have to consider the value
      double compDistanceNorm =
              winValue
              * SAMath.minMaxNormalization(
              SAMath.average(window.distances()), mMCompDistance[0], mMCompDistance[1]);

      // the more snps the window has, the more you have to consider the value
      double compCountNorm =
              winValue
              * SAMath.minMaxNormalization(
              window.count(), mMCompCount[0], mMCompCount[1]);

      // the more is the inter-snp distance, the less you have to consider the value
      double lociCountNorm =
              winValue
              * SAMath.minMaxNormalization(
              window.getTStop() - window.getTStart(), mMLociCount[0], mMLociCount[1]);

      return SAMath.average(compDistanceNorm, compCountNorm);
   }

   public abstract double getSingleWindowValue(Signal window);
}
