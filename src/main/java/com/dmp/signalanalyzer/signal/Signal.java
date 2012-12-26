package com.dmp.signalanalyzer.signal;

import com.dmp.signalanalyzer.configuration.ConfigurationManager;
import com.dmp.signalanalyzer.exceptions.SignalLengthMismatch;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class Signal {

   private float lowerBound = Float.MIN_VALUE, upperBound = Float.MAX_VALUE;
   private Float tStart = null, tStop = null;
   private Float value = null;
   // Use a map to index using time
   private Map<Float, Signal> pulses;

   public Signal(float tStart, float tStop, float value) {
      this.reset();
      this.tStart = tStart;
      this.tStop = tStop;
      this.value = value;
   }

   public Signal(float time, float value) {
      this(time, time, value);
   }

   public Signal() {
      this.reset();
   }

   public void addPulsesArray(float[] signal, float[] positions) throws SignalLengthMismatch {
      int len = signal.length;
      if (len == positions.length) {
         for (int i = 0; i < len; i++) {
            this.addPulse(new Signal(positions[i], signal[i]));
         }
      } else {
         throw new SignalLengthMismatch();
      }
   }

   public void addPulsesArray(float[] signal) {
      for (int i = 0; i < signal.length; i++) {
         this.addPulse(new Signal(signal[i], i));
      }
   }

   public boolean addPulse(Signal pulse) {
      if ((pulse.getTime() >= lowerBound) && (pulse.getTime() <= upperBound)) {
         this.pulses.put(Float.valueOf(pulse.getTime()), pulse);
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
      return (this.canContain(pulse) && this.addPulse(pulse));
   }

   public boolean canContain(Signal pulse) {
      return ((this.tStart == null || (pulse.getTStart() >= this.getTStart()))
              && (this.tStop == null || (pulse.getTStop() <= this.getTStop())));
   }

   public void reset() {
      this.tStart = null;
      this.tStop = null;
      this.pulses = new HashMap<Float, Signal>();
   }

   public void writeToFile(String filepath, char separator) throws IOException {
      ConfigurationManager configurationManager = ConfigurationManager.getInstance();
      this.writeToFile(filepath, separator, configurationManager.isBufferedReaderAsDefault());
   }

   public void writeToFile(String filepath, char separator, boolean bufferedWriting) throws IOException {
      String newLine = "\n";

      Writer fwr = new FileWriter(filepath);

      if (bufferedWriting) {
         fwr = new BufferedWriter(fwr);
      }

      // Print Header
      fwr.write("WindowID");
      fwr.write(separator + "Size");
      fwr.write(separator + "Start_Position");
      fwr.write(separator + "Stop_Position");
      fwr.write(separator + "Center");
      fwr.write(separator + "Value");

      int i = 0;
      for (Signal p : this.pulses) {
         fwr.write(newLine);
         fwr.write(String.valueOf(++i));
         fwr.write(separator + String.valueOf(p.getTStop() - p.getTStart()));
         fwr.write(separator + String.valueOf(p.getTStart()));
         fwr.write(separator + String.valueOf(p.getTStop()));
         fwr.write(separator + String.valueOf(p.getTime()));
         fwr.write(separator + String.valueOf(p.getValue()));
      }

      fwr.close();
   }

   public Signal intersect(Signal secondOne) {
      Signal intersectSignal = new Signal();

      for (Signal pulse : this.getPulses()) {
         float time = pulse.getTime();
         float myValue = pulse.getValue();
         secondOne.
       
      }
   }

   public void sortByPosition() {r
      // sort pulses
      for (Signal p : this.pulses) {
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

   public float getTime() {
      return (this.getTStart() + this.getTStop()) / 2f;
   }

   public float getValue() {
      if (this.value != null) {
         return this.value.floatValue();
      } else {
         return 0f;
      }
   }

   public void setLowerBound(float lowerBound) {
      this.lowerBound = lowerBound;
   }

   public void setUpperBound(float upperBound) {
      this.upperBound = upperBound;
   }

   public Signal get(int i) {
      return this.pulses.get(i);
   }

   public int size() {
      return this.pulses.size();
   }

   public Iterable<Signal> getPulses() {
      return this.pulses.entrySet().iterator();
   }

   public List<Signal> toList() {
      List<Signal> outList = new ArrayList<Signal>();

      for (Signal pulse : this.getPulses()) {
         outList.add(pulse);
      }

      return outList;
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
      for (Signal pulse : this.pulses) {
         valuesArray[++i] = pulse.getValue();
      }

      return valuesArray;
   }

   public Signal cloneAtLevel(int level) {
      Signal signalClone = new Signal(this.getTStart(), this.getTStop(), this.getValue());

      for (Signal pulse : this.getPulses()) {
         if (level > 0) { // Clone objects
            signalClone.addPulse(pulse.cloneAtLevel(level - 1));
         } else { // copy object references
            signalClone.addPulse(pulse);
         }
      }
      return signalClone;
   }

   public Signal clone() {
      return this.cloneAtLevel(Integer.MAX_VALUE);


   }

   // Auxiliary classes
   private static class SignalComparatorByPosition implements Comparator<Signal> {

      public int compare(Signal o1, Signal o2) {
         return (o1.getTime() < o2.getTime() ? -1
                 : (o1.getTime() == o2.getTime() ? 0 : 1));
      }
   }

   public static Comparator<Signal> comparatorByPosition() {
      return new SignalComparatorByPosition();


   }

   private static class SignalComparatorByValue implements Comparator<Signal> {

      public int compare(Signal o1, Signal o2) {
         return (o1.getValue() < o2.getValue() ? -1
                 : (o1.getValue() == o2.getValue() ? 0 : 1));
      }
   }

   public static Comparator<Signal> comparatorByValue() {
      return new SignalComparatorByValue();
   }
}
