export interface BaseEvent {
    eventId: string;
    eventType: string;
    timestamp: number;
    status: EventStatus;
}

export enum EventStatus {
    SUCCESS = 'SUCCESS',
    FAILED = 'FAILED'
}