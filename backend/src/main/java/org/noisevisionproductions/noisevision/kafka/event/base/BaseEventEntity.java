package org.noisevisionproductions.noisevision.kafka.event.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.noisevisionproductions.noisevision.kafka.event.model.EventStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEventEntity {
    @Column(nullable = false)
    private String eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (eventId == null) {
            eventId = UUID.randomUUID().toString();
        }
    }
}