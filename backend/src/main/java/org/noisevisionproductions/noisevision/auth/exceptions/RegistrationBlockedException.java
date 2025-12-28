package org.noisevisionproductions.noisevision.auth.exceptions;

import lombok.Getter;

import java.time.Duration;

@Getter
public class RegistrationBlockedException extends RuntimeException {
    private final Duration timeLeft;

    public RegistrationBlockedException(Duration timeLeft) {
        super("Registration is blocked for " + timeLeft.toMinutes() + " minutes");
        this.timeLeft = timeLeft;
    }
}