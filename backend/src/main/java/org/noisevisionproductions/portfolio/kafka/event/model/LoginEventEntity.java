package org.noisevisionproductions.portfolio.kafka.event.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.noisevisionproductions.portfolio.kafka.event.base.BaseEventEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_events")
@Getter
@Setter
public class LoginEventEntity extends BaseEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private String ipAddress;
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime loginTime;

    private String failureReason;
}
