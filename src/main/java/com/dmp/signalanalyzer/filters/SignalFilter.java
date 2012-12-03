package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.signal.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public abstract class SignalFilter {
    protected FilterConfiguration filterConfiguration = new FilterConfiguration();

    public String getName() {
        return getClass().getSimpleName();
    }

    public abstract Signal filter(Signal signal);
    public void setFilterConfiguration(FilterConfiguration configuration){this.filterConfiguration = configuration;}
}
