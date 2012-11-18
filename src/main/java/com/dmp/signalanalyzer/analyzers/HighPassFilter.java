package com.dmp.signalanalyzer.analyzers;

import com.dmp.signalanalyzer.beans.PositionSortedSignal;
import com.dmp.signalanalyzer.beans.Pulse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class HighPassFilter {
	private PositionSortedSignal signal;
	private Map<Double, Float> analysis;
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
		this.analysis = new HashMap<Double, Float>();

		Iterator<Pulse> pIt = signal.iterator();

		float preX = -2f;
		float preY = -2f;
		while (pIt.hasNext()) {
			Pulse p = pIt.next();

			if (preX == -2f) {
				preX = p.getValue();
				preY = preX;
			}

			float y = passHighFunction(preY, preX, p.getValue());
			System.out.println(String.format("%s:%s", p.getPosition(),y));
			this.analysis.put(p.getPosition(), y);

			preX = p.getValue();
			preY = y;
		}

	}

	private float passHighFunction(float yPre, float pre, float now) {
		//double yNow = (alfa * yPre) + (beta * (now - pre));
		float yNow = alfa * (yPre + now - pre);
		return yNow;
	}

	public Map<Double,Float> getAnalysis() {
		return this.analysis;
	}

}
