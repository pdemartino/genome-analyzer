package com.dmp.signalanalyzer.apps.logic;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public enum Filter {

   lowpass("LowPass"),
   unbias("UnbiasFilter"),
   ninetiethPercSelector("NinetiethPercentSelector"),
   winninetiethPerc("WindowedNinetiethPercentileAnalysis");
   
   static String CHAIN_SEPARATOR = "-";
   String className;
   Filter(String className) {
      this.className = className;
   }
}
