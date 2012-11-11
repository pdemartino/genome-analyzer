package com.dmp.signalanalyzer.analyzers;

import com.dmp.signalanalyzer.beans.PositionSortedSignal;
import com.dmp.signalanalyzer.beans.Slice;
import com.dmp.signalanalyzer.beans.SlicedSignal;
import java.util.Iterator;

/**
 * 
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public abstract class ASlicedSignalAnalyzer implements ISignalAnalyzer{
	protected SlicedSignal slicedSignal;

	public ASlicedSignalAnalyzer(PositionSortedSignal signal, double sliceSize, double sliceStep){
		this.slicedSignal = new SlicedSignal(signal, sliceSize, sliceStep);
	}
    
	public void runAnalysis(){
    	// compute mean for each block
    	Iterator<Slice> slicesIterator  = this.slicedSignal.iterator();
        while(slicesIterator.hasNext()){
            Slice slice = slicesIterator.next();
            slice.setValue(getSingleSliceValue(slice));
        }
    }
	
	public abstract float getSingleSliceValue(Slice slice);
	
    public SlicedSignal getAnalysis(){
    	return this.getSlicedSignal();
    }
    
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
	
	public SlicedSignal getSlicedSignal() {
		return slicedSignal;
	}

}
