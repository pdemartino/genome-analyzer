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

   public float getSingleWindowValue(Signal slice) {
      percentile.setQuantile(quantile);
      double[] values = slice.toDoubleValuesArray();
      return (float) percentile.evaluate(values);
   }


}
