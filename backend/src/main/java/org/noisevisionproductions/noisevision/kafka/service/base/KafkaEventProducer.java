package org.noisevisionproductions.noisevision.kafka.service.base;

import org.noisevisionproductions.noisevision.kafka.event.base.KafkaEvent;

public interface KafkaEventProducer<T extends KafkaEvent> {
    void sendEvent(T event);

    String getTopicName();
}
