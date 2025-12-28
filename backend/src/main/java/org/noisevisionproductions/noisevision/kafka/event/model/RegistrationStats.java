package org.noisevisionproductions.noisevision.kafka.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationStats {
    private long totalRegistrations;
    private long successfulRegistrations;
    private long failedRegistrations;
    private double successRate;
    private List<RegistrationEventEntity> recentEvents;
}
