package org.noisevisionproductions.noisevision.auth.service;

import org.noisevisionproductions.noisevision.auth.exceptions.RegistrationBlockedException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SuccessfulRegistrationService {
    private final Map<String, LocalDateTime> successfulRegistrations = new ConcurrentHashMap<>();

    public void registerSuccessfulRegistration(String ipAddress) {
        successfulRegistrations.put(ipAddress, LocalDateTime.now());
    }

    public void canRegister(String ipAddress) {
        LocalDateTime lastRegistration = successfulRegistrations.get(ipAddress);
        if (lastRegistration != null) {
            LocalDateTime oneHourAfterRegistration = lastRegistration.plusHours(1);
            if (LocalDateTime.now().isBefore(oneHourAfterRegistration)) {
                Duration timeLeft = Duration.between(LocalDateTime.now(), oneHourAfterRegistration);
                throw new RegistrationBlockedException(timeLeft);
            } else {
                successfulRegistrations.remove(ipAddress);
            }
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void cleanExpiredRegistration() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        successfulRegistrations.entrySet().removeIf(entry ->
                entry.getValue().isBefore(oneHourAgo));
    }
}
