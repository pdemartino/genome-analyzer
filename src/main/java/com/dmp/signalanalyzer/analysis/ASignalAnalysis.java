/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.signalanalyzer.analysis;

import com.dmp.signalanalyzer.Block;
import com.dmp.signalanalyzer.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author paco
 */
public abstract class ASignalAnalysis {
    protected String name; // name of the Analysis
    
    protected List<Double> positions = null;
    protected List<Float> signal = null;
    protected double step, window;
    
    protected List<Block> blocks = null;
    protected List<Integer> overBlocks = null;
    
   
    public abstract void runAnalysis();
    
    public List<Integer> topFivePercent(){
        List <Integer> top_five;
         // Copy The list ( to be optimized )
        List<Block> tmpList  = new ArrayList<Block>(this.blocks);
        Collections.copy(tmpList, this.blocks);
        
        int five_percent = (int)Math.ceil((float)this.blocks.size() / 20);
              
        
        Collections.sort(tmpList,Collections.reverseOrder(Block.getComparator()));
        top_five = new ArrayList<Integer>();
        for (int i=1; i<= five_percent; i++){
            int block_index = tmpList.get(i-1).index;
            this.blocks.get(block_index).selected = true;
            top_five.add(block_index);
        }
        
        return top_five;
    }
    
     protected void setSlices(){
        // 1 Define blocks' bounds
        List<int[]> slices = Utils.sliceArray(this.step,this.window,this.positions);
        this.blocks = new ArrayList<Block>();
        
        int i=0;
        for (int[] slice : slices){
            Block tmpBlock = new Block();
            tmpBlock.index = i;
            tmpBlock.start = slice[0];
            tmpBlock.stop = slice[1];
            tmpBlock.start_position = this.positions.get(tmpBlock.start);
            tmpBlock.stop_position = this.positions.get(tmpBlock.stop);
            this.blocks.add(tmpBlock);
            i++;
        }
        
    }
   
    
    public String getName(){
        return this.name;
    }
    
    public List<Block> getBlocks(){
        return this.blocks;
    }

    public List<Integer> getOverBlocks() {
        return overBlocks;
    }
   
    public List<Double> getPositions() {
        return positions;
    }

    public void setPositions(List<Double> positions) {
        this.positions = positions;
    }

    public List<Float> getSignal() {
        return signal;
    }

    public void setSignal(List<Float> signal) {
        this.signal = signal;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public double getWindow() {
        return window;
    }

    public void setWindow(double window) {
        this.window = window;
    }
    
    
    
    
    
    
    
}
