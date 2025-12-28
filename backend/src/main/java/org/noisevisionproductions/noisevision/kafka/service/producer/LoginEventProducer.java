package org.noisevisionproductions.noisevision.kafka.service.producer;

import lombok.extern.slf4j.Slf4j;
import org.noisevisionproductions.noisevision.kafka.event.dto.LoginAttemptEvent;
import org.noisevisionproductions.noisevision.kafka.service.base.KafkaEventProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class LoginEventProducer implements KafkaEventProducer<LoginAttemptEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_NAME = "login-attempt-events";

    public LoginEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendEvent(LoginAttemptEvent event) {
        if (event == null) {
            log.warn("Attempted to send null login event");
            return;
        }

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                getTopicName(),
                event.getEventId(),
                event
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent login event for user: {} with partition: {} and offset: {}",
                        event.getUserId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            } else {
                log.error("Unable to send login event for user: {}", event.getUserId(), ex);
            }
        });
    }

    @Override
    public String getTopicName() {
        return TOPIC_NAME;
    }
}
