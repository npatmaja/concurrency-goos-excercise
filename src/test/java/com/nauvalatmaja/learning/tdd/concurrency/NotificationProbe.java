package com.nauvalatmaja.learning.tdd.concurrency;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import com.nauvalatmaja.learning.tdd.concurrency.polling.Probe;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.ArgumentCaptor;

public class NotificationProbe {
    private static final int NOT_SET = -1;

    public static Probe fireNotificationTo(
        NotificationGateway notification, final Matcher<Integer> matcher) {
        return new Probe() {
            private int count = NOT_SET;

            @Override
            public boolean isSatisfied() {
               return count != NOT_SET && matcher.matches(count);
            }

            @Override
            public void sample() {
                ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
                verify(notification, atLeastOnce()).fire(captor.capture());
                count = captor.getAllValues().size();
            }

            @Override
            public void describeAcceptanceCriteriaTo(Description d) {
                d.appendText("notification")
                .appendText(" has been called ")
                .appendDescriptionOf(matcher);
            }

            @Override
            public void describeFailureTo(Description d) {
                d.appendText("call count was ").appendValue(count);
            }

        };
    }
}