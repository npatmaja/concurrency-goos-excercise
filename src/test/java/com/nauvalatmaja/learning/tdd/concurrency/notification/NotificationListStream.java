package com.nauvalatmaja.learning.tdd.concurrency.notification;

import java.util.List;

import org.hamcrest.Matcher;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationListStream<T> {

    private final List<T> notifications;
    private final Matcher<Iterable<? extends T>> criteria;

    public boolean hasMatched() {
        return criteria.matches(notifications);
    }
}
