package com.dmp.signalanalyzer.filters.windowed;


import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.signal.Signal;
import com.dmp.signalanalyzer.utils.SAMath;
import org.apache.log4j.Logger;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public abstract class WindowedSignalFilter extends SignalFilter {
   private static final Logger logger = Logger.getLogger(WindowedSignalFilter.class.getName());
   public static Double windowMultiplier = 0.01;
   public static Double stepMultiplier = 0.5;

    public Signal filter(Signal inputSignal) {
        double signalSize = inputSignal.getTStop() - inputSignal.getTStart();
        double winMult = filterConfiguration.get("window") != null
                ? (Double.valueOf((String)filterConfiguration.get("window")))
                : this.windowMultiplier;
        double winSize = winMult * signalSize;

        double stSize = (double) (winSize * stepMultiplier);

        logger.info(String.format("Applying sliding window (win: %s, step: %s)",winSize,stSize));
        Signal windowedSignal = inputSignal.applySlidingWindow(winSize,stSize);
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
