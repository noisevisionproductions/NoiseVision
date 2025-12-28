package org.noisevisionproductions.noisevision.kafka.service.base;

import org.noisevisionproductions.noisevision.kafka.event.base.KafkaEvent;

public interface KafkaEventConsumer<T extends KafkaEvent> {
    void handleEvent(T event);

    String getTopicName();

    String getGroupId();

    Class<T> getEventType();
}
