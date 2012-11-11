package com.dmp.signalanalyzer.analyzers;

import com.dmp.signalanalyzer.beans.PositionSortedSignal;
import com.dmp.signalanalyzer.beans.Pulse;
import com.dmp.signalanalyzer.beans.Slice;
import java.util.Iterator;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class MaximumAnalysis extends ASlicedSignalAnalyzer{
	private String name = "Maximum"; 
    public MaximumAnalysis(PositionSortedSignal signal, double sliceSize, double sliceStep) {
    	super(signal, sliceSize, sliceStep);
    }
    
    public float getSingleSliceValue(Slice slice){
    	float maximum = 0f;
    	
        Iterator<Pulse> pulseIterator = slice.iterator();
        while(pulseIterator.hasNext()){
        	Pulse pulse = pulseIterator.next();
        	if (pulse.getValue() > maximum){
        		maximum = pulse.getValue();
        	}
        }
        
        return maximum;
    }

	public String getName() {
		return this.name;
	}
    
}
