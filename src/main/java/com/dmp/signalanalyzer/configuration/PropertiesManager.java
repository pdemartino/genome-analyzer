package com.dmp.signalanalyzer.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class PropertiesManager {
   protected Logger logger = Logger.getLogger(getClass().getName());
   private static final String propertyPrefix = "";
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
         logger.error(ex.getMessage());
      }
   }
}
