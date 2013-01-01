package com.dmp.signalanalyzer.signal;

import com.dmp.signalanalyzer.exceptions.SignalLengthMismatch;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class Signal implements Iterable<Signal> {

   private Logger logger = Logger.getLogger(Signal.class.getName());
   private int lowerBound = -1 * Integer.MAX_VALUE, upperBound = Integer.MAX_VALUE;
   private Integer tStart = null, tStop = null;
   private Float value = null;
   private int size = 0;
   // Using a TreeMap to automatically keep items ordered by time (the key value)
   private TreeMap<Integer, Signal> pulses;

   public Signal(int tStart, int tStop, float value) {
      this();
      this.tStart = tStart;
      this.tStop = tStop;
      this.value = value;
   }

   public Signal(int time, float value) {
      this(time, time, value);
   }

   public Signal() {
      this.reset();
   }

   public void addPulsesArray(float[] signal, int[] positions) throws SignalLengthMismatch {
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
         this.addPulse(new Signal(i, signal[i]));
      }
   }

   public boolean addPulse(Signal pulse) {
      if ((pulse.getTime() >= lowerBound) && (pulse.getTime() <= upperBound)) {
         if (this.pulses.get(pulse.getTime()) != null) {
            logger.warn(String.format("Pulse at time %s already existing into Signal!", pulse.getTime()));
         } else {
            this.pulses.put(Integer.valueOf(pulse.getTime()), pulse);
            this.size++;
            if ((this.tStart == null) || (this.tStart > pulse.getTStart())) {
               this.tStart = pulse.getTStart();
            }

            if ((this.tStop == null) || (this.tStop < pulse.getTStop())) {
               this.tStop = pulse.getTStop();
            }
            return true;
         }
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
      this.pulses = new TreeMap<Integer, Signal>();
   }

   public Signal intersect(Signal secondOne) {
      Signal intersectSignal = new Signal();

      for (Signal pulse : this) {
         int time = pulse.getTime();
         Signal otherPulse = secondOne.get(time);
         if (otherPulse != null) {
            float value = Math.min(pulse.getValue(), otherPulse.getValue());
            intersectSignal.addPulse(new Signal(time, time, value));
         }
      }
      return intersectSignal;
   }

   @Override
   public String toString() {
      String outString = "{";
      for (Signal pulse : this) {
         outString += String.format("\n\t%s", pulse);
      }
      outString += "}";

      return outString;
   }

   // Accessors
   public int getTStart() {
      return tStart.intValue();
   }

   public int getTStop() {
      return tStop.intValue();
   }

   public int getTime() {
      return (this.getTStart() + this.getTStop()) / 2;
   }

   public float getValue() {
      if (this.value != null) {
         return this.value.floatValue();
      } else {
         return 0f;
      }
   }

   public void setLowerBound(int lowerBound) {
      this.lowerBound = lowerBound;
   }

   public void setUpperBound(int upperBound) {
      this.upperBound = upperBound;
   }

   public Signal get(int time) {
      return this.pulses.get(time);
   }

   public Signal firstEntry() {
      return this.pulses.firstEntry().getValue();
   }

   public Signal lastEntry() {
      return this.pulses.lastEntry().getValue();
   }

   public int size() {
      return this.pulses.size();
   }

   public List<Signal> toList() {
      List<Signal> outList = new LinkedList<Signal>();

      for (Signal pulse : this) {
         outList.add(pulse);
      }

      return outList;
   }

   public void setValue(float wholeSignalValue) {
      this.value = wholeSignalValue;
   }

   public double[] toDoubleValuesArray() {
      double[] valuesArray = new double[this.pulses.size()];
      int i = -1;
      for (Signal pulse : this) {
         valuesArray[++i] = pulse.getValue();
      }
      return valuesArray;
   }

   public Signal cloneAtLevel(int level) {
      Signal signalClone = new Signal(this.getTStart(), this.getTStop(), this.getValue());

      for (Signal pulse : this) {
         if (level > 0) { // Clone objects
            signalClone.addPulse(pulse.cloneAtLevel(level - 1));
         } else { // copy object references
            signalClone.addPulse(pulse);
         }
      }
      return signalClone;
   }

   public int getSize() {
      return size;
   }

   @Override
   public Signal clone() {
      return this.cloneAtLevel(Integer.MAX_VALUE);
   }

   public Iterator<Signal> iterator() {
      return new SignalIterator();
   }

   public SignalStats getStatistics() {
      return new SignalStats(this);
   }

   // Auxiliary classes
   private static class SignalComparatorByValue implements Comparator<Signal> {

      public int compare(Signal o1, Signal o2) {
         return (o1.getValue() < o2.getValue() ? -1
                 : (o1.getValue() == o2.getValue() ? 0 : 1));
      }
   }

   public static Comparator<Signal> comparatorByValue() {
      return new SignalComparatorByValue();
   }

   private class SignalIterator implements Iterator<Signal> {

      Iterator innerIterator;

      public SignalIterator() {
         super();
         innerIterator = pulses.entrySet().iterator();
      }

      public boolean hasNext() {
         return innerIterator.hasNext();
      }

      public Signal next() {
         Map.Entry<Float, Signal> nextOne = (Map.Entry<Float, Signal>) innerIterator.next();
         return nextOne.getValue();
      }

      public void remove() {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }
}
