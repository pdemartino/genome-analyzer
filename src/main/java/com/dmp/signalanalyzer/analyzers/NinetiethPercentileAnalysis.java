package com.dmp.signalanalyzer.analyzers;

import com.dmp.signalanalyzer.beans.PositionSortedSignal;
import com.dmp.signalanalyzer.beans.Slice;
import org.apache.commons.math.stat.descriptive.rank.Percentile;

/**
*
* @author Pasquale De Martino <paco.dmp@gmail.com>
*/
public class NinetiethPercentileAnalysis extends ASlicedSignalAnalyzer {
	private Percentile percentile;
	private double quantile = 90;

	
	public NinetiethPercentileAnalysis(PositionSortedSignal signal,
			double sliceSize, double sliceStep) {
		super(signal, sliceSize, sliceStep);
		this.percentile = new Percentile();
		this.percentile.setQuantile(quantile);
	}
        
    public float getSingleSliceValue(Slice slice){
    	double[] values = slice.doubleValuesArray();
    	return (float) this.percentile.evaluate(values);
    }

	public String getName() {
		return "Ninetieth Percentile";
	}
       
}
