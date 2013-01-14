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
   private TreeMap<Integer, Signal> components;

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

   public void addComponentsArray(float[] signal, int[] positions) throws SignalLengthMismatch {
      int len = signal.length;
      if (len == positions.length) {
         for (int i = 0; i < len; i++) {
            this.addComponent(new Signal(positions[i], signal[i]));
         }
      } else {
         throw new SignalLengthMismatch();
      }
   }

   public void addComponentsArray(float[] signal) {
      for (int i = 0; i < signal.length; i++) {
         this.addComponent(new Signal(i, signal[i]));
      }
   }

   public boolean addComponent(Signal component) {
      if ((component.getTime() >= lowerBound) && (component.getTime() <= upperBound)) {
         if (this.components.get(component.getTime()) != null) {
            logger.warn(String.format("Component at time %s already existing into Signal!", component.getTime()));
         } else {
            this.components.put(Integer.valueOf(component.getTime()), component);
            this.size++;
            if ((this.tStart == null) || (this.tStart > component.getTStart())) {
               this.tStart = component.getTStart();
            }

            if ((this.tStop == null) || (this.tStop < component.getTStop())) {
               this.tStop = component.getTStop();
            }
            return true;
         }
      }
      return false;
   }

   public boolean addComponentIfCanContain(Signal component) {
      return (this.canContain(component) && this.addComponent(component));
   }

   public boolean canContain(Signal component) {
      return ((this.tStart == null || (component.getTStart() >= this.getTStart()))
              && (this.tStop == null || (component.getTStop() <= this.getTStop())));
   }

   @SuppressWarnings("empty-statement")
   public float[] distances(){
      if (this.count() == 0) {
         float[] distances = {0};
         return distances;
      }
      
      // the first item has not to be considered
      float[] distances = new float[this.count()-1];
      Signal previous = null;
      int i = 0;
      for (Signal component : this){
         if (previous != null){
            distances[i] = component.getTStart() - previous.getTStop();
         }
         previous = component;
      }
      return distances;
   }
   
   public void reset() {
      this.tStart = null;
      this.tStop = null;
      this.components = new TreeMap<Integer, Signal>();
   }

   public Signal intersect(Signal secondOne) {
      Signal intersectSignal = new Signal();

      for (Signal component : this) {
         int time = component.getTime();
         Signal otherPulse = secondOne.get(time);
         if (otherPulse != null) {
            float value = Math.min(component.getValue(), otherPulse.getValue());
            intersectSignal.addComponent(new Signal(time, time, value));
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
      return this.components.get(time);
   }

   public Signal firstEntry() {
      return this.components.firstEntry().getValue();
   }

   public Signal lastEntry() {
      return this.components.lastEntry().getValue();
   }

   public int numberOfComponents() {
      return this.components.size();
   }

   public List<Signal> toList() {
      List<Signal> outList = new LinkedList<Signal>();

      for (Signal component : this) {
         outList.add(component);
      }

      return outList;
   }

   public void setValue(float wholeSignalValue) {
      this.value = wholeSignalValue;
   }

   public double[] toDoubleValuesArray() {
      double[] valuesArray = new double[this.components.size()];
      int i = -1;
      for (Signal pulse : this) {
         valuesArray[++i] = pulse.getValue();
      }
      return valuesArray;
   }

   public Signal cloneAtLevel(int level) {
      Signal signalClone = new Signal(this.getTStart(), this.getTStop(), this.getValue());

      for (Signal component : this) {
         if (level > 0) { // Clone objects
            signalClone.addComponent(component.cloneAtLevel(level - 1));
         } else { // copy object references
            signalClone.addComponent(component);
         }
      }
      return signalClone;
   }

   public int count() {
      return size;
   }
   
   public Signal flat(){
      Signal flatted = new Signal();
      for (Signal window : this){
         float flattedValue = window.getValue();
         for (Signal component : window){
            flatted.addComponent(new Signal(component.getTStart(),component.getTStop(),flattedValue));
         }
      }
      return flatted;
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
         innerIterator = components.entrySet().iterator();
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
