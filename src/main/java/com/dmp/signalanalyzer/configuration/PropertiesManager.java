/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.signalanalyzer.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Paco
 */
public class PropertiesManager {

   private static final String propertyPrefix = "com.dmp.signalanalyzer";
   private static final String propertiesFile = "signal-analyzer.properties";
   private static PropertiesManager singletonInstance = null;

   public static PropertiesManager getInstance() {
      if (singletonInstance == null) {
         singletonInstance = new PropertiesManager();
      }
      return singletonInstance;
   }

   private PropertiesManager() {
      this.loadProperties();
   }

   public String getProperty(String propertyName) {
      return System.getProperty(propertyPrefix + propertyName);
   }

   /**
    * Load properties from a file and store them ad System properties if are not
    * yet defined
    */
   private void loadProperties() {
      try {
         // load default properties from file
         Properties defaultProperties = new Properties();
         InputStream defaultPropertiesStream = this.getClass().getClassLoader().getResourceAsStream(propertiesFile);
         defaultProperties.load(defaultPropertiesStream);

         // put loaded properties into System environment if they are not yet defined
         Set<String> propertiesNames = defaultProperties.stringPropertyNames();
         for (String property : propertiesNames) {
            if (property.startsWith(propertyPrefix)) {
               if (System.getProperty(property) == null) {
                  System.setProperty(property, defaultProperties.getProperty(property));
               }
            }
         }
      } catch (IOException ex) {
         Logger.getLogger(PropertiesManager.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
