package org.noisevisionproductions.portfolio.kafka.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.noisevisionproductions.portfolio.kafka.event.base.KafkaEvent;
import org.noisevisionproductions.portfolio.kafka.event.model.EventStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginAttemptEvent implements KafkaEvent {

    private String eventId = UUID.randomUUID().toString();
    private String eventType = "LOGIN_ATTEMPT";
    private LocalDateTime timestamp = LocalDateTime.now();

    private String userId;
    private String email;
    private EventStatus status;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime loginTime;
    private String failureReason;
}
