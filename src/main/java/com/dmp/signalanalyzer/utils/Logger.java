package com.dmp.signalanalyzer.utils;

import java.util.Calendar;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class Logger {
    public enum LOGLEVEL {DEBUG,INFO};
    
    private LOGLEVEL logLevel;
    
    public void debug(String msg){
        if (this.logLevel.equals(LOGLEVEL.DEBUG)){
            printMessage(msg,LOGLEVEL.DEBUG);
        }
    }
    
    public void info(String msg){
        printMessage(msg,LOGLEVEL.INFO);
    }
    
    private static void printMessage(String msg, LOGLEVEL loglevel){
        String logMessage = String.format("[%s](%s) %s", Calendar.getInstance().getTime(), loglevel,msg);
        System.out.println(logMessage);
    }
    
    public void setLogLevel(LOGLEVEL logLevel) {
        this.logLevel = logLevel;
    }
    
    
}
