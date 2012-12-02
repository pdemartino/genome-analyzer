package com.dmp.signalanalyzer.exceptions;

public class SignalLengthMismatch extends Exception {
   private static final String defaultMessage = "Positions and Signal lenghts do not match";
   
   
	public SignalLengthMismatch(String message) {
		super(message);
	}
   
   public SignalLengthMismatch(){
      super(defaultMessage);
   }

}
