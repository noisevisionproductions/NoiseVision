package org.noisevisionproductions.noisevision.kafka.controller.base;

import lombok.RequiredArgsConstructor;
import org.noisevisionproductions.noisevision.kafka.event.base.BaseEventEntity;
import org.noisevisionproductions.noisevision.kafka.service.base.BaseStatsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public abstract class BaseKafkaStatsController<T extends BaseEventEntity, S extends BaseStatsService<T, ?>> {
    protected final S statsService;

    @GetMapping("/recent")
    public ResponseEntity<List<T>> getRecentEvents(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(statsService.getRecentEvents(limit));
    }

    @GetMapping("/period")
    public ResponseEntity<List<T>> getEventsForPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(statsService.getEventsBetweenDates(start, end));
    }
}
