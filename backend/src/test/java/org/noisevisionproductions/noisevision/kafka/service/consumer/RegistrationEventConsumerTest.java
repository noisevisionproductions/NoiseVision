package org.noisevisionproductions.noisevision.kafka.service.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.noisevisionproductions.noisevision.kafka.event.dto.UserRegistrationEvent;
import org.noisevisionproductions.noisevision.kafka.event.model.EventStatus;
import org.noisevisionproductions.noisevision.kafka.event.model.RegistrationEventEntity;
import org.noisevisionproductions.noisevision.kafka.repository.RegistrationEventRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class RegistrationEventConsumerTest {

    @Mock
    private RegistrationEventRepository eventRepository;

    @InjectMocks
    private RegistrationEventConsumer registrationEventConsumer;

    private UserRegistrationEvent testEvent;
    private static final String TEST_EVENT_ID = "test-event-123";
    private static final String TEST_EVENT_TYPE = "USER_REGISTRATION";

    @BeforeEach
    void setUp() {
        LocalDateTime testTime = LocalDateTime.now();
        testEvent = new UserRegistrationEvent();
        testEvent.setUserId("testId");
        testEvent.setEmail("test@example.com");
        testEvent.setName("Test User");
        testEvent.setCompanyName("Test Company");
        testEvent.setTimestamp(testTime);
        testEvent.setRegistrationTime(testTime);
        testEvent.setStatus(EventStatus.SUCCESS);
        testEvent.setEventId(TEST_EVENT_ID);
        testEvent.setEventType(TEST_EVENT_TYPE);
    }

    @Test
    void shouldSuccessfullyProcessRegistrationEvent() {
        when(eventRepository.save(any(RegistrationEventEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        registrationEventConsumer.handleEvent(testEvent);

        verify(eventRepository).save(argThat(entity -> {
            assertThat(entity.getUserId()).isEqualTo(testEvent.getUserId());
            assertThat(entity.getEmail()).isEqualTo(testEvent.getEmail());
            assertThat(entity.getName()).isEqualTo(testEvent.getName());
            assertThat(entity.getCompanyName()).isEqualTo(testEvent.getCompanyName());
            assertThat(entity.getStatus()).isEqualTo(testEvent.getStatus());
            assertThat(entity.getEventId()).isEqualTo(testEvent.getEventId());
            assertThat(entity.getEventType()).isEqualTo(testEvent.getEventType());
            assertThat(entity.getTimestamp()).isEqualTo(testEvent.getTimestamp());
            assertThat(entity.getRegistrationTime()).isEqualTo(testEvent.getRegistrationTime());
            return true;
        }));
    }

    @Test
    void shouldHandleExceptionWhenProcessingEvent() {
        when(eventRepository.save(any(RegistrationEventEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        registrationEventConsumer.handleEvent(testEvent);

        verify(eventRepository, times(1)).save(any(RegistrationEventEntity.class));
    }

    @Test
    void shouldHandleNullEvent() {
        registrationEventConsumer.handleEvent(null);

        verify(eventRepository, never()).save(any(RegistrationEventEntity.class));
    }
}