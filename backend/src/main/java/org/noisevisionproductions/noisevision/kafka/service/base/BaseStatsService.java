package org.noisevisionproductions.noisevision.kafka.service.base;

import lombok.RequiredArgsConstructor;
import org.noisevisionproductions.noisevision.kafka.event.base.BaseEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public abstract class BaseStatsService<T extends BaseEventEntity, R extends JpaRepository<T, ?>> {
    protected final R repository;

    public abstract long getTotalEvents();

    public abstract long getSuccessfulEvents();

    public abstract long getFailedEvents();

    public abstract List<T> getRecentEvents(int limit);

    public abstract List<T> getEventsBetweenDates(LocalDateTime start, LocalDateTime end);

    protected double calculateSuccessRate(long successful, long failed) {
        long total = successful + failed;
        if (total == 0) return 0.0;
        return (double) successful / total * 100;
    }
}
