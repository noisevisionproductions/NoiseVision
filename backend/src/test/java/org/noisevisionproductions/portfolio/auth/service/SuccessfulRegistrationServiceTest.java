package org.noisevisionproductions.portfolio.unit.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.auth.exceptions.RegistrationBlockedException;
import org.noisevisionproductions.portfolio.auth.service.SuccessfulRegistrationService;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SuccessfulRegistrationServiceTest {

    private SuccessfulRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new SuccessfulRegistrationService();
    }

    @Test
    void registerSuccessfulRegistration_ShouldStoreRegistration() {
        String ipAddress = "127.0.0.1";

        service.registerSuccessfulRegistration(ipAddress);

        assertThatThrownBy(() -> service.canRegister(ipAddress))
                .isInstanceOf(RegistrationBlockedException.class);
    }

    @Test
    void canRegister_ShouldAllowRegistration_WhenNoPreviousRegistration() {
        String ipAddress = "127.0.0.1";

        assertThatCode(() -> service.canRegister(ipAddress))
                .doesNotThrowAnyException();
    }

    @Test
    void canRegister_ShouldThrownException_WhenRegistrationWithinOneHour() {
        String ipAddress = "127.0.0.1";

        service.registerSuccessfulRegistration(ipAddress);

        assertThatThrownBy(() -> service.canRegister(ipAddress))
                .isInstanceOf(RegistrationBlockedException.class)
                .hasMessageContaining("minutes");
    }

    @Test
    void canRegister_ShouldAllowRegistration_WhenMoreThanOneHourPassed() {
        String ipAddress = "127.0.0.1";

        service.registerSuccessfulRegistration(ipAddress);

        ReflectionTestUtils.setField(service, "successfulRegistrations",
                new ConcurrentHashMap<String, LocalDateTime>() {{
                    put(ipAddress, LocalDateTime.now().minusHours(2));
                }});

        assertThatCode(() -> service.canRegister(ipAddress))
                .doesNotThrowAnyException();
    }

    @Test
    void cleanExpiredRegistration_ShouldRemoveOldRegistration() {
        String recentIp = "127.0.0.1";
        String oldIp = "127.0.0.2";

        Map<String, LocalDateTime> registrations = new ConcurrentHashMap<>();
        registrations.put(recentIp, LocalDateTime.now().minusMinutes(30));
        registrations.put(oldIp, LocalDateTime.now().minusHours(2));

        ReflectionTestUtils.setField(service, "successfulRegistrations", registrations);

        service.cleanExpiredRegistration();

        assertThatThrownBy(() -> service.canRegister(recentIp))
                .isInstanceOf(RegistrationBlockedException.class);

        assertThatCode(() -> service.canRegister(oldIp))
                .doesNotThrowAnyException();
    }

    @Test
    void cleanExpiredRegistration_ShouldHandleEmptyMap() {
        assertThatCode(() -> service.cleanExpiredRegistration())
                .doesNotThrowAnyException();
    }

    @Test
    void multipleRegistrationAttempts_ShouldBehaveCorrectly() {
        String ipAddress = "127.0.0.1";

        assertThatCode(() -> service.canRegister(ipAddress))
                .doesNotThrowAnyException();

        service.registerSuccessfulRegistration(ipAddress);

        assertThatThrownBy(() -> service.canRegister(ipAddress))
                .isInstanceOf(RegistrationBlockedException.class);

        ReflectionTestUtils.setField(service, "successfulRegistrations",
                new ConcurrentHashMap<String, LocalDateTime>() {{
                    put(ipAddress, LocalDateTime.now().minusHours(2));
                }});

        assertThatCode(() -> service.canRegister(ipAddress))
                .doesNotThrowAnyException();
    }
}