package com.dmp.signalanalyzer.configuration;

import java.util.Locale;

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
   private String inputFileSeparator = ",";
   private String outputFileSeparator = ",";
   private String outputFileExtension = "csv";
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

   public String getInputFileSeparator() {
      return inputFileSeparator;
   }

   public void setInputFileSeparator(String inputFileSeparator) {
      this.inputFileSeparator = inputFileSeparator;
   }

   public String getOutputFileSeparator() {
      return outputFileSeparator;
   }

   public void setOutputFileSeparator(String outputFileSeparator) {
      this.outputFileSeparator = outputFileSeparator;
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

      this.setInputFileSeparator(propertiesManager.getProperty("inputFileSeparator"));
      this.setOutputFileSeparator(propertiesManager.getProperty("outputFileSeparator"));
      this.setOutputFileExtension(propertiesManager.getProperty("outputFileExtension"));
      this.setBufferedReaderAsDefault(Boolean.getBoolean(propertiesManager.getProperty("bufferedReaderAsDefault")));
      
      this.setStepMultiplier(Float.valueOf(propertiesManager.getProperty("stepMultiplier")));
      this.setWindowsMultiplier(Float.valueOf(propertiesManager.getProperty("windowsMultiplier")));
   }
}
