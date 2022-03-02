package com.nauvalatmaja.learning.tdd.concurrentcy;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FireAndForgetTest {
    private final List<String> sendToAddresses = Arrays.asList(
        "a@example.com",
        "b@example.com",
        "c@example.com",
        "d@example.com",
        "e@example.com",
        "f@example.com",
        "g@example.com",
        "h@example.com",
        "i@example.com",
        "j@example.com"
    );
    private NotificationGateway notification;
    private NotificationHandler handler;

    @BeforeEach
    void setup() {
        notification = mock(NotificationGateway.class);
        
        doAnswer(invocation -> {
            Thread.sleep(1000);
            return null;
        }).when(notification).fire(anyString());
        
        handler = new NotificationHandler(notification);
    }
    
    @Test
    void givenAList_whenFire_shouldCallNotificationGateway() {    
        handler.handle(sendToAddresses);
        verify(notification, times(sendToAddresses.size())).fire(anyString());
    }
}
