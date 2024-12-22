package org.noisevisionproductions.portfolio.kafka.stats;

import org.noisevisionproductions.portfolio.kafka.event.model.EventStatus;
import org.noisevisionproductions.portfolio.kafka.event.model.RegistrationEventEntity;
import org.noisevisionproductions.portfolio.kafka.event.model.RegistrationStats;
import org.noisevisionproductions.portfolio.kafka.repository.RegistrationEventRepository;
import org.noisevisionproductions.portfolio.kafka.service.base.BaseStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationStatsService extends BaseStatsService<RegistrationEventEntity, RegistrationEventRepository> {

    public RegistrationStatsService(RegistrationEventRepository repository) {
        super(repository);
    }

    @Override
    public long getTotalEvents() {
        return getSuccessfulEvents() + getFailedEvents();
    }

    @Override
    public long getSuccessfulEvents() {
        return repository.countByStatus(EventStatus.SUCCESS);
    }

    @Override
    public long getFailedEvents() {
        return repository.countByStatus(EventStatus.FAILED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationEventEntity> getRecentEvents(int limit) {
        return repository.findAllByOrderByTimestampDesc()
                .stream()
                .limit(limit)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationEventEntity> getEventsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return repository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }

    @Transactional(readOnly = true)
    public RegistrationStats getStats() {
        long successful = getSuccessfulEvents();
        long failed = getFailedEvents();

        return RegistrationStats.builder()
                .totalRegistrations(successful + failed)
                .successfulRegistrations(successful)
                .failedRegistrations(failed)
                .successRate(calculateSuccessRate(successful, failed))
                .recentEvents(repository.findAllByOrderByTimestampDesc())
                .build();
    }
}
