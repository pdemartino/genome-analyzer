/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dmp.signalanalyzer.filters.windowed;

import com.dmp.signalanalyzer.signal.Signal;

/**
 *
 * @author pdemartino
 */
public class WindowedDensity extends WindowedSignalFilter{

   @Override
   public double getSingleWindowValue(Signal window) {
      return window.count();
   }
   
}
