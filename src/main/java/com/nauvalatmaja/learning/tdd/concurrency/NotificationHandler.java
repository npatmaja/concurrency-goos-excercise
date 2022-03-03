package com.nauvalatmaja.learning.tdd.concurrency;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import lombok.SneakyThrows;

public class NotificationHandler {
    private final NotificationGateway notification;
    private final Executor executor;

    public NotificationHandler(NotificationGateway notification, Executor executor) {
        this.notification = notification;
        this.executor = executor;
    }

    public void handle(List<String> sendToAddresses) {
        CompletableFuture.runAsync(() -> {
            sendToAddresses.parallelStream().forEach(this::sendNotification);
        }, executor);
    }

    @SneakyThrows
    private void sendNotification(String emailAddress) {
        notification.fire(emailAddress);
    }

}
