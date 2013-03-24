package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.signal.Signal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com> Apply a low pass filter to
 * the input signal Each pulse p[i] is translated as lp(i-1) + (p[i] - lp(i-1))
 * / alfa
 */
public class LowPass extends SignalFilter {

   private static final Logger logger = Logger.getLogger(LowPass.class.getName());
   float smoothingFactor = 200f;
   boolean normalizeUsingPosition = false;
   boolean backward = false;
   boolean twoWay = false;

   public Signal filter(Signal inputSignal) {
      Signal filteredSignal;
      logger.debug(String.format("Filtering %s long signal...",inputSignal.count()));
      logger.debug(String.format("Using configuration: %s", filterConfiguration));
      
      refreshConfiguration();
      filteredSignal = innerFilter(inputSignal, backward);
      if (twoWay) {
         logger.debug("TwoWay mode, going reverse way...");
         Signal reverseFilteredSignal = innerFilter(inputSignal, !backward);
         filteredSignal = filteredSignal.intersect(reverseFilteredSignal);
      }

      if (normalizeUsingPosition) {
         filteredSignal = applyDistanceScalingFactor(filteredSignal);
      }
      
      return filteredSignal;
   }

   private Signal innerFilter(Signal inputSignal, boolean backward) {
      logger.debug(String.format("Generating filtered signal in %s mode",backward ? "backward" : "forward"));
      Signal filteredSignal = new Signal();

      List<Signal> signalList = inputSignal.toList();
      if (backward) {
         Collections.reverse(signalList);
      } 
      
      logger.debug(String.format("Signal has been converted into a %s long list",signalList.size()));

      Signal firstItem = signalList.get(0);
      Signal prevFiltered = new Signal(firstItem.getTime(), firstItem.getValue());
      filteredSignal.addComponent(prevFiltered);

      double filteredValue;
      for (Signal pTmp : signalList) {
         // skip first item
         if (pTmp != firstItem) {
            filteredValue = prevFiltered.getValue()
                    + (pTmp.getValue() - prevFiltered.getValue()) / smoothingFactor;

            prevFiltered = new Signal(pTmp.getTime(), filteredValue);
            filteredSignal.addComponent(prevFiltered);
         }
      }
      logger.debug(String.format("Generated a %s long filtered signal",filteredSignal.count()));

      return filteredSignal;
   }

   private static Map<String, Double> getDistanceFactors(Signal signal) {
      Map<String, Double> distanceFactors = new HashMap<String, Double>();

      double maxDistance = 0f;
      double minDistance = Double.MAX_VALUE;
      // retrieve max and min distance
      Signal previuos = signal.firstEntry();
      for (Signal component : signal) {
         double distance = component.getTime() - previuos.getTime();
         maxDistance = Math.max(maxDistance, distance);
         minDistance = Math.min(minDistance, distance);
         previuos = component;
      }
      distanceFactors.put("minDistance", minDistance);
      distanceFactors.put("maxDistance", maxDistance);
      distanceFactors.put("distanceRange", maxDistance - minDistance);

      return distanceFactors;
   }

   private void refreshConfiguration() {

      if (filterConfiguration.get("normalizeUsingPosition") != null) {
         normalizeUsingPosition = Boolean.valueOf((String)this.filterConfiguration.get("normalizeUsingPosition"));
      }

      if (this.filterConfiguration.get("backward") != null) {
         backward = Boolean.valueOf((String)this.filterConfiguration.get("backward"));
      }

      if (this.filterConfiguration.get("smoothingFactor") != null) {
         smoothingFactor = Float.valueOf((String)this.filterConfiguration.get("smoothingFactor"));
      }

      if (this.filterConfiguration.get("twoWay") != null) {
         twoWay = Boolean.valueOf((String)this.filterConfiguration.get("twoWay"));
      }
   }

   private static Signal applyDistanceScalingFactor(Signal inputSignal) {
      Signal scaledSignal = new Signal();

      Map<String, Double> distanceFactors = getDistanceFactors(inputSignal);
      double minDistance = distanceFactors.get("minDistance");
      double distanceRange = distanceFactors.get("distanceRange");

      double scalingFactor;
      Signal previous = null;
      for (Signal component : inputSignal) {
         scalingFactor = previous == null ? 1
                 : 1 - (Math.abs(previous.getTime() - component.getTime()) - minDistance) / distanceRange;
         scaledSignal.addComponent(
                 new Signal(component.getTStart(), component.getTStop(), scalingFactor * component.getValue()));
         previous = component;
      }

      return scaledSignal;
   }
}
