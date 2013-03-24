package com.dmp.signalanalyzer.apps.io;

import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.signal.RecombinationMap;
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
public class OutputManager {

   private final Logger logger = Logger.getLogger(OutputManager.class.getName());
   private final String DATE_FORMAT_NOW = "yyyyMMdd";
   private Calendar cal = Calendar.getInstance();
   private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
   private String directory = ".";
   private String fileExtension = "csv";
   private String valueSeparator = ",";
   private Boolean appendDate = false;
   private RecombinationMap recombinationMap;

   public void writeToFile(Signal signal, String filter, boolean selected) throws IOException {
      writeToFile(signal, filter, selected, true);
   }

   public void writeToFile(Signal signal, String filter, boolean selected, boolean bufferedWriting) throws IOException {
      String newLine = "\n";
      String separator = valueSeparator;

      String filePath = getFilePath(filter, selected);
      logger.info(String.format("Writing %s items into %s...", signal.count(), filePath));
      Writer fwr = new FileWriter(filePath);

      if (bufferedWriting) {
         fwr = new BufferedWriter(fwr);
      }

      // Print Header

      fwr.write("Position");
      if (recombinationMap != null) {
         fwr.write(separator + "GeneticMap");
      }
      fwr.write(separator + "Value");


      for (Signal p : signal) {
         fwr.write(newLine);
         if (recombinationMap != null) {
            Double position = recombinationMap.getPosition(p.getTime());
            fwr.write(position != null ? position.toString() : "");
            fwr.write(separator);
         }
         fwr.write(String.valueOf(p.getTime()));
         fwr.write(separator + String.valueOf(p.getValue()));
      }
      logger.debug(String.format("Written %s lines", signal.count()));
      fwr.close();
   }

   private String getFilePath(String filter, boolean selected) {
      String finalDirectory = casDirectory(filter);
      if (finalDirectory != null) {
         return String.format("%s%s.%s",
                 finalDirectory + File.separator + "filtered",
                 (selected ? "-selected" : ""),
                 fileExtension);
      } else {
         return null;
      }

   }

   public String getDirectory() {
      return directory;
   }

   public void setDirectory(String directory) {
      this.directory = directory;
   }

   public String getValueSeparator() {
      return valueSeparator;
   }

   public void setValueSeparator(String valueSeparator) {
      this.valueSeparator = valueSeparator;
   }

   public RecombinationMap getRecombinationMap() {
      return recombinationMap;
   }

   public void setRecombinationMap(RecombinationMap recombinationMap) {
      this.recombinationMap = recombinationMap;
   }

   public Boolean getAppendDate() {
      return appendDate;
   }

   public String getFileExtension() {
      return fileExtension;
   }

   public void setFileExtension(String fileExtension) {
      this.fileExtension = fileExtension;
   }
   
   

   public void setAppendDate(Boolean appendDate) {
      this.appendDate = appendDate;
   }

   private String casDirectory(String filter) {
      String finalDirectory = directory + File.separator + filter;
      if (appendDate) {
         finalDirectory = String.format("%s_%s",
                 finalDirectory,
                 sdf.format(cal.getTime()));
      }
      File directoryObj = new File(finalDirectory);
      if (directoryObj.isDirectory() || directoryObj.mkdirs()){
         return finalDirectory;
      }
      return null;
   }
}