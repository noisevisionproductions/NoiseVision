package org.noisevisionproductions.portfolio.kafka.service.producer;

import lombok.extern.slf4j.Slf4j;
import org.noisevisionproductions.portfolio.kafka.event.dto.UserRegistrationEvent;
import org.noisevisionproductions.portfolio.kafka.service.base.KafkaEventProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class RegistrationEventProducer implements KafkaEventProducer<UserRegistrationEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_NAME = "user-registration-events";

    public RegistrationEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendEvent(UserRegistrationEvent event) {
        if (event.getEventId() == null || event.getEventType() == null) {
            log.warn("Event has null required fields: eventId={}, eventType={}",
                    event.getEventId(), event.getEventType());
        }

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                getTopicName(),
                event.getEventId(),
                event
        );

        future.whenComplete(((result, ex) -> {
            if (ex == null && result != null) {
                log.info("Sent registration event for user: {} with partition: {} and offset: {}",
                        event.getUserId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            } else {
                log.error("Unable to send registration event for user: {}",
                        event.getUserId(),
                        ex
                );
            }
        }));

    }

    @Override
    public String getTopicName() {
        return TOPIC_NAME;
    }
}