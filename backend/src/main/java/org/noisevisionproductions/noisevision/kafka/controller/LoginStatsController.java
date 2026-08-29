package org.noisevisionproductions.noisevision.kafka.controller;

import org.noisevisionproductions.noisevision.kafka.controller.base.BaseKafkaStatsController;
import org.noisevisionproductions.noisevision.kafka.event.model.LoginEventEntity;
import org.noisevisionproductions.noisevision.kafka.event.model.LoginStats;
import org.noisevisionproductions.noisevision.kafka.stats.LoginStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kafka/stats/logins")
@PreAuthorize("hasAuthority('ACCESS_KAFKA_DASHBOARD')")
public class LoginStatsController extends BaseKafkaStatsController<LoginEventEntity, LoginStatsService> {
    public LoginStatsController(LoginStatsService statsService) {
        super(statsService);
    }

    @GetMapping
    public ResponseEntity<LoginStats> getLoginStats() {
        return ResponseEntity.ok(statsService.getStats());
    }
}
