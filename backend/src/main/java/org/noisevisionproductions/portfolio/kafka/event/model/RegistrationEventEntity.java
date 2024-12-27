package org.noisevisionproductions.portfolio.kafka.event.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.noisevisionproductions.portfolio.kafka.event.base.BaseEventEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration_events")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class RegistrationEventEntity extends BaseEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String email;

    @Column()
    private String name;

    @Column()
    private String companyName;

    @Column(nullable = false)
    private LocalDateTime registrationTime;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(length = 50)
    private String registrationSource;

    @PrePersist
    public void onCreate() {
        if (registrationTime == null) {
            registrationTime = LocalDateTime.now();
        }
    }
}
