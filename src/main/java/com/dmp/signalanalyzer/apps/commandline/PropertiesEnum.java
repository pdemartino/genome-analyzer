package com.dmp.signalanalyzer.apps.commandline;

/**
 *
 * @author pdemartino
 */
public enum PropertiesEnum {

   step,
   window,
   smoothingFactor,
   normalizeUsingPosition,
   backward,
   twoWay,
   skipInputHeader,
   inputFileSeparator,
   outputFileSeparator,
   outputFileExtension,
   bufferedReaderAsDefault,
   outputDirectory;
   
   private static String propertyPrefix = "";
   public String getPropertyName(){
      return propertyPrefix + this.name();
   }
}
