package com.dmp.signalanalyzer.analyzers;

import com.dmp.signalanalyzer.beans.PositionSortedSignal;
import com.dmp.signalanalyzer.beans.Pulse;
import com.dmp.signalanalyzer.beans.Slice;
import java.util.Iterator;

/**
*
* @author Pasquale De Martino <paco.dmp@gmail.com>
*/
public class MeanAnalysis extends ASlicedSignalAnalyzer {
	private String name = "Mean";

	public MeanAnalysis(PositionSortedSignal signal, double sliceSize,
			double sliceStep) {
		super(signal, sliceSize, sliceStep);
		
	}
	
	public float getSingleSliceValue(Slice slice){
		float sum = 0f;
		Iterator<Pulse> pulsesIterator = slice.iterator();
		while(pulsesIterator.hasNext()){
			sum += pulsesIterator.next().getValue();
		}
		return sum / slice.size();
	}

	public String getName() {
		return this.name;
	}
	
    
    
}
    
   
