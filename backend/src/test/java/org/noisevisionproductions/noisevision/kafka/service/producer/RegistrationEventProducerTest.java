package org.noisevisionproductions.noisevision.kafka.service.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.noisevisionproductions.noisevision.kafka.event.dto.UserRegistrationEvent;
import org.noisevisionproductions.noisevision.kafka.event.model.EventStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RegistrationEventProducerTest {

    private static final String TOPIC = "user-registration-events";
    private static final int PARTITION = 0;
    private static final long OFFSET = 123L;
    private static final String TEST_EVENT_ID = "test-event-123";
    private static final String TEST_EVENT_TYPE = "USER_REGISTRATION";

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private RegistrationEventProducer producerService;
    private UserRegistrationEvent testEvent;

    @BeforeEach
    void setUp() {
        producerService = new RegistrationEventProducer(kafkaTemplate);

        testEvent = new UserRegistrationEvent();
        testEvent.setUserId("test-user-id");
        testEvent.setEmail("test@example.com");
        testEvent.setName("Test User");
        testEvent.setCompanyName("Test Company");
        testEvent.setTimestamp(LocalDateTime.now());
        testEvent.setStatus(EventStatus.SUCCESS);
        testEvent.setEventId(TEST_EVENT_ID);
        testEvent.setEventType(TEST_EVENT_TYPE);
    }

    @Test
    void shouldSuccessfullySendRegistrationEvent() {
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                TOPIC, PARTITION, testEvent.getEventId(), testEvent
        );

        RecordMetadata recordMetadata = new RecordMetadata(
                new TopicPartition(TOPIC, PARTITION),
                OFFSET,
                0,
                System.currentTimeMillis(),
                0,
                0
        );

        SendResult<String, Object> sendResult = new SendResult<>(producerRecord, recordMetadata);

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        producerService.sendEvent(testEvent);

        verify(kafkaTemplate).send(TOPIC, testEvent.getEventId(), testEvent);
    }

    @Test
    void shouldHandleFailureWhenSendingEvent() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Failed to send message"));

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(future);

        producerService.sendEvent(testEvent);

        verify(kafkaTemplate).send(TOPIC, testEvent.getEventId(), testEvent);
    }

    @Test
    void shouldHandleNullEvent() {
        producerService.sendEvent(null);

        verify(kafkaTemplate, never()).send(anyString(), any(), any());
    }

    @Test
    void shouldReturnCorrectTopicName() {
        Assertions.assertEquals(TOPIC, producerService.getTopicName());
    }
}
