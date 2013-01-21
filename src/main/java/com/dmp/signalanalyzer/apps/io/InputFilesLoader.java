package com.dmp.signalanalyzer.apps.io;

import com.dmp.signalanalyzer.apps.Main;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;


/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class InputFilesLoader {
   static final Logger logger = Logger.getLogger(Main.class.getName());
   
   public static double[] csvToDoubleArray(String csvFileName, int columnToGet, String separator) throws FileNotFoundException, IOException {
      logger.debug(String.format("Parsing %s input file to get column %s using separator %s",csvFileName,columnToGet,separator));
      List<Double> doubleOutList = new ArrayList<Double>();

      BufferedReader oReader = new BufferedReader(new FileReader(csvFileName));
      String nextLine;

      while ((nextLine = oReader.readLine()) != null) {
         String[] nextLineArray = nextLine.split(separator);
         try {
            doubleOutList.add(Double.valueOf(nextLineArray[columnToGet]));
         } catch (NumberFormatException ex) {
            logger.warn(String.format("The value %s cannot be converted to Float", nextLineArray[columnToGet]));
         }
      }
      logger.debug(String.format("Loaded %s items, converting list to float array and return it out",doubleOutList.size()));

      return listToDoubleArray(doubleOutList);
   }

   public static float[] csvToFloatArray(String csvFileName, int columnToGet, String separator) throws FileNotFoundException, IOException {
      logger.debug(String.format("Parsing %s input file to get column %s using separator %s",csvFileName,columnToGet,separator));
      List<Float> floatOutList = new ArrayList<Float>();

      BufferedReader oReader = new BufferedReader(new FileReader(csvFileName));
      String nextLine;

      while ((nextLine = oReader.readLine()) != null) {
         String[] nextLineArray = nextLine.split(separator);
         try {
            floatOutList.add(Float.valueOf(nextLineArray[columnToGet]));
         } catch (NumberFormatException ex) {
            logger.warn(String.format("The value %s cannot be converted to Float", nextLineArray[columnToGet]));
         }
      }
      logger.debug(String.format("Loaded %s items, converting list to float array and return it out",floatOutList.size()));

      return listToFloatArray(floatOutList);
   }
   
   public static int[] csvToIntegerArray(String csvFileName, int columnToGet, String separator) throws FileNotFoundException, IOException {
      logger.debug(String.format("Parsing %s input file to get column %s using separator %s",csvFileName,columnToGet,separator));
      List<Integer> floatOutList = new ArrayList<Integer>();

      BufferedReader oReader = new BufferedReader(new FileReader(csvFileName));
      String nextLine;

      while ((nextLine = oReader.readLine()) != null) {
         String[] nextLineArray = nextLine.split(separator);
         try {
            floatOutList.add(Integer.valueOf(nextLineArray[columnToGet]));
         } catch (NumberFormatException ex) {
            logger.warn(String.format("The value %s cannot be converted to Integer", nextLineArray[columnToGet]));
         }
      }
      logger.debug(String.format("Loaded %s items, converting list to float array and return it out",floatOutList.size()));

      return listToIntegerArray(floatOutList);
   }
   
   private static double[] listToDoubleArray(List<Double> inputList){
      double[] outArray = new double[inputList.size()];

      int i = 0;
      for (Double iDouble : inputList) {
         outArray[i++] = iDouble.floatValue();
      }

      return outArray;
   }

   private static float[] listToFloatArray(List<Float> inputList) {
      float[] outArray = new float[inputList.size()];

      int i = 0;
      for (Float iFloat : inputList) {
         outArray[i++] = iFloat.floatValue();
      }

      return outArray;
   }
   
   private static int[] listToIntegerArray(List<Integer> inputList) {
      int[] outArray = new int[inputList.size()];

      int i = 0;
      for (Integer iInteger : inputList) {
         outArray[i++] = iInteger.intValue();
      }

      return outArray;
   }
}
