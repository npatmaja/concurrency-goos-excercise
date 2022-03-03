package com.nauvalatmaja.learning.tdd.concurrency;

import static com.nauvalatmaja.learning.tdd.concurrency.NotificationProbe.fireNotificationTo;
import static com.nauvalatmaja.learning.tdd.concurrency.polling.Poller.assertEventually;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

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
    private ExecutorService executor;

    @BeforeEach
    void setup() {
        notification = mock(NotificationGateway.class);

        executor = Executors.newFixedThreadPool(5);
        
        handler = new NotificationHandler(notification, executor);
    }

    @Nested
    class UsingExecutor {
        @Test
        @SneakyThrows
        void givenAList_whenFire_shouldCallNotificationGateway() {
            Logger log = Logger.builder().start(System.currentTimeMillis()).build();
            
            doAnswer(invocation -> {
                log.logExecution(Thread.currentThread(), "executes notification#fire");
                Thread.sleep(1000);
                return null;
            }).when(notification).fire(anyString());
    
            handler.handle(sendToAddresses);

            executor.shutdown();
            log.logExecution(Thread.currentThread(), "executor shutdown");
            
            executor.awaitTermination(10000, TimeUnit.MILLISECONDS);
            log.logExecution(Thread.currentThread(), "executor terminated");
            
            verify(notification, times(sendToAddresses.size())).fire(anyString());
        }
    }

    @Nested
    class UsingPoller {
        @SneakyThrows
        @Test
        void givenAList_whenFire_shouldCallNotificationGateway() {
            doAnswer(invocation -> {
                Thread.sleep(1000);
                return null;
            }).when(notification).fire(anyString());
    
            handler.handle(sendToAddresses);
            
            assertEventually(
                fireNotificationTo(notification, is(sendToAddresses.size())),
                10000L, 1000L);
            verify(notification, times(sendToAddresses.size())).fire(anyString());
        }
    }

    @RequiredArgsConstructor
    @Builder
    public static class Logger {
        private final long start;
        
        public void logExecution(Thread thread, String message) {
            System.out.printf("[thread %s] %s after [%s ms] from intial execution time\n",
                thread.getName(), message, System.currentTimeMillis() - start);
        }
        
    }
}
