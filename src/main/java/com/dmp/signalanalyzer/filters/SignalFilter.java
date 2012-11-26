package com.dmp.signalanalyzer.filters;

import com.dmp.signalanalyzer.beans.Signal;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public abstract class SignalFilter {

    public String getName() {
        return getClass().getSimpleName();
    }

    public abstract Signal filter(Signal signal);

}
