/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.signalanalyzer;

import com.dmp.signalanalyzer.signal.Signal;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Paco
 */
public class SignalTest extends TestCase {
   
   public void testAddPulse(){
      Random rndObj = new Random();
      int numberOfItems = 10;
      Signal signal = new Signal();
      for (int i = 0; i < numberOfItems; i++){
         float randomValue = rndObj.nextFloat();
         signal.addComponent(new Signal(i, i, randomValue));
         assertTrue("Value mismatch on items " + i, signal.get(i).getValue() == randomValue);
      }
   }

   public void testOrderKeeping() {
      int firstEntryTime = 1;
      int lastEntryTime = 10;
      Signal signal = new Signal();
      // insert iterm in reverse order
      for (int i = lastEntryTime; i >= firstEntryTime; i--) {
         signal.addComponent(new Signal(i, i, i));
      }

      // test first and last
      assertEquals("Unexpected first entry", firstEntryTime, signal.firstEntry().getTime());
      assertEquals("Unexpected last entry", lastEntryTime, signal.lastEntry().getTime());

      // test if they are oredered by time
      Signal previous = null;
      for (Signal pulse : signal) {
         if (previous != null) {
            assertTrue(previous.getTime() <= pulse.getTime());
         }
         previous = pulse;
      }
   }

   public void testToReverseList() {
      Signal signal = new Signal();
      // insert iterm in ascending order
      for (int i = 1; i <= 10; i++) {
         signal.addComponent(new Signal(i, i, i));
      }

      // test if they are oredered descending by time
      Signal previous = null;
      List<Signal> signalList = signal.toList();
      Collections.reverse(signalList);
      for (Signal pulse : signalList) {
         if (previous != null) {
            assertTrue(previous.getTime() >= pulse.getTime());
         }
         previous = pulse;
      }
   }

   public void testIntersect() {
      int numberOfItems = 10;
      Signal sigObj1 = generateRandomSignalWithOrdinalPositions(numberOfItems);
      Signal sigObj2 = generateRandomSignalWithOrdinalPositions(numberOfItems);
      Signal intersect = sigObj1.intersect(sigObj2);

      for (int i = 0; i < numberOfItems; i++) {
         // I'm using ordinal positions
         int position = i;
         assertFalse(String.format("Item %s of the intersect is null",i),intersect.get(position) == null);
         assertFalse(String.format("Item %s of the intersect signal is NaN", i),Float.isNaN(intersect.get(position).getValue()));
         assertTrue(String.format("Wrong intersect value on position %s", i),
                 intersect.get(position).getValue()
                 == Math.min(sigObj1.get(position).getValue(), sigObj2.get(position).getValue()));
      }

   }

   public void testReassignmentIntersect() {
      int numberOfItems = 10;
      Signal sigobj1 = new Signal();
      Signal sigobj2 = new Signal();
      for (int i = 0; i < numberOfItems; i++) {
         sigobj1.addComponent(new Signal(i, i));
         sigobj2.addComponent(new Signal(i, i));
      }

      // Intersect reassigning the variable
      sigobj1 = sigobj1.intersect(sigobj2);
      for (int i = 0; i < numberOfItems; i++) {
         assertFalse("", sigobj1.get(i) == null);
         assertTrue("", sigobj1.get(i).getTime() == i);
         assertFalse("", Float.isNaN(sigobj1.get(i).getValue()));
         assertTrue("", sigobj1.get(i).getValue() == i);
      }


   }

   private Signal generateRandomSignalWithOrdinalPositions(int numberOfItems) {
      Signal outSignal = new Signal();

      Random rndObj = new Random();
      float[] signal = new float[numberOfItems];
      for (int i = 0; i < numberOfItems; i++) {
         signal[i] = rndObj.nextFloat();
      }
      outSignal.addComponentsArray(signal);

      return outSignal;
   }
}
