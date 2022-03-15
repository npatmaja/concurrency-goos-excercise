package com.nauvalatmaja.learning.tdd.concurrency.polling;

import org.hamcrest.StringDescription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Poller {
    private long timeoutMillis;
    private long pollDelayMillis;

    public void check(Probe probe) throws InterruptedException {
        Timeout timeout = new Timeout(timeoutMillis);
        while (!probe.isSatisfied()) {
            if (timeout.hasTimedOut()) {
                throw new AssertionError(describeFailureOf(probe));
            }
            Thread.sleep(pollDelayMillis);
            probe.sample();
        }
    }

    private String describeFailureOf(Probe probe) {
        StringDescription description = new StringDescription();
        description.appendText("\nTried to look for:\n      ");
        probe.describeAcceptanceCriteriaTo(description);
        description.appendText("\nbut:\n    ");
        probe.describeFailureTo(description);
        return description.toString();
    }

    public static void assertEventually(
        Probe probe,
        long timeoutMillis,
        long delayMillis)
    throws InterruptedException {
        new Poller(timeoutMillis, delayMillis).check(probe);;
    }
}
