package com.dmp.signalanalyzer.apps.io;

import com.dmp.signalanalyzer.configuration.ConfigurationManager;
import com.dmp.signalanalyzer.signal.Signal;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.Logger;


/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class SignalIoManager {

   private static final Logger logger = Logger.getLogger(SignalIoManager.class.getName());
   private static final String DATE_FORMAT_NOW = "yyyyMMdd";
   private static Calendar cal = Calendar.getInstance();
   private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);

   public static void writeToFile(Signal signal, String outputDirectory, String analysisName, String fileNameAppend) throws IOException {
      ConfigurationManager configurationManager = ConfigurationManager.getInstance();
      SignalIoManager.writeToFile(signal, outputDirectory, analysisName, fileNameAppend, configurationManager.isBufferedReaderAsDefault());
   }

   public static void writeToFile(Signal signal, String outputDirectory, String analysisName, String fileNameAppend, boolean bufferedWriting) throws IOException {
      String newLine = "\n";
      String separator = ConfigurationManager.getInstance().getOutputFileSeparator();
      
      String filePath = getFilePath(outputDirectory, analysisName, fileNameAppend);
      logger.info(String.format("Writing %s items into %s..." ,signal.count(),filePath));
      Writer fwr = new FileWriter(filePath);

      if (bufferedWriting) {
         fwr = new BufferedWriter(fwr);
      }

      // Print Header
      fwr.write("WindowID");
      fwr.write(separator + "Size");
      fwr.write(separator + "Start_Position");
      fwr.write(separator + "Stop_Position");
      fwr.write(separator + "Center");
      fwr.write(separator + "Value");

      int i = 0;
      for (Signal p : signal) {
         fwr.write(newLine);
         fwr.write(String.valueOf(++i));
         fwr.write(separator + String.valueOf(p.getTStop() - p.getTStart()));
         fwr.write(separator + String.valueOf(p.getTStart()));
         fwr.write(separator + String.valueOf(p.getTStop()));
         fwr.write(separator + String.valueOf(p.getTime()));
         fwr.write(separator + String.valueOf(p.getValue()));
      }
      logger.debug(String.format("Written %s lines",i));
      fwr.close();
   }

   private static String getFilePath(String outputDirectory, String analysisName, String fileNameAppend) {
      String fileName =
              String.format("%s_%s.%s",
              outputDirectory + File.separator + analysisName + fileNameAppend,
              sdf.format(cal.getTime()),
              ConfigurationManager.getInstance().getOutputFileExtension());
              
      int attempt = 0;
      while ((new File(fileName)).exists()) {
         logger.warn(String.format("%s already exists", fileName));
         fileName = String.format("%s_%s_%s.%s",
              outputDirectory + File.separator + analysisName + fileNameAppend,
              sdf.format(cal.getTime()),
              ++attempt,
              ConfigurationManager.getInstance().getOutputFileExtension());
      }
      
      return fileName;
   }
}
