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
      float[] minMaxLociSize = getMinMaxLociCount(windowedSignal);
      float[] minMaxComponentsSize = getMinMaxComponentsCount(windowedSignal);
      float[] minMaxComponentsDistance = getMinMaxComponentsDistance(windowedSignal);

      float winValue;
      for (Signal window : windowedSignal) {
         winValue = getSingleWindowValue(window);
         winValue = windowGeneticAdjustment(winValue, window,
                 minMaxComponentsDistance, minMaxComponentsSize, minMaxLociSize);
         if (Float.isNaN(winValue)) {
            winValue = 0f;
         }
         window.setValue(winValue);
      }
      return windowedSignal.flat();
   }

   private Signal generateWindowedSignal(Signal inputSignal) {
      ConfigurationManager configurationManager = ConfigurationManager.getInstance();
      Signal windowedSignal = new Signal();

      
      int winSize = filterConfiguration.get("window") != null
              ? ((Float) filterConfiguration.get("window")).intValue()
              : (int) ((inputSignal.getTStop() + inputSignal.getTStart()) / 2 * configurationManager.getWindowsMultiplier());

      int stSize = filterConfiguration.get("step") != null
              ? ((Float) filterConfiguration.get("step")).intValue()
              : (int) (winSize * configurationManager.getStepMultiplier());

      // Create Windows
      int start = inputSignal.getTStart();
      int stop;
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

   private static float[] getMinMaxComponentsCount(Signal signal) {
      float[] minMax = {Float.MAX_VALUE, -1 * Float.MAX_VALUE};
      for (Signal window : signal) {
         minMax[0] = Math.min(window.count(), minMax[0]);
         minMax[1] = Math.max(window.count(), minMax[1]);
      }
      return minMax;
   }

   private static float[] getMinMaxLociCount(Signal signal) {
      float[] minMax = {Float.MAX_VALUE, -1 * Float.MAX_VALUE};
      for (Signal window : signal) {
         float lociCount = window.getTStop() - window.getTStart();
         minMax[0] = Math.min(lociCount, minMax[0]);
         minMax[1] = Math.max(lociCount, minMax[1]);
      }
      return minMax;
   }

   private static float[] getMinMaxComponentsDistance(Signal signal) {
      float[] minMax = {Float.MAX_VALUE, -1 * Float.MAX_VALUE};

      for (Signal window : signal) {
         float[] windowDistances = window.distances();
         minMax[0] = Math.min(SAMath.min(windowDistances), minMax[0]);
         minMax[1] = Math.max(SAMath.max(windowDistances), minMax[1]);
      }
      return minMax;
   }

   private static float windowGeneticAdjustment(float winValue, Signal window,
           float[] mMCompDistance, float[] mMCompCount, float[] mMLociCount) {

      // the more the window is large, the less you have to consider the value
      float compDistanceNorm =
              winValue
              * SAMath.minMaxNormalization(
              SAMath.average(window.distances()), mMCompDistance[0], mMCompDistance[1]);

      // the more snps the window has, the more you have to consider the value
      float compCountNorm =
              winValue
              * SAMath.minMaxNormalization(
              window.count(), mMCompCount[0], mMCompCount[1]);

      // the more is the inter-snp distance, the less you have to consider the value
      float lociCountNorm =
              winValue
              * SAMath.minMaxNormalization(
              window.getTStop() - window.getTStart(), mMLociCount[0], mMLociCount[1]);

      return SAMath.average(compDistanceNorm, compCountNorm);
   }

   public abstract float getSingleWindowValue(Signal window);
}
