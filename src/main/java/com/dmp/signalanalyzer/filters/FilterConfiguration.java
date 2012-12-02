package com.dmp.signalanalyzer.filters;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class FilterConfiguration {
    Map<String,Object> configurationParameters;

    public FilterConfiguration() {
        this.configurationParameters = new HashMap<String, Object>();
    }
    
    public void defineParameter(String parameterName, Object defaultValue){
        this.configurationParameters.put(parameterName, defaultValue);
    }
    
    public void set(String parameterName, Object value){
        if (configurationParameters.containsKey(parameterName)){
            this.configurationParameters.put(parameterName, value);
        }
    }
    
    public Object get(String parameterName){
        if (configurationParameters.containsKey(parameterName)){
            return configurationParameters.get(parameterName);
        }
        return null;
    }
}
