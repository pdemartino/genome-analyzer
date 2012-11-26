package com.dmp.signalanalyzer.filters.windowed;

import com.dmp.signalanalyzer.beans.Signal;
import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.utils.SignalAnalyzerConstants;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public abstract class WindowedSignalFilter extends SignalFilter {
   private float stepSize = -1f, windowSize = -1f;
    
  
   public Signal filter(Signal inputSignal) {
      Signal windowedSignal = generateWindowedSignal(inputSignal);
      float winValue;
      for (Signal window : windowedSignal.getPulses()){
         winValue = getSingleWindowValue(window);
         if (Float.isNaN(winValue)) {
            winValue = 0f;
         }
         window.setValue(winValue);
      }
      return windowedSignal;
   }

   private Signal generateWindowedSignal(Signal inputSignal) {
      Signal windowedSignal = new Signal();
    
      float winSize = this.windowSize > 0 ? this.windowSize 
              : inputSignal.getTStop() * SignalAnalyzerConstants.WINDOW_MULT;
     
      float stSize = this.stepSize > 0 ? this.stepSize 
              : inputSignal.getTStop() * SignalAnalyzerConstants.STEP_MULT;
      
      // Create Windows
      float start = inputSignal.getTStart();
      float stop;
      while (start <= inputSignal.getTStop()) {
         stop = Math.min(start + winSize, inputSignal.getTStop());
         Signal window = new Signal(start, stop, 0f);
         windowedSignal.addPulse(window);
         start = start + stSize;
      }

      // Fill Windows
      boolean inserted;
      for (Signal pulse : inputSignal.getPulses()) {
         inserted = false;
         for (Signal window : windowedSignal.getPulses()) {
            inserted = window.addPulseIfCanContain(pulse);
            if (inserted) {
               break;
            }
         }
      }
      
      return windowedSignal;
   }

   public void setStepSize(float stepSize) {
      this.stepSize = stepSize;
   }

   public void setWindowSize(float windowSize) {
      this.windowSize = windowSize;
   }
   
   

   public abstract float getSingleWindowValue(Signal window);
   /*
    public List<Integer> topFivePercent() {
    List<Integer> top_five;
    // Copy The list ( to be optimized )
    List<Block> tmpList = new ArrayList<Block>(this.blocks);
    Collections.copy(tmpList, this.blocks);

    int five_percent = (int) Math.ceil((float) this.blocks.size() / 20);

    Collections.sort(tmpList,
    Collections.reverseOrder(Block.getComparator()));
    top_five = new ArrayList<Integer>();
    for (int i = 1; i <= five_percent; i++) {
    int block_index = tmpList.get(i - 1).index;
    this.blocks.get(block_index).selected = true;
    top_five.add(block_index);
    }

    return top_five;
    }

    */
}
