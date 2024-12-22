package org.noisevisionproductions.portfolio.kafka.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.noisevisionproductions.portfolio.kafka.event.dto.LoginAttemptEvent;
import org.noisevisionproductions.portfolio.kafka.event.model.LoginEventEntity;
import org.noisevisionproductions.portfolio.kafka.repository.LoginEventRepository;
import org.noisevisionproductions.portfolio.kafka.service.base.KafkaEventConsumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginEventConsumer implements KafkaEventConsumer<LoginAttemptEvent> {

    private final LoginEventRepository eventRepository;
    private static final String TOPIC_NAME = "user-login-events";
    private static final String GROUP_ID = "portfolio-group";

    public LoginEventConsumer(LoginEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    @KafkaListener(
            topics = TOPIC_NAME,
            groupId = GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleEvent(LoginAttemptEvent event) {
        if (event == null) {
            log.warn("Received null login event");
            return;
        }

        try {
            LoginEventEntity eventEntity = getLoginEventEntity(event);

            eventRepository.save(eventEntity);
        } catch (Exception e) {
            log.error("Error processing login event for userr: {}", event.getEmail(), e);
        }
    }

    private static LoginEventEntity getLoginEventEntity(LoginAttemptEvent event) {
        LoginEventEntity eventEntity = new LoginEventEntity();
        eventEntity.setUserId(event.getUserId());
        eventEntity.setEmail(event.getEmail());
        eventEntity.setTimestamp(event.getTimestamp());
        eventEntity.setLoginTime(event.getLoginTime());
        eventEntity.setStatus(event.getStatus());
        eventEntity.setEventId(event.getEventId());
        eventEntity.setEventType(event.getEventType());
        return eventEntity;
    }

    @Override
    public String getTopicName() {
        return TOPIC_NAME;
    }

    @Override
    public String getGroupId() {
        return GROUP_ID;
    }

    @Override
    public Class<LoginAttemptEvent> getEventType() {
        return LoginAttemptEvent.class;
    }
}
