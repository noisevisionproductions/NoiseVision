package org.noisevisionproductions.portfolio.kafka.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.portfolio.kafka.event.model.EventStatus;
import org.noisevisionproductions.portfolio.kafka.event.model.RegistrationEventEntity;
import org.noisevisionproductions.portfolio.kafka.event.model.RegistrationStats;
import org.noisevisionproductions.portfolio.kafka.repository.RegistrationEventRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationStatsServiceTest {

    @Mock
    private RegistrationEventRepository eventRepository;

    private RegistrationStatsService statsService;
    private List<RegistrationEventEntity> testEvents;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        statsService = new RegistrationStatsService(eventRepository);
        now = LocalDateTime.now();

        testEvents = Arrays.asList(
                createEvent("user1", now, EventStatus.SUCCESS),
                createEvent("user2", now.minusHours(1), EventStatus.FAILED),
                createEvent("user3", now.minusHours(2), EventStatus.SUCCESS),
                createEvent("user4", now.minusHours(3), EventStatus.SUCCESS)
        );
    }

    @Test
    void shouldGetStats() {
        when(eventRepository.countByStatus(EventStatus.SUCCESS)).thenReturn(3L);
        when(eventRepository.countByStatus(EventStatus.FAILED)).thenReturn(1L);
        when(eventRepository.findAllByOrderByTimestampDesc()).thenReturn(testEvents);

        RegistrationStats stats = statsService.getStats();

        assertThat(stats.getTotalRegistrations()).isEqualTo(4);
        assertThat(stats.getSuccessfulRegistrations()).isEqualTo(3);
        assertThat(stats.getFailedRegistrations()).isEqualTo(1);
        assertThat(stats.getSuccessRate()).isEqualTo(75.0);
        assertThat(stats.getRecentEvents()).isEqualTo(testEvents);
    }

    @Test
    void shouldReturnZeroStatsWhenNoEvents() {
        when(eventRepository.countByStatus(EventStatus.SUCCESS)).thenReturn(0L);
        when(eventRepository.countByStatus(EventStatus.FAILED)).thenReturn(0L);
        when(eventRepository.findAllByOrderByTimestampDesc()).thenReturn(List.of());

        RegistrationStats stats = statsService.getStats();

        assertThat(stats.getTotalRegistrations()).isZero();
        assertThat(stats.getSuccessfulRegistrations()).isZero();
        assertThat(stats.getFailedRegistrations()).isZero();
        assertThat(stats.getSuccessRate()).isZero();
        assertThat(stats.getRecentEvents()).isEmpty();
    }

    @Test
    void shouldGetRecentEvents() {
        when(eventRepository.findAllByOrderByTimestampDesc()).thenReturn(testEvents);
        int limit = 2;

        List<RegistrationEventEntity> recentEvents = statsService.getRecentEvents(limit);

        assertThat(recentEvents).hasSize(2);
        assertThat(recentEvents).containsExactly(testEvents.getFirst(), testEvents.get(1));
    }

    @Test
    void shouldGetEventBetweenDates() {
        LocalDateTime start = now.minusHours(2);
        LocalDateTime end = now;
        List<RegistrationEventEntity> expectedEvents = testEvents.subList(0, 3);

        when(eventRepository.findByTimestampBetweenOrderByTimestampDesc(start, end))
                .thenReturn(expectedEvents);

        List<RegistrationEventEntity> events = statsService.getEventsBetweenDates(start, end);

        assertThat(events).hasSize(3);
        assertThat(events).isEqualTo(expectedEvents);
    }

    @ParameterizedTest
    @CsvSource({
            "3, 1, 75.0",
            "0, 0, 0.0",
            "5, 0, 100.0",
            "0, 5, 0.0"
    })
    void shouldCalculateSuccessRate(long successful, long failed, double expectedRate) {
        when(eventRepository.countByStatus(EventStatus.SUCCESS)).thenReturn(successful);
        when(eventRepository.countByStatus(EventStatus.FAILED)).thenReturn(failed);
        when(eventRepository.findAllByOrderByTimestampDesc()).thenReturn(List.of());

        RegistrationStats stats = statsService.getStats();

        assertThat(stats.getSuccessRate()).isEqualTo(expectedRate);
    }

    private RegistrationEventEntity createEvent(String userId, LocalDateTime timestamp, EventStatus eventStatus) {
        RegistrationEventEntity event = new RegistrationEventEntity();
        event.setUserId(userId);
        event.setEmail(userId + "@example.com");
        event.setName("Test User " + userId);
        event.setCompanyName("Test Company");
        event.setRegistrationTime(timestamp);
        event.setStatus(eventStatus);
        return event;
    }
}
