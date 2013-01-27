package com.dmp.signalanalyzer;

import com.dmp.signalanalyzer.exceptions.SignalLengthMismatch;
import com.dmp.signalanalyzer.signal.RecombinationMap;
import com.dmp.signalanalyzer.signal.Signal;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Pasquale De Martino
 */
public class RecombinationMapTest extends TestCase {

    public void testLengthMismatch() {
        RecombinationMap underTest;

        int numberOfComponents = 100;
        Double[] positions = getRandomArray(numberOfComponents);
        Double[] recombination = getRandomArray(2 * numberOfComponents);

        try {
            underTest = new RecombinationMap(positions, recombination);
            assertFalse("SignalLengthMismatch exception not thrown", true);
        } catch (SignalLengthMismatch ex) {
            assertTrue(true);
        }

    }

    public void testRecombinationMapDefinition() {
        RecombinationMap underTest;

        int numberOfComponents = 100;
        Double[] positions = getRandomArray(numberOfComponents);
        Double[] recombination = getRandomArray(numberOfComponents);

        try {
            underTest = new RecombinationMap(positions, recombination);

            for (int i = 0; i < positions.length; i++) {
                Double positionVal = positions[i];
                Double recombinationVal = recombination[i];

                assertEquals("Wrong recombination for pos " + positionVal,
                        recombinationVal, underTest.getRecombination(positionVal));
                assertEquals("Wrong position for recombination " + recombinationVal,
                        positionVal, underTest.getPosition(recombinationVal));
            }

        } catch (SignalLengthMismatch ex) {
            assertFalse("UnexpectedException", true);
        }


    }

    public void testApplyRecombinationMap() throws SignalLengthMismatch {
        RecombinationMap underTest;

        int numberOfComponents = 100;
        Double[] positions = generateIncrementalArray(numberOfComponents, 1.);
        Double[] recombination = generateIncrementalArray(numberOfComponents, 2.);
        Double[] values = getRandomArray(numberOfComponents);
        Signal signal = new Signal();
        signal.addComponentsArray(values, positions);

        underTest = new RecombinationMap(positions, recombination);
        Signal recombinatedSignal = underTest.applyRecombinationMap(signal);

        assertEquals(signal.count(), recombinatedSignal.count());
        int i = 0;
        for (Signal component : recombinatedSignal) {
            assertEquals(recombination[i], component.getTime());
            i++;
        }
        
        // revert
        Signal revertedSignal = underTest.revertRecombination(recombinatedSignal);
        assertEquals(signal.count(), recombinatedSignal.count());
        i = 0;
        for (Signal component : revertedSignal) {
            assertEquals(positions[i], component.getTime());
            i++;
        }
        
    }

    private Double[] getRandomArray(int len) {
        Random rndObj = new Random();
        Double[] rndArray = new Double[len];

        for (int i = 0; i < len; i++) {
            rndArray[i] = Double.valueOf(rndObj.nextDouble());
        }
        return rndArray;
    }

    private Double[] generateIncrementalArray(int len, double increment) {
        Double[] incrArray = new Double[len];
        double start = 0.;

        for (int i = 0; i < len; i++) {
            start += increment;
            incrArray[i] = start;
        }

        return incrArray;
    }
}
