/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.signalanalyzer.analysis;

import com.dmp.signalanalyzer.Block;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author paco
 */
public class MeanAnalysis extends ASignalAnalysis {
    
    private float totalMean;

    public MeanAnalysis() {
        this.name = "Mean";
    }
    
    
    
    
    public void runAnalysis(){
       this.setSlices();
        // compute mean for each block
        
        for (Block tmpBlock : this.blocks){
            
            float sum = 0f;
            for (int i = tmpBlock.start; i<= tmpBlock.stop; i++){
                float val = this.getSignal().get(i).floatValue();
                if (Float.isNaN(val)){
                    val = 0;
                }
                sum+= val;
            }
            //I have to add 1 because left bound belongs to block
            //I use a float variable to avoid integer truncation later during rate
            float number_of_items = tmpBlock.stop - tmpBlock.start + 1;
            float mean = sum / number_of_items;
            tmpBlock.value = mean;
            
        }
        
        
    }
    
}
    
   
