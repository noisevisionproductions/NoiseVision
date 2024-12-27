import {vi, expect, describe, beforeEach, test} from "vitest";
import {EventStatus} from "@/kafka/types/baseEvent";
import {act, renderHook, waitFor} from "@testing-library/react";
import {useRegistrationStats} from "@/kafka/hooks/useRegistrationStats";
import {registrationStatsService} from "@/kafka/services/registrationStatsService";
import {AxiosError} from "axios";
import {RegistrationStats} from "@/kafka/types/registrationEvent";

vi.mock('@/kafka/services/registrationStatsService', () => ({
    registrationStatsService: {
        getStats: vi.fn(),
        getRecentRegistrations: vi.fn(),
        getRegistrationsForPeriod: vi.fn()
    }
}));

describe('useRegistrationStats Authorization Tests', () => {
    beforeEach(() => {
        vi.clearAllMocks();
        vi.spyOn(console, 'error').mockImplementation(() => {
        });
    });

    test('should handle unauthorized access (401)', async () => {
        const unauthorizedError = new AxiosError(
            'Unauthorized',
            '401',
            undefined,
            undefined,
            {
                status: 401,
                statusText: 'Unauthorized',
                data: null,
                headers: {},
                config: {} as any
            }
        );

        vi.mocked(registrationStatsService.getStats).mockRejectedValueOnce(unauthorizedError);

        const {result} = renderHook(() => useRegistrationStats());

        await waitFor(() => {
            expect(result.current.error).toBe('kafka.errors.statsFetchFailed');
            expect(result.current.loading).toBe(false);
            expect(result.current.stats).toBeNull();
        });
    });

    test('should handle forbidden access (403)', async () => {
        const forbiddenError = new AxiosError(
            'Forbidden',
            '403',
            undefined,
            undefined,
            {
                status: 403,
                statusText: 'Forbidden',
                data: null,
                headers: {},
                config: {} as any

            }
        );

        vi.mocked(registrationStatsService.getStats).mockRejectedValueOnce(forbiddenError);

        const {result} = renderHook(() => useRegistrationStats());

        await waitFor(() => {
            expect(result.current.error).toBe('kafka.errors.statsFetchFailed');
            expect(result.current.loading).toBe(false);
            expect(result.current.stats).toBeNull();
        });
    });

    test('should handle successful authorized access', async () => {
        const mockStats: RegistrationStats = {
            totalRegistrations: 100,
            successfulRegistrations: 90,
            failedRegistrations: 10,
            successRate: 0.9,
            recentEvents: [{
                id: 1,
                eventId: 'test-event-id',
                eventType: 'USER_REGISTRATION',
                userId: 'test-user',
                email: 'test@example.com',
                name: 'Test User',
                companyName: 'Test Company',
                timestamp: Date.now(),
                registrationTime: new Date().toISOString(),
                status: EventStatus.SUCCESS,
                ipAddress: '127.0.0.1',
                userAgent: 'test-agent',
                registrationSource: 'WEB'
            }]
        };

        vi.mocked(registrationStatsService.getStats).mockResolvedValueOnce(mockStats);
        vi.mocked(registrationStatsService.getRecentRegistrations).mockResolvedValueOnce([]);

        const {result} = renderHook(() => useRegistrationStats());

        await waitFor(() => {
            expect(result.current.error).toBeNull();
            expect(result.current.loading).toBe(false);
            expect(result.current.stats).toEqual(mockStats);
        });
    });
})

describe('useRegistrationStats', () => {
    const mockStats = {
        totalRegistrations: 100,
        successfulRegistrations: 90,
        failedRegistrations: 10,
        successRate: 0.9,
        recentEvents: []
    };

    const mockEvents = [{
        eventId: "123e4567-e89b-12d3-a456-426614174000",
        eventType: "USER_REGISTRATION",
        timestamp: Date.now(),
        status: EventStatus.SUCCESS,
        id: 1,
        userId: 'test-user',
        email: 'test@example.com',
        name: 'Test User',
        companyName: 'Test Company',
        registrationTime: '2024-01-01T12:00:00Z',
        ipAddress: '127.0.0.1',
        userAgent: 'Mozilla/5.0',
        registrationSource: 'WEB'
    }];

    beforeEach(() => {
        vi.clearAllMocks();
        vi.spyOn(console, 'error').mockImplementation(() => {
        });
    });

    test('should initialize with default values', async () => {
        vi.mocked(registrationStatsService.getStats).mockResolvedValueOnce(mockStats);
        vi.mocked(registrationStatsService.getRecentRegistrations).mockResolvedValueOnce(mockEvents);

        const {result} = renderHook(() => useRegistrationStats());

        await waitFor(() => {
            expect(result.current.loading).toBe(false);
            expect(result.current.error).toBeNull();
            expect(result.current.stats).toEqual(mockStats);
            expect(result.current.recentEvents).toEqual(mockEvents);
        });
    });

    test('should fetch initial data on mount', async () => {
        vi.mocked(registrationStatsService.getStats).mockResolvedValueOnce(mockStats);
        vi.mocked(registrationStatsService.getRecentRegistrations).mockResolvedValueOnce(mockEvents);

        const {result} = renderHook(() => useRegistrationStats());

        expect(result.current.loading).toBe(true);

        await waitFor(() => {
            expect(result.current.loading).toBe(false);
            expect(result.current.stats).toEqual(mockStats);
            expect(result.current.recentEvents).toEqual(mockEvents);
        });

        expect(registrationStatsService.getStats).toHaveBeenCalledTimes(1);
        expect(registrationStatsService.getRecentRegistrations).toHaveBeenCalledTimes(1);
    });

    test('should handle fetch stats error', async () => {
        vi.mocked(registrationStatsService.getStats).mockRejectedValueOnce(new Error('API Error'));

        const {result} = renderHook(() => useRegistrationStats());

        await waitFor(() => {
            expect(result.current.error).toBe('kafka.errors.statsFetchFailed');
            expect(result.current.loading).toBe(false);
        });

        expect(console.error).toHaveBeenCalled();
    });

    test('should fetch recent events with custom limit', async () => {
        vi.mocked(registrationStatsService.getRecentRegistrations).mockResolvedValueOnce(mockEvents);
        vi.mocked(registrationStatsService.getRecentRegistrations)
            .mockResolvedValueOnce(mockEvents)
            .mockResolvedValueOnce(mockEvents);

        const {result} = renderHook(() => useRegistrationStats());

        await act(async () => {
            await result.current.fetchRecentEvents(5);
        });

        expect(registrationStatsService.getRecentRegistrations).toHaveBeenCalledWith(5);
        expect(result.current.recentEvents).toEqual(mockEvents);
        expect(result.current.loading).toBe(false);
        expect(result.current.error).toBeNull();
    });

    test('should fetch registrations for period', async () => {
        vi.mocked(registrationStatsService.getRegistrationsForPeriod).mockResolvedValueOnce(mockEvents);

        const startDate = new Date('2024-01-01');
        const endDate = new Date('2024-01-31');

        const {result} = renderHook(() => useRegistrationStats());

        await act(async () => {
            await result.current.fetchRegistrationsForPeriod(startDate, endDate);
        });

        expect(registrationStatsService.getRegistrationsForPeriod).toHaveBeenCalledWith(startDate, endDate);
        expect(result.current.recentEvents).toEqual(mockEvents);
        expect(result.current.loading).toBe(false);
        expect(result.current.error).toBeNull();
    });

    test('should handle fetch recent events error', async () => {
        vi.mocked(registrationStatsService.getRecentRegistrations).mockRejectedValueOnce(new Error('API Error'));

        const {result} = renderHook(() => useRegistrationStats());

        await act(async () => {
            await result.current.fetchRecentEvents();
        });

        expect(result.current.error).toBe('kafka.errors.recentEventsFetchFailed');
        expect(result.current.loading).toBe(false);
        expect(console.error).toHaveBeenCalled();
    });

    test('should handle fetch registrations for period error', async () => {
        vi.mocked(registrationStatsService.getRegistrationsForPeriod).mockRejectedValueOnce(new Error('API Error'));

        const startDate = new Date('2024-01-01');
        const endDate = new Date('2024-01-31');

        const {result} = renderHook(() => useRegistrationStats());

        await act(async () => {
            await result.current.fetchRegistrationsForPeriod(startDate, endDate);
        });

        expect(result.current.error).toBe('kafka.errors.registrationsForPeriodFetchFailed');
        expect(result.current.loading).toBe(false);
        expect(console.error).toHaveBeenCalled();
    });
});