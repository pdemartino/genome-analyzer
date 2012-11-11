package com.dmp.signalanalyzer.beans;

import java.util.Comparator;


public class Pulse {
	private Double position;
	private Float value;
	
	public Pulse(Double position, Float value){
		this.position = position;
		this.value = value;
	}

	public Double getPosition() {
		return position;
	}

	public Float getValue() {
		return value;
	}
	
    @Override
	public String toString(){
		return String.format("[pos:%s ,val:%s]", position, value);
	}
	
	public static class PulsesComparatorByPosition implements Comparator<Pulse>{

		public int compare(Pulse o1, Pulse o2) {
			return (
					o1.getPosition() < o2.getPosition() ? -1 
							: (o1.getPosition() == o2.getPosition() ? 0 : 1));
		}
	}
	
}
