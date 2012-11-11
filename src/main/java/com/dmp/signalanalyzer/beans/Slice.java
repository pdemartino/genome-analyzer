package com.dmp.signalanalyzer.beans;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Slice {
	private List<Pulse> pulses = new LinkedList<Pulse>();
	double start, stop;
	private float value;

	public Slice(double start, double stop) {
		this.start = start;
		this.stop = stop;
	}

	public List<Pulse> getPulses() {
		return pulses;
	}

	public void setPulses(List<Pulse> pulses) {
		this.pulses = pulses;
	}

	public void addPulse(Pulse pulse) {
		this.pulses.add(pulse);
	}

	public double getSliceCenter() {
		return (this.start + this.stop) / 2d;
	}

	public double getStart() {
		return start;
	}

	public double getStop() {
		return stop;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

    @Override
	public String toString() {
		String outString = "{" + "\nstart: " + start + "\nstop: " + stop
                + "\nvalue:" + value
				+ "\npulses:";

		for (Pulse pulse : this.pulses) {
			outString += String.format("\n\t%s", pulse);
		}

		outString += "\n}";
		return outString;
	}

	public boolean addPulseIfCanContain(Pulse pulse) {
		if (pulse.getPosition() < this.start
				|| pulse.getPosition() > this.stop) {
            return false;
        }else{
			this.pulses.add(pulse);
			return true;
		}
	}

	public Iterator<Pulse> iterator() {
		return this.pulses.iterator();
	}

	public int size() {
		return this.pulses.size();
	}

	public float[] valuesArray() {
		float[] outArray = new float[this.pulses.size()];

		int i = 0;
		for (Pulse pulse : this.pulses) {
			outArray[i] = pulse.getValue();
			i++;
		}

		return outArray;
	}

	public double[] doubleValuesArray() {
		double[] outArray = new double[this.pulses.size()];

		int i = 0;
		for (Pulse pulse : this.pulses) {
			outArray[i] = pulse.getValue();
			i++;
		}

		return outArray;
	}

}
