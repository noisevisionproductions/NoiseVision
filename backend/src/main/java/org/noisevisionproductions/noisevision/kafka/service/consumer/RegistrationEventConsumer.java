package org.noisevisionproductions.noisevision.kafka.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.noisevisionproductions.noisevision.kafka.event.dto.UserRegistrationEvent;
import org.noisevisionproductions.noisevision.kafka.event.model.RegistrationEventEntity;
import org.noisevisionproductions.noisevision.kafka.repository.RegistrationEventRepository;
import org.noisevisionproductions.noisevision.kafka.service.base.KafkaEventConsumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RegistrationEventConsumer implements KafkaEventConsumer<UserRegistrationEvent> {

    private final RegistrationEventRepository eventRepository;
    private static final String TOPIC_NAME = "user-registration-events";
    private static final String GROUP_ID = "portfolio-group";

    public RegistrationEventConsumer(RegistrationEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    @KafkaListener(
            topics = TOPIC_NAME,
            groupId = GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleEvent(UserRegistrationEvent event) {
        if (event == null) {
            log.warn("Received null registration event");
            return;
        }

        try {
            RegistrationEventEntity eventEntity = getRegistrationEventEntity(event);

            RegistrationEventEntity savedEntity = eventRepository.save(eventEntity);
            log.info("Successfully saved registration event with ID: {}", savedEntity.getId());
        } catch (Exception e) {
            log.error("Error processing registration event: {}", event, e);
        }
    }

    private static RegistrationEventEntity getRegistrationEventEntity(UserRegistrationEvent event) {
        RegistrationEventEntity eventEntity = new RegistrationEventEntity();

        eventEntity.setEventId(event.getEventId());
        eventEntity.setEventType(event.getEventType());
        eventEntity.setTimestamp(event.getTimestamp());
        eventEntity.setStatus(event.getStatus());

        eventEntity.setUserId(event.getUserId());
        eventEntity.setEmail(event.getEmail());
        eventEntity.setName(event.getName());
        eventEntity.setCompanyName(event.getCompanyName());
        eventEntity.setRegistrationTime(event.getRegistrationTime());

        eventEntity.setIpAddress(event.getIpAddress());
        eventEntity.setUserAgent(event.getUserAgent());
        eventEntity.setRegistrationSource(event.getRegistrationSource());

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
    public Class<UserRegistrationEvent> getEventType() {
        return UserRegistrationEvent.class;
    }
}
