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
public class LoginStats {
    private long totalLogins;
    private long successfulLogins;
    private long failedLogins;
    private double successRate;
    private List<LoginEventEntity> recentEvents;
}
