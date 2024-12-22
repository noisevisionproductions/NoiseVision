package org.noisevisionproductions.portfolio.kafka.service.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LoginEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private LoginEventProducer loginEventProducer;

    private LoginAttemptEvent testEvent;

    private static final String TEST_EVENT_ID = "test-event-123";
    private static final String TEST_USER_ID = "test-user-123";
    private static final String TOPIC_NAME = "login-attempt-events";

    @BeforeEach
    void setUp() {
        LocalDateTime testTime = LocalDateTime.now();
        testEvent = new LoginAttemptEvent();
        testEvent.setEventId(TEST_EVENT_ID);
        testEvent.setUserId(TEST_USER_ID);
        testEvent.setEmail("test@example.com");
        testEvent.setTimestamp(testTime);
        testEvent.setLoginTime(testTime);
        testEvent.setStatus(EventStatus.SUCCESS);
        testEvent.setEventType("USER_LOGIN");
    }

    @Test
    void shouldSuccessfullySendEvent() {
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(TOPIC_NAME, TEST_EVENT_ID, testEvent);
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition(TOPIC_NAME, 0), 0L, 0, 0L, 0, 0);
        SendResult<String, Object> sendResult = new SendResult<>(producerRecord, recordMetadata);

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(sendResult));

        loginEventProducer.sendEvent(testEvent);

        verify(kafkaTemplate).send(
                eq(TOPIC_NAME),
                eq(TEST_EVENT_ID),
                eq(testEvent)
        );
    }

    @Test
    void shouldHandleFailureWhenSendingEvent() {
        RuntimeException testException = new RuntimeException("Test error");
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.failedFuture(testException));

        loginEventProducer.sendEvent(testEvent);

        verify(kafkaTemplate).send(
                eq(TOPIC_NAME),
                eq(TEST_EVENT_ID),
                eq(testEvent)
        );
    }

    @Test
    void shouldReturnCorrectTopicName() {
        assertThat(loginEventProducer.getTopicName()).isEqualTo(TOPIC_NAME);
    }
}
