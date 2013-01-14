package com.dmp.signalanalyzer.filters.windowed;

import com.dmp.signalanalyzer.signal.Signal;

/**
*
* @author Pasquale De Martino <paco.dmp@gmail.com>
*/
public class WindowedMeanAnalysis extends WindowedSignalFilter {
	
	public float getSingleWindowValue(Signal window){
		float sum = 0f;
		
		for (Signal component : window){
			sum += component.getValue();
		}
		return sum / window.count();
	}

}
    
   
