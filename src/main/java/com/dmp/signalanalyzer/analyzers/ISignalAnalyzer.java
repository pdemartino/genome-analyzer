package com.dmp.signalanalyzer.analyzers;

/**
*
* @author Pasquale De Martino <paco.dmp@gmail.com>
*/
public interface ISignalAnalyzer {
	
	public String getName();
	public void runAnalysis();
	public Object getAnalysis();
}
