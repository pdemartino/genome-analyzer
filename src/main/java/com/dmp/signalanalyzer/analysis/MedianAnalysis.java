/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.signalanalyzer.analysis;

import com.dmp.signalanalyzer.Block;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author paco
 */
public class MedianAnalysis extends ASignalAnalysis {

    public MedianAnalysis() {
        this.name = "Median";
    }
    
    

    @Override
    public void runAnalysis() {
        this.setSlices();
        // compute ninth percentile for each block
        for (Block tmpBlock : this.blocks){
            
            int block_len = tmpBlock.stop - tmpBlock.start + 1;
                   
            //subList
            //"Returns a view of the portion of this list between the specified 
            //fromIndex, inclusive, and toIndex, exclusive. "
            List<Float> sliceValues = this.signal.subList(tmpBlock.start, tmpBlock.stop + 1);
            List<Float> sliceValuesCopy = new ArrayList(this.signal.subList(tmpBlock.start, tmpBlock.stop + 1));
            Collections.copy(sliceValuesCopy, sliceValues);
            
            // sort
            //Collections.sort(sliceValuesCopy);
            // compute ten percent 
            //int fifty_percent = Math.round(block_len / 2); //block_len *50 /100
            
            
            // ninth percentile is the value of the gre
            float median = Median(sliceValuesCopy);
            
            tmpBlock.value = median;
                    
        }
        
    }
    
    private static float Median(List<Float> values) {
        Collections.sort(values);

        if (values.size() % 2 == 1) {
            return values.get((values.size() + 1) / 2 - 1).floatValue();
        } else {
            float lower = values.get(values.size() / 2 - 1);
            float upper = values.get(values.size() / 2);

            return (lower + upper) / 2f;
        }
    }
    

    
  
    
}
