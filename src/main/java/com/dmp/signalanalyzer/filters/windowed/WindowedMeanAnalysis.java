package com.dmp.signalanalyzer.filters.windowed;

import com.dmp.signalanalyzer.beans.Signal;

/**
*
* @author Pasquale De Martino <paco.dmp@gmail.com>
*/
public class WindowedMeanAnalysis extends WindowedSignalFilter {
	
	public float getSingleWindowValue(Signal window){
		float sum = 0f;
		
		for (Signal pulse : window.getPulses()){
			sum += pulse.getValue();
		}
		return sum / window.size();
	}

}
    
   
