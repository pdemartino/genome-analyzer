package com.dmp.signalanalyzer.beans;

import com.dmp.signalanalyzer.beans.Pulse.PulsesComparatorByPosition;
import com.dmp.signalanalyzer.exceptions.SignalLengthMismatch;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PositionSortedSignal {
	private List<Pulse> positionSortedSignal;

	public PositionSortedSignal(double[] positions, float[] values) throws SignalLengthMismatch {
		if (positions.length != values.length){
			throw new SignalLengthMismatch("Positions and Values array must have same length");
		}else{
			int signalLenth = positions.length;
			this.positionSortedSignal = new LinkedList<Pulse>();
			
			for (int i = 0; i < signalLenth; i++) {
				Pulse pulse = new Pulse(positions[i],values[i]);
				this.addPulse(pulse);
			}
			this.sortSignalByPosition();
		}
	}
	
	public PositionSortedSignal(String positionsFileName, String valuesFileName) throws SignalLengthMismatch, FileNotFoundException{
		
		Scanner positionsFileScanner = new Scanner(new FileInputStream(positionsFileName));
		Scanner valuesFileScanner = new Scanner(new FileInputStream(valuesFileName));
		this.positionSortedSignal = new LinkedList<Pulse>();
		
		while (positionsFileScanner.hasNextDouble() && valuesFileScanner.hasNextFloat()){
			Double position = positionsFileScanner.nextDouble();
			Float value = valuesFileScanner.nextFloat();
			Pulse pulse = new Pulse(position,value);
			this.addPulse(pulse);
		}
		boolean stillHavePositions = positionsFileScanner.hasNextDouble();
		boolean stillHaveValues = valuesFileScanner.hasNextFloat();
		
		positionsFileScanner.close();
		valuesFileScanner.close();
		
		// At the and check if values and positions number are the same
		if (stillHavePositions || stillHaveValues){
			this.positionSortedSignal = null;
			throw new SignalLengthMismatch("Positions and Values array must have same length");
		}else{
			this.sortSignalByPosition();
		}
	}

	public Iterator<Pulse> iterator(){
		return this.positionSortedSignal.iterator();
	}
	
	private void addPulse(Pulse pulse){
		this.positionSortedSignal.add(pulse);
	}
	
	private void sortSignalByPosition(){
		Collections.sort(positionSortedSignal, new PulsesComparatorByPosition());
	}

	public int size() {
		return this.positionSortedSignal.size();
	}
	
	public String toString(){
		String outString = "{";
		for (Pulse pulse : this.positionSortedSignal){
			outString+= String.format("\n\t%s", pulse);
		}
		outString+="}";
		
		return outString;
	}
	
	public Double getStartPosition(){
		return this.positionSortedSignal.get(0).getPosition();
	}
	
	public Double getStopPosition(){
		return this.positionSortedSignal.get(positionSortedSignal.size() -1 ).getPosition();
	}
}
