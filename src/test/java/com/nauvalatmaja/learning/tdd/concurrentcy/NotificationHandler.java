package com.nauvalatmaja.learning.tdd.concurrentcy;

import java.util.List;

import lombok.SneakyThrows;

public class NotificationHandler {
    private final NotificationGateway notification;

    public NotificationHandler(NotificationGateway notification) {
        this.notification = notification;
    }

    public void handle(List<String> sendToAddresses) {
        sendToAddresses.stream().forEach(this::sendNotification);
    }

    @SneakyThrows
    private void sendNotification(String emailAddress) {
        notification.fire(emailAddress);
    }

}
