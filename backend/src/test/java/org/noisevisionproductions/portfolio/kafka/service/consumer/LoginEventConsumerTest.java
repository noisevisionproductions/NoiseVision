package org.noisevisionproductions.portfolio.kafka.service.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.noisevisionproductions.portfolio.kafka.event.dto.LoginAttemptEvent;
import org.noisevisionproductions.portfolio.kafka.event.model.EventStatus;
import org.noisevisionproductions.portfolio.kafka.event.model.LoginEventEntity;
import org.noisevisionproductions.portfolio.kafka.repository.LoginEventRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class LoginEventConsumerTest {

    @Mock
    private LoginEventRepository eventRepository;

    @InjectMocks
    private LoginEventConsumer loginEventConsumer;

    private LoginAttemptEvent testEvent;
    private static final String TEST_EVENT_ID = "test-event-123";
    private static final String TEST_EVENT_TYPE = "USER_LOGIN";

    @BeforeEach
    void setUp() {
        LocalDateTime testTime = LocalDateTime.now();
        testEvent = new LoginAttemptEvent();
        testEvent.setUserId("testId");
        testEvent.setEmail("test@example.com");
        testEvent.setTimestamp(testTime);
        testEvent.setLoginTime(testTime);
        testEvent.setStatus(EventStatus.SUCCESS);
        testEvent.setEventId(TEST_EVENT_ID);
        testEvent.setEventType(TEST_EVENT_TYPE);
    }

    @Test
    void shouldSuccessfullyProcessLoginEvent() {
        when(eventRepository.save(any(LoginEventEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        loginEventConsumer.handleEvent(testEvent);

        verify(eventRepository).save(argThat(entity -> {
            assertThat(entity.getUserId()).isEqualTo(testEvent.getUserId());
            assertThat(entity.getEmail()).isEqualTo(testEvent.getEmail());
            assertThat(entity.getStatus()).isEqualTo(testEvent.getStatus());
            assertThat(entity.getEventId()).isEqualTo(testEvent.getEventId());
            assertThat(entity.getEventType()).isEqualTo(testEvent.getEventType());
            assertThat(entity.getTimestamp()).isEqualTo(testEvent.getTimestamp());
            assertThat(entity.getLoginTime()).isEqualTo(testEvent.getLoginTime());
            return true;
        }));
    }

    @Test
    void shouldHandleNullEvent() {
        loginEventConsumer.handleEvent(null);
        verify(eventRepository, never()).save(any(LoginEventEntity.class));
    }

    @Test
    void shouldHandleExceptionDuringEventProcessing() {
        when(eventRepository.save(any(LoginEventEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        loginEventConsumer.handleEvent(testEvent);

        verify(eventRepository, times(1)).save(any(LoginEventEntity.class));
    }
}