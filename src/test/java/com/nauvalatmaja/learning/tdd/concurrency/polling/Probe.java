package com.nauvalatmaja.learning.tdd.concurrency.polling;

import org.hamcrest.Description;

public interface Probe {
    boolean isSatisfied();
    void sample();
    void describeAcceptenceCriteriaTo(Description d);
    void describeFailureTo(Description d);
}
