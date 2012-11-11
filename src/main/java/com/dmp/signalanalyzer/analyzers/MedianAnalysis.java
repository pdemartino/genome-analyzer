package com.dmp.signalanalyzer.analyzers;

import com.dmp.signalanalyzer.beans.PositionSortedSignal;
import com.dmp.signalanalyzer.beans.Slice;
import org.apache.commons.math.stat.descriptive.rank.Percentile;

/**
*
* @author Pasquale De Martino <paco.dmp@gmail.com>
*/
public class MedianAnalysis extends ASlicedSignalAnalyzer {
	private String name = "Median";
	private Percentile percentile;
	private double quantile = 50;
    
	public MedianAnalysis(PositionSortedSignal signal, double sliceSize,
			double sliceStep) {
		super(signal, sliceSize, sliceStep);
		percentile = new Percentile();
		percentile.setQuantile(quantile);
	}

	public float getSingleSliceValue(Slice slice){
    	return (float) percentile.evaluate(slice.doubleValuesArray());
    }
    	public String getName() {
		return this.name;
	}
}
