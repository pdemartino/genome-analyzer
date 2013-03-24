package com.dmp.signalanalyzer.apps.logic;

import com.dmp.signalanalyzer.filters.FilterConfiguration;
import com.dmp.signalanalyzer.filters.NinetiethPercentSelector;
import com.dmp.signalanalyzer.filters.SignalFilter;
import com.dmp.signalanalyzer.signal.Signal;
import org.apache.log4j.Logger;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class FilterRunner {
   protected Logger logger = Logger.getLogger(getClass().getName());
   FilterConfiguration filterConfiguration;
   SignalFilter selector = new NinetiethPercentSelector();

   public Signal run(Signal inputSignal, String filters) {
      String[] chain = filters.split(Filter.CHAIN_SEPARATOR);

      Signal outSignal = null;
      for (String filter : chain) {
         Filter filterClass = Filter.valueOf(filter);
         if (filterClass != null) {
            SignalFilter filterObject;
            try {
               filterObject = (SignalFilter) Class.forName(filterClass.className).cast(SignalFilter.class);
               filterObject.setFilterConfiguration(filterConfiguration);
               outSignal = filterObject.filter(outSignal != null ? outSignal : inputSignal);
            } catch (ClassNotFoundException ex) {
               notifyUnExistingFilter(filter);
            }
         }else{
            notifyUnExistingFilter(filter);
         }
      }
      return outSignal;
   }
   
   public Signal parseSelected(Signal filteredSignal){
      return selector.filter(filteredSignal);
   }
   
   public void setFilterConfiguration(FilterConfiguration filterConfiguration){
      this.filterConfiguration = filterConfiguration;
   }
   
   private void notifyUnExistingFilter(String filter){
      logger.error(String.format("Cannot apply filter %s because it does not exist!",filter));
   }
   
   
}
