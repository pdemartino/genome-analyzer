/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.signalanalyzer.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pasquale De Martino
 */
public class Utils {

    // Slices the array depending on its content, step and window size
    // It expects that the array is ordered
    public static List<int[]> sliceArray(double step, double window, List<Double> array) {
        List<int[]> slices = null;


        if ((array != null) && (array.size() > 0)) {
            slices = new ArrayList<int[]>();
            double subsetStart = array.get(0).doubleValue();
            double subsetStop = subsetStart + window;
            double maxStop = array.get(array.size() - 1).floatValue();

            // startIndex stores the start point during iteration
            // avoiding restarting from 0 every time
            int startIndex = 0;
            int i;
            int selected_start,selected_stop;
            boolean found = false;

            while (subsetStart <= maxStop) {
                // search lower bound of the subset
                // select first item >= start
                i = startIndex;
                found = false;
                while ((!found) && (i < array.size())) {
                    if (array.get(i) < subsetStart) {
                        i++;
                    } else {
                        found = true;
                    }
                }

                if (found) {
                    selected_start = i;
                    startIndex = selected_start; //save for next iteration

                    // search upper bound of the subset
                    // select last item <= stop
                    i = array.size() - 1;
                    found = false;
                    while ((!found) && (i >= subsetStart)) {
                        if (array.get(i) > subsetStop) {
                            i--;
                        } else {
                            found = true;
                        }
                    }
                    selected_stop = i;

                    //add new slice into return variable
                    slices.add(new int[]{selected_start, selected_stop});

                    //increase
                    subsetStart += step;
                    subsetStop = subsetStart + window;

                } else {
                    // create while exit condition
                    subsetStart = maxStop + 1;
                }
            }
        }
        return slices;
    }


}
