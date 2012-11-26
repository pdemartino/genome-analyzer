package com.dmp.signalanalyzer.utils;

import java.util.Locale;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class SignalAnalyzerConstants {

   public static final Locale DEFAULT_LOCALE = Locale.US;
   public static final int NUM_ANALYSIS = 8;
   public static final float STEP_MULT = 1f / 40f;
   public static final float WINDOW_MULT = 1f / 20f;
   public static String CSV_SEPARATOR = ",";
   public static String CSV_EXTENSION = ".csv";
   public static boolean DEFAULT_BUFFERED_WRITE = false;
}
