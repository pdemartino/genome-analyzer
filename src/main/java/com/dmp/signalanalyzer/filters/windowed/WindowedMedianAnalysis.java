package com.dmp.signalanalyzer.filters.windowed;

import com.dmp.signalanalyzer.signal.Signal;
import org.apache.commons.math.stat.descriptive.rank.Percentile;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class WindowedMedianAnalysis extends WindowedSignalFilter {

   private static final double quantile = 50.;
   private static Percentile percentile = new Percentile();

   
   public double getSingleWindowValue(Signal window) {
      percentile.setQuantile(quantile);
      double percentileVal = percentile.evaluate(window.getY());
      return percentileVal;
   }

}
