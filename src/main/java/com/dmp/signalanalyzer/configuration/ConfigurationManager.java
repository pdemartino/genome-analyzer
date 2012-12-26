package com.dmp.signalanalyzer.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class ConfigurationManager {

   private static ConfigurationManager singleInstance = null;
   private PropertiesManager propertiesManager;
   private Locale defaultLocale = Locale.US;
   private int numAnalysis = 8;
   private float stepMultiplier = 1f / 2f; // half a time a window
   private float windowsMultiplier = 1f / 20f;
   private char inputFileSeparatorChar = ' ';
   private char outputFileSeparatorChar = ' ';
   private String outputFileExtension = ".csv";
   private static boolean bufferedReaderAsDefault = false;

   public static ConfigurationManager getInstance() {
      if (singleInstance == null) {
         singleInstance = new ConfigurationManager();
      }
      return singleInstance;
   }

   private ConfigurationManager() {
      this.propertiesManager = PropertiesManager.getInstance();
      this.loadConfiguration();
   }

   public Locale getDefaultLocale() {
      return defaultLocale;
   }

   public void setDefaultLocale(Locale defaultLocale) {
      this.defaultLocale = defaultLocale;
   }

   public float getStepMultiplier() {
      return stepMultiplier;
   }

   public void setStepMultiplier(float stepMultiplier) {
      this.stepMultiplier = stepMultiplier;
   }

   public float getWindowsMultiplier() {
      return windowsMultiplier;
   }

   public void setWindowsMultiplier(float windowsMultiplier) {
      this.windowsMultiplier = windowsMultiplier;
   }

   public char getInputFileSeparatorChar() {
      return inputFileSeparatorChar;
   }

   public void setInputFileSeparatorChar(char inputFileSeparatorChar) {
      this.inputFileSeparatorChar = inputFileSeparatorChar;
   }

   public char getOutputFileSeparatorChar() {
      return outputFileSeparatorChar;
   }

   public void setOutputFileSeparatorChar(char outputFileSeparatorChar) {
      this.outputFileSeparatorChar = outputFileSeparatorChar;
   }

   public String getOutputFileExtension() {
      return outputFileExtension;
   }

   public void setOutputFileExtension(String outFileExtension) {
      this.outputFileExtension = outFileExtension;
   }

   public boolean isBufferedReaderAsDefault() {
      return bufferedReaderAsDefault;
   }

   public void setBufferedReaderAsDefault(boolean bufferedReaderAsDefault) {
      ConfigurationManager.bufferedReaderAsDefault = bufferedReaderAsDefault;
   }

   public int getAnalysisNumber() {
      return this.numAnalysis;
   }

   private void loadConfiguration() {
      this.setDefaultLocale(Locale.US);

      this.setInputFileSeparatorChar(propertiesManager.getProperty("inputFileSeparatorChar").charAt(0));
      this.setOutputFileSeparatorChar(propertiesManager.getProperty("outputFileSeparatorChar").charAt(0));
      this.setOutputFileExtension(propertiesManager.getProperty("outputFileExtension"));
      this.setBufferedReaderAsDefault(Boolean.getBoolean(propertiesManager.getProperty("bufferedReaderAsDefault")));
      
      this.setStepMultiplier(Float.valueOf(propertiesManager.getProperty("stepMultiplier")));
      this.setWindowsMultiplier(Float.valueOf(propertiesManager.getProperty("windowsMultiplier")));
   }
}
