package org.noisevisionproductions.noisevision.kafka.repository;

import org.noisevisionproductions.noisevision.kafka.event.model.EventStatus;
import org.noisevisionproductions.noisevision.kafka.event.model.LoginEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginEventRepository extends JpaRepository<LoginEventEntity, Long> {
    long countByStatus(EventStatus status);

    List<LoginEventEntity> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);

    List<LoginEventEntity> findAllByOrderByTimestampDesc();

    List<LoginEventEntity> findByUserIdOrderByTimestampDesc(String userId);
}
