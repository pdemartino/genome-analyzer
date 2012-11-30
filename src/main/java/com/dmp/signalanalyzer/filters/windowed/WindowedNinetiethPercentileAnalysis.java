package com.dmp.signalanalyzer.filters.windowed;

import com.dmp.signalanalyzer.beans.Signal;
import org.apache.commons.math.stat.descriptive.rank.Percentile;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class WindowedNinetiethPercentileAnalysis extends WindowedSignalFilter {

   private static final double quantile = 90.;
   private static Percentile percentile = new Percentile();

   public float getSingleWindowValue(Signal window) {
      percentile.setQuantile(quantile);
      float percentileVal = (float) percentile.evaluate(window.toDoubleValuesArray());
      // The more items you consider, the more this value is important
      percentileVal *= window.size();
      // The more this window is large, the less this value is important
      percentileVal /= (window.getTStop() - window.getTStart());
      return percentileVal;
   }
}
