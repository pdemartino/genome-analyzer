/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.signalanalyzer;

import java.util.Comparator;

/**
 *
 * @author pasquale
 */
public class Block {
    public int start,stop; //bounds
    public double start_position, stop_position;
    public float value;
    public int index;
    public boolean selected = false;

   
    
    public String toString(){
        return "#" + index + "\t["+ start + "->" + stop + "] "+ "\t["+ start_position + "->" + stop_position + "] "+ "\t("+ value+")";
    }
    
    public static Comparator getComparator(){
        return new BlocksComparator();
    }
    
    public double middle(){
        return (this.stop_position +  this.start_position) /2;
    }
    
    public double distance_from_selection(double selection_position){
        return Math.abs(this.middle() - selection_position);
    }
    
    
}

class BlocksComparator implements Comparator<Block>{

    @Override
    public int compare(Block o1, Block o2) {
        if(o1.value > o2.value)
            return 1;
        else if(o1.value < o2.value)
            return -1;
        else
            return 0;
    }
    
}
