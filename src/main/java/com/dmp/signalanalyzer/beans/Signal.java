package com.dmp.signalanalyzer.beans;

import com.dmp.signalanalyzer.exceptions.SignalLengthMismatch;
import com.dmp.signalanalyzer.utils.SignalAnalyzerConstants;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class Signal {

   private Float tStart = null, tStop = null;
   private Float value = null;
   private List<Signal> pulses;

   public Signal(float tStart, float tStop, float value) {
      this.reset();
      this.tStart = tStart;
      this.tStop = tStop;
      this.value = value;
   }

   public Signal(float time, float value) {
      this(time, time, value);
   }
   
   public Signal(){
      this.reset();
   }

   public boolean addPulse(Signal pulse) {

      if (this.pulses.add(pulse)) {
         if ((this.tStart == null) || (this.tStart > pulse.getTStart())) {
            this.tStart = pulse.getTStart();
         }

         if ((this.tStop == null) || (this.tStop < pulse.getTStop())) {
            this.tStop = pulse.getTStop();
         }
         return true;
      }
      return false;
   }

   public boolean addPulseIfCanContain(Signal pulse) {
      return (this.canContain(pulse) && this.pulses.add(pulse));
   }

   public boolean canContain(Signal pulse) {
      return (
              (this.tStart == null || (pulse.getTStart() >= this.getTStart()))
              && (this.tStop == null || (pulse.getTStop() <= this.getTStop())));
   }

   public void loadPulsesFromFile(String positionsFileName, String valuesFileName) throws SignalLengthMismatch, FileNotFoundException {
      this.reset();

      Scanner positionsFileScanner = new Scanner(new File(positionsFileName));
      positionsFileScanner.useLocale(SignalAnalyzerConstants.DEFAULT_LOCALE);
      Scanner valuesFileScanner = new Scanner(new File(valuesFileName));
      valuesFileScanner.useLocale(SignalAnalyzerConstants.DEFAULT_LOCALE);

      while (positionsFileScanner.hasNextFloat() && valuesFileScanner.hasNextFloat()) {
         Float filePosition = positionsFileScanner.nextFloat();
         Float fileValue = valuesFileScanner.nextFloat();
         Signal pulse = new Signal(filePosition.floatValue(), fileValue.floatValue());
         this.addPulse(pulse);
      }
      boolean stillHavePositions = positionsFileScanner.hasNextDouble();
      boolean stillHaveValues = valuesFileScanner.hasNextFloat();

      positionsFileScanner.close();
      valuesFileScanner.close();

      // At the and check if values and positions number are the same
      if (stillHavePositions || stillHaveValues) {
         this.reset();
         throw new SignalLengthMismatch("Positions and Values array must have same length");
      }
   }
   
   public void reset(){
      this.tStart = null;
      this.tStop = null;
      this.pulses = new LinkedList<Signal>();
   }
   
   public void writeToFile(String filepath, String separator) throws IOException {
      FileWriter fwr = new FileWriter(filepath);
      BufferedWriter bwr = new BufferedWriter(fwr);

      for (Signal p : this.pulses) {
         bwr.write(String.valueOf(p.getTime()));
         bwr.write(separator);
         bwr.write(String.valueOf(p.getValue()));
         bwr.newLine();
      }

      bwr.close();
   }
   
   public void sortByPosition() {
      // sort pulses
      for (Signal p : this.pulses){
         p.sortByPosition();
      }
      Collections.sort(this.pulses, new Signal.SignalComparatorByPosition());
   }
   
   @Override
   public String toString() {
      String outString = "{";
      for (Signal pulse : this.pulses) {
         outString += String.format("\n\t%s", pulse);
      }
      outString += "}";

      return outString;
   }

   // Accessors
   public float getTStart() {
      return tStart.floatValue();
   }

   public float getTStop() {
      return tStop.floatValue();
   }
   
   public float getTime(){
      return (this.getTStart() + this.getTStop())/2f;
   }
   
   public float getValue(){
      return this.value.floatValue();
   }

   public Signal get(int i) {
      return this.pulses.get(i);
   }

   public int size() {
      return this.pulses.size();
   }

   public Iterable<Signal> getPulses() {
      return this.pulses;
   }

   public Iterator<Signal> iterator() {
      return this.pulses.iterator();
   }

   public void setValue(float wholeSignalValue) {
      this.value = wholeSignalValue;
   }

   public double[] toDoubleValuesArray() {
      double[] valuesArray = new double[this.pulses.size()];
      int i = -1;
      for (Signal pulse : this.pulses){
         valuesArray[++i] = pulse.getValue();
      }
      
      return valuesArray;
   }
   
   // Auxiliary classes
   public static class SignalComparatorByPosition implements Comparator<Signal> {

      public int compare(Signal o1, Signal o2) {
         return (o1.getTime() < o2.getTime() ? -1
                 : (o1.getTime() == o2.getTime() ? 0 : 1));
      }
   }
   
   
}
