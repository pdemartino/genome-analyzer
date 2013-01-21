package com.dmp.signalanalyzer.utils;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class SAMath {
   
   /**
    * Returns a normalized value using min max method
    * @param value
    * @param min
    * @param max
    * @return
    */
   public static float minMaxNormalization(float value, float min, float max){
      float range = max - min;
      float normalized = (value - min) / range;
      return normalized;
   }
   
   public static float average(float... values){
      int count = 0;
      float sum = 0;
      
      for (float value : values){
         sum += value;
         count++;
      }
      
      return count==0 ? 0 : sum / count;
   }
   
   public static float min(float... values){
      float min = Float.MAX_VALUE;
      for (float value : values){
         min = Math.min(min, value);
      }
      return min;
   }
   
   public static float max(float... values){
      float max = -1 * Float.MAX_VALUE;
      for (float value : values){
         max = Math.max(max, value);
      }
      return max;
   }
}
