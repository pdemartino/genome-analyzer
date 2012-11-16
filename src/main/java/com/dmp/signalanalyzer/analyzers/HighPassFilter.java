/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.signalanalyzer.analyzers;

import com.dmp.signalanalyzer.beans.PositionSortedSignal;
import com.dmp.signalanalyzer.beans.Pulse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author pdemartino <pdemartino@venere.com>
 */
public class HighPassFilter implements ISignalAnalyzer{
   private PositionSortedSignal signal;
   private Map<Double,Double> analysis;
   private double cutOff = 0.5;
   double a = (2 - cutOff) / (2 + cutOff);
   double b = 2/(2+cutOff);

   public String getName() {
      return "HPF";
   }
   
   public void setSignal(PositionSortedSignal signal){
      this.signal = signal;
   }
   
   public void setCufOffFrequency(double freq){
      this.cutOff = freq;
   }

   public void runAnalysis() {
      this.analysis = new HashMap<Double, Double>();
      
      Iterator<Pulse> pIt = signal.iterator();
      
      double preX = -1;
      double preY = -1;
      while(pIt.hasNext()){
         Pulse p = pIt.next();
         
         if (preX == -1){
            preX = p.getValue();
            preY = preX;
         }
         
         double y = passHighFunction(preY, preX, p.getValue());
         this.analysis.put(p.getPosition(), y);
         
         preX = p.getValue();
         preY = y;
      }
      
   }
   
   private double passHighFunction(double yPre, double pre, double now){
      double yNow = a*yPre + b * (now + pre);
      return yNow;
   }

   public Object getAnalysis() {
      return this.analysis;
   }
   
}
