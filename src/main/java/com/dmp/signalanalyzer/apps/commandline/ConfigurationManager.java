package com.dmp.signalanalyzer.apps.commandline;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

public class ConfigurationManager {

   protected Logger logger = Logger.getLogger(getClass().getName());
   public static String multipleValuesSeparator = ",";
   private static final String propertyPrefix = "";
   private static final String propertiesFile = "signal-analyzer.properties";
   private static ConfigurationManager singleton = null;
   private Map<String, Object> configurationParameters = null;

   public static ConfigurationManager getInstance() {
      if (singleton == null) {
         singleton = new ConfigurationManager();
      }
      return singleton;
   }

   private ConfigurationManager() {
      loadProperties();
   }

   public void parseCommandLine(String[] argv) throws ParseException {
      CommandLine cmLine;
      CommandLineParser parser = new PosixParser();
      cmLine = parser.parse(buildOptions(), argv);

      this.configurationParameters = new HashMap<String, Object>();

      for (CommandLineOption option : CommandLineOption.values()) {
         System.out.println("Checking for " + option.name());
         if (cmLine.hasOption(option.name())) {
            String name = option.name();
            Object value;
            if (option.hasArguments()) {
               value =
                       cmLine.getOptionValue(name).contains(multipleValuesSeparator) || option.getMaxNumOfArguments() > 1
                       ? Arrays.asList(cmLine.getOptionValue(name).split(multipleValuesSeparator))
                       : cmLine.getOptionValue(name);

            } else {
               value = "true";
            }
            configurationParameters.put(name, value);
            // override property
            if (option.hasPropertyToOverride()) {
               System.setProperty(option.getPropertyToOverride(), value.toString());
            }

         }
      }
   }

   private void loadProperties() {
      try {
         // load default properties from file
         Properties defaultProperties = new Properties();
         InputStream defaultPropertiesStream = this.getClass().getClassLoader().getResourceAsStream(propertiesFile);
         defaultProperties.load(defaultPropertiesStream);

         // put loaded properties into System environment if they are not yet defined
         Set<String> propertiesNames = defaultProperties.stringPropertyNames();

         for (String property : propertiesNames) {
            if (PropertiesEnum.valueOf(property) != null) {
               if (System.getProperty(property) == null) {
                  System.setProperty(property, defaultProperties.getProperty(property));
               }
            }
         }
      } catch (IOException ex) {
         logger.error(ex.getMessage());
      }
   }

   public static void printHelp() {
      HelpFormatter helpFormatter = new HelpFormatter();
      helpFormatter.printHelp(120, "syntaxx", "header", buildOptions(), "footer");
   }

   private static Options buildOptions() {
      Options commandLineOptions = new Options();

      for (CommandLineOption option : CommandLineOption.values()) {
         commandLineOptions.addOption(
                 OptionBuilder
                 .hasOptionalArgs(option.getMaxNumOfArguments())
                 .withArgName(option.getArgumentName())
                 .isRequired(option.isMandatory())
                 .withDescription(option.getDescription())
                 .create(option.name()));
      }

      return commandLineOptions;
   }

   public Map<String, Object> getArguments() {
      return this.configurationParameters;
   }

   public Object getConfigurationValue(String parameterName) {
      if (this.configurationParameters.containsKey(parameterName)) {
         return this.configurationParameters.get(parameterName);
      }

      return System.getProperty(parameterName);

   }

   public boolean hasConfigurationValue(String parameterName) {
      return getConfigurationValue(parameterName) != null;
   }

   public static Object[] splitFileNameAndColumn(String optionValue) {
      String separator = ":";
      Object[] outArray = new Object[2]; // file and column
      String[] splittedValue = optionValue.split(separator);

      // set filename
      outArray[0] = splittedValue[0];
      if (outArray.length > 1) {
         try {
            outArray[1] = Integer.valueOf(splittedValue[1]);
            outArray[1] = Integer.valueOf(Math.max(0, (Integer) outArray[1]));
         } catch (NumberFormatException ex) {
            outArray[1] = Integer.valueOf(0);
         }
      } else {
         outArray[1] = Integer.valueOf(0);
      }

      return outArray;
   }
}
