package com.nauvalatmaja.learning.tdd.concurrency.notification;

import java.util.ArrayList;
import java.util.List;

import com.nauvalatmaja.learning.tdd.concurrency.polling.Timeout;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotificationTrace<T> {
    private final Object tracelock = new Object();
    private final List<T> trace = new ArrayList<>();
    private long timeoutMs = 1000L;

    public void append(T message) {
        synchronized (tracelock) {
            trace.add(message);
            tracelock.notifyAll();
        }
    }

    public void containsNotificationIn(Matcher<Iterable<? extends T>> criteria) throws InterruptedException {
        Timeout timeout = new Timeout(timeoutMs);
        synchronized (tracelock) {
            NotificationListStream<T> stream = new NotificationListStream<>(trace, criteria);
            while (!stream.hasMatched()) {
                if (timeout.hasTimedOut()) {
                    throw new AssertionError(failureDescriptionFrom(criteria));
                }
                timeout.waitOn(tracelock);
            }
        }

    }

    private String failureDescriptionFrom(Matcher<Iterable<? extends T>> criteria) {
        StringDescription description = new StringDescription();

        description.appendText("no message matching ")
                .appendDescriptionOf(criteria)
                .appendText("received within " + timeoutMs + "ms\n");

        if (trace.isEmpty()) {
            description.appendText("received nothing");
        } else {
            description.appendValueList("received:\n   ", "\n   ", "", trace);
        }

        return description.toString();
    }
}
