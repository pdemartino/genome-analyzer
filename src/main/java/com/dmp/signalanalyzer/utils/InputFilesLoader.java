package com.dmp.signalanalyzer.utils;

import au.com.bytecode.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class InputFilesLoader {

   public static float[] csvToFloatArray(String csvFileName, int columnToGet, char separator) throws FileNotFoundException, IOException {
      List<Float> floatOutList = new ArrayList<Float>();
      
      
      CSVReader oReader = new CSVReader(new FileReader(csvFileName), separator);
      String[] nextLine;
      
      while ((nextLine = oReader.readNext()) != null) {
         floatOutList.add(new Float(nextLine[columnToGet]));
      }
      
      return listToFloatArray(floatOutList);
   }
   
   private static float[] listToFloatArray(List<Float> inputList){
      float[] outArray = new float[inputList.size()];
      
      int i=0;
      for (Float iFloat : inputList){
         outArray[i++] = iFloat.floatValue();
      }
      
      return outArray;
   }
}
