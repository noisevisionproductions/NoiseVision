package org.noisevisionproductions.portfolio.kafka.stats;

import org.noisevisionproductions.portfolio.kafka.event.model.EventStatus;
import org.noisevisionproductions.portfolio.kafka.event.model.LoginEventEntity;
import org.noisevisionproductions.portfolio.kafka.event.model.LoginStats;
import org.noisevisionproductions.portfolio.kafka.repository.LoginEventRepository;
import org.noisevisionproductions.portfolio.kafka.service.base.BaseStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoginStatsService extends BaseStatsService<LoginEventEntity, LoginEventRepository> {

    public LoginStatsService(LoginEventRepository repository) {
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
    public List<LoginEventEntity> getRecentEvents(int limit) {
        return repository.findAllByOrderByTimestampDesc()
                .stream()
                .limit(limit)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginEventEntity> getEventsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return repository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }

    @Transactional(readOnly = true)
    public LoginStats getStats() {
        long successful = getSuccessfulEvents();
        long failed = getFailedEvents();

        return LoginStats.builder()
                .totalLogins(successful + failed)
                .successfulLogins(successful)
                .failedLogins(failed)
                .successRate(calculateSuccessRate(successful, failed))
                .recentEvents(repository.findAllByOrderByTimestampDesc())
                .build();
    }
}
