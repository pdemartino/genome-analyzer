/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.signalanalyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author paco
 */
public  class InputLoader {
    

    
    public static List<Double> loadPositions(String filename) throws FileNotFoundException{
        List<Double> positions = new ArrayList<Double>();
        
        
        
        //openfile
        Scanner scanner = new Scanner(new FileInputStream(filename));

        
        while (scanner.hasNextFloat()){
            //each line is a new individual
        
            //load individual genotype
            Double posVal= scanner.nextDouble();
            
            positions.add(posVal);
        }
        scanner.close();
        
        return positions;

    }
    
    public static List<Float> loadFst(String filename) throws FileNotFoundException{
        List<Float> values= new ArrayList<Float>();
        
        
        
        //openfile
        Scanner scanner = new Scanner(new FileInputStream(filename));

        
        while (scanner.hasNextFloat()){
            //load individual genotype
            Float val= scanner.nextFloat();
            if (val.isNaN())
                val = 0f;
            
            values.add(val);
        }
        scanner.close();
        
        return values;
    }
}
