package org.noisevisionproductions.noisevision.kafka.event.base;

import java.time.LocalDateTime;

public interface KafkaEvent {
    String getEventId();

    String getEventType();

    LocalDateTime getTimestamp();
}
