package com.dmp.signalanalyzer.analyzers;

import com.dmp.signalanalyzer.beans.PositionSortedSignal;
import com.dmp.signalanalyzer.beans.Pulse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class HighPassFilter {
	private PositionSortedSignal signal;
	private List<Float> analysis;
	private float cutOff = 0.5f;
	private float alfa;
	private float beta; 

	public String getName() {
		return "HPF";
	}
	
	

	public HighPassFilter() {
		super();
		this.setCufOffFrequency(cutOff);
	}



	public void setSignal(PositionSortedSignal signal) {
		this.signal = signal;
	}

	public void setCufOffFrequency(float freq) {
		this.cutOff = freq;
		alfa = (2 - cutOff) / (2 + cutOff);
		beta = 2 / (2 + cutOff);
	}

	public void runAnalysis() {
		this.analysis = new ArrayList<Float>();

		Iterator<Pulse> pIt = signal.iterator();

		float preX = -2f;
		float preY = -2f;
		while (pIt.hasNext()) {
			Pulse p = pIt.next();

			if (preX == -2f) {
				preX = p.getValue();
				preY = preX;
			}

			//float y = passHighFunction(preY, preX, p.getValue());
                        float y = smoothedValue(p.getValue(), p.getPosition());
			System.out.println(String.format("%s:%s", p.getPosition(),y));
			this.analysis.add(y);

			preX = p.getValue();
			preY = y;
		}

	}

	private float passHighFunction(float yPre, float pre, float now) {
		//double yNow = (alfa * yPre) + (beta * (now - pre));
		float yNow = alfa * (yPre + now - pre);
		return yNow;
	}
        
        private float smoothed = 0f;
        private float smoothing = 10f;
        private double lastUpdate = 0;
        private float smoothedValue(float originalValue, double originalPosition){
            double elapsedLoci = originalPosition - lastUpdate;
            smoothed +=  elapsedLoci * (originalValue - smoothed) / smoothing;
            lastUpdate = originalPosition;
            return smoothed;
            
        }

	public List<Float> getAnalysis() {
		return this.analysis;
	}

}
