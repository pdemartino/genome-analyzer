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
public class NinetiethPercentileAnalysis extends ASignalAnalysis {

    public NinetiethPercentileAnalysis() {
        this.name = "NinetiehPercentile";
    }

    @Override
    public void runAnalysis() {
        this.setSlices();
        // compute ninth percentile for each block
        for (Block tmpBlock : this.blocks) {

            int block_len = tmpBlock.stop - tmpBlock.start + 1;

            //subList
            //"Returns a view of the portion of this list between the specified 
            //fromIndex, inclusive, and toIndex, exclusive. "
            List<Float> sliceValues = this.signal.subList(tmpBlock.start, tmpBlock.stop + 1);
            List<Float> sliceValuesCopy = new ArrayList(this.signal.subList(tmpBlock.start, tmpBlock.stop + 1));
            Collections.copy(sliceValuesCopy, sliceValues);

            // sort
            Collections.sort(sliceValuesCopy);
            // compute ten percent 
            int ten_percent = block_len / 10; //block_len *10 /100


            // ninth percentile is the value of the gre
            float ninth_percentile = sliceValuesCopy.get(block_len - ten_percent - 1);

            tmpBlock.value = ninth_percentile;

        }

    }

   
}
