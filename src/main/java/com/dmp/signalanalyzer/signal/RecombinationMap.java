package com.dmp.signalanalyzer.signal;

import com.dmp.signalanalyzer.exceptions.SignalLengthMismatch;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.apache.log4j.Logger;


/**
 *
 * @author Pasquale De Martino
 */
public class RecombinationMap {
    TreeBidiMap recombinationMap;
    Logger logger = Logger.getLogger(this.getClass());

    public RecombinationMap(Double[] positions, Double[] recombination) throws SignalLengthMismatch {
        if (positions != null && recombination != null 
                && positions.length != recombination.length) {
            throw new SignalLengthMismatch("Positions and recombination arrays are not the same length");
        } else {
            recombinationMap = new TreeBidiMap();
            for (int i = 0 ; i< positions.length; i++){
                recombinationMap.put(positions[i], recombination[i]);
            }
            
        }
    }

    public Double getPosition(Double recombination) {
        return (Double) recombinationMap.getKey(recombination);
    }

    public Double getRecombination(Double position) {
        return (Double) recombinationMap.get(position);
    }

    public Signal applyRecombinationMap(Signal signal) {
        Signal recombinatedSignal = new Signal();

        for (Signal component : signal) {
            Double recombination = getRecombination(component.getTime());

            if (recombination != null) {
                recombinatedSignal.addComponent(new Signal(recombination, component.getValue()));
            }else{
                logger.warn(
                        String.format("Cannot find recombination value for %s, it will not be inserted into recombinated signal"
                        ,component.getTime()));
            }
        }
        return recombinatedSignal;
    }
    
    public Signal revertRecombination(Signal signal){
        Signal revertedSignal = new Signal();

        for (Signal component : signal) {
            Double position = getPosition(component.getTime());

            if (position != null) {
                revertedSignal.addComponent(new Signal(position, component.getValue()));
            }else{
                logger.warn(
                        String.format("Cannot find recombination value for %s, it will not be inserted into reverted signal"
                        ,component.getTime()));
            }
        }
        return revertedSignal;
    }
}
