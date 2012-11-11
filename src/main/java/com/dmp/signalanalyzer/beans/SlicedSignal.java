package com.dmp.signalanalyzer.beans;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SlicedSignal {

    private List<Slice> slices = new ArrayList<Slice>();
    private double sliceSize = 0d, sliceStep = 0d;

    public SlicedSignal(PositionSortedSignal wholeSignal, double sliceSize, double sliceStep) {
        this.sliceSize = sliceSize;
        this.sliceStep = sliceStep;

        double minPosition = wholeSignal.getStartPosition().doubleValue();
        double maxPosition = wholeSignal.getStopPosition().doubleValue();

        double start = minPosition;
        double stop;
        Iterator<Pulse> signalIterator = wholeSignal.iterator();

        // Create Slices
        while (start <= maxPosition) {
            stop = Math.min(start + sliceSize, maxPosition);
            Slice slice = new Slice(start, stop);
            this.slices.add(slice);
            start = start + sliceStep;
        }

        // Fill Slices
        int startSlice = 0;
        boolean inserted;
        while (signalIterator.hasNext()) {
            inserted = false;
            Pulse pulse = signalIterator.next();

            for (int i = startSlice; i < slices.size(); i++) {
                Slice slice = slices.get(i);
                if (!slice.addPulseIfCanContain(pulse)){
                    if (!inserted){
                        startSlice = i + 1;
                    }else{
                        break;
                    }
                }else{
                    inserted = true;
                }
            }
        }
    }

    public int size() {
        return this.slices.size();
    }

    public double getSliceSize() {
        return sliceSize;
    }

    public double getSliceStep() {
        return sliceStep;
    }

    @Override
    public String toString() {
        String outString = "{";

        for (Slice slice : this.slices) {
            outString += String.format("\n\t%s", slice);
        }

        outString += "\n}";
        return outString;
    }

    public void writeToCSV(String csvFileName) throws IOException {
        String csvSeparator = ",";

        FileWriter fstream = new FileWriter(csvFileName);
        BufferedWriter out = new BufferedWriter(fstream);

        for (Slice slice : slices) {
            out.write(
                    slice.getSliceCenter() + csvSeparator
                    + slice.getValue());
            out.newLine();
        }

        out.close();
    }

    public Iterator<Slice> iterator() {
        return this.slices.iterator();
    }
    
//    private void markTopFivePercent() {
//        int fivePercent = (int) Math.ceil(this.slices.size() / 20);
//        
//        
//        Slice[] topFive = new Slice[fivePercent];
//        
//        // Init 
//        for (int i = 0; i < fivePercent; i++){
//            topFive[i] = this.slices.get(i);
//        }
//        
//        // The rest of the slices
//        for (int j = fivePercent; j< size(); j++){
//            Slice slice = this.slices.get(j);
//            for (int i = 0; i < fivePercent; i++){
//                if (slice.vatopFive[i].getValue())
//            }
//        }
//        
//        
//		List<Integer> top_five;
//		// Copy The list ( to be optimized )
//		List<Block> tmpList = new ArrayList<Block>(this.blocks);
//		Collections.copy(tmpList, this.blocks);
//
//		
//
//		Collections.sort(tmpList,
//				Collections.reverseOrder(Block.getComparator()));
//		top_five = new ArrayList<Integer>();
//		for (int i = 1; i <= five_percent; i++) {
//			int block_index = tmpList.get(i - 1).index;
//			this.blocks.get(block_index).selected = true;
//			top_five.add(block_index);
//		}
//
//		return top_five;
//	}
}
