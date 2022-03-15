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

    public void handle(List<String> recipients) {
        CompletableFuture.runAsync(() -> {
            recipients.parallelStream().forEach(this::sendNotification);
        }, executor);
    }

    public void handleWithExecutor(List<String> recipients) {
        for (String recipient : recipients) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    sendNotification(recipient);
                }
            });
        }
    }

    @SneakyThrows
    private void sendNotification(String emailAddress) {
        notification.fire(emailAddress);
    }

}
