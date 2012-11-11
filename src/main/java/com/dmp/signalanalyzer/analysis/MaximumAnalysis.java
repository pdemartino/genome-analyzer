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
public class MaximumAnalysis extends ASignalAnalysis{

    public MaximumAnalysis() {
        this.name = "Maximum";
    }
    
    
    
    
    public void runAnalysis(){
       this.setSlices();
        
        for (Block tmpBlock : this.blocks){
            
            float max = 0f;
            for (int i = tmpBlock.start; i<= tmpBlock.stop; i++){
                if (this.signal.get(i) > max){
                    max = this.signal.get(i);
                }
            }
            tmpBlock.value = max;
            
        }
    }
    
    
}
