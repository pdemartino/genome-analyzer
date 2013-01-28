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
   
   public void testTime(){
       double time = (new Random()).nextDouble();
       Signal underTest = new Signal(time, 0.);
       
       assertEquals(time, underTest.getTime());
   }
    
    public void testAddPulse(){
      Random rndObj = new Random();
      int numberOfItems = 10;
      Signal signal = new Signal();
      for (double i = 0; i < numberOfItems; i++){
         double randomValue = rndObj.nextDouble();
         signal.addComponent(new Signal(i, i, randomValue));
         assertEquals("Value mismatch on items " + i, randomValue, signal.get(i).getValue(),0.00001);
      }
   }
   
   public void testOrderKeeping() {
      double firstEntryTime = 1;
      double lastEntryTime = 10;
      Signal signal = new Signal();
      // insert iterm in reverse order
      for (double i = lastEntryTime; i >= firstEntryTime; i--) {
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
      for (double i = 1; i <= 10; i++) {
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
         double position = i;
         assertFalse(String.format("Item %s of the intersect is null",i),intersect.get(position) == null);
         assertFalse(String.format("Item %s of the intersect signal is NaN", i),Double.isNaN(intersect.get(position).getValue()));
         assertTrue(String.format("Wrong intersect value on position %s", i),
                 intersect.get(position).getValue()
                 == Math.min(sigObj1.get(position).getValue(), sigObj2.get(position).getValue()));
      }

   }

   public void testReassignmentIntersect() {
      int numberOfItems = 10;
      Signal sigobj1 = new Signal();
      Signal sigobj2 = new Signal();
      for (double i = 0; i < numberOfItems; i++) {
         sigobj1.addComponent(new Signal(i, i));
         sigobj2.addComponent(new Signal(i, i));
      }

      // Intersect reassigning the variable
      sigobj1 = sigobj1.intersect(sigobj2);
      for (int i = 0; i < numberOfItems; i++) {
         assertFalse("", sigobj1.get(i) == null);
         assertTrue("", sigobj1.get(i).getTime() == i);
         assertFalse("", Double.isNaN(sigobj1.get(i).getValue()));
         assertTrue("", sigobj1.get(i).getValue() == i);
      }


   }
   
   public void testApplySlidingWindow(){
       int numberOfItems = 50;
       double winSize = numberOfItems / 10.;
       double stepSize = winSize / 2.;
       
       Signal underTest = generateRandomSignalWithOrdinalPositions(numberOfItems);
       
       Signal windowed = underTest.applySlidingWindow(winSize, stepSize);
       
       for (Signal component : underTest){
           for (Signal window : windowed){
               if (component.getTime() < window.getTStop()
                       && component.getTime() >= window.getTStart()){
                   assertTrue(
                           String.format("Unexpedted missing component %s in win [%s,%s]"
                           ,component.getTime(),window.getTStart(),window.getTStop()
                           ),window.get(component.getTime())!= null);
               }else{
                   assertTrue(
                           String.format("Unexpedted found component %s in win [%s,%s]"
                           ,component.getTime(),window.getTStart(),window.getTStop()
                           ),
                           window.get(component.getTime())== null);
               }
           }
       }
   }

   private Signal generateRandomSignalWithOrdinalPositions(int numberOfItems) {
      Signal outSignal = new Signal();

      Random rndObj = new Random();
      Double[] signal = new Double[numberOfItems];
      for (int i = 0; i < numberOfItems; i++) {
         signal[i] = rndObj.nextDouble();
      }
      outSignal.addComponentsArray(signal);

      return outSignal;
   }
   
   
}
