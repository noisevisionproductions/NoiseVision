import {BaseEvent} from "@/kafka/types/baseEvent";

export interface RegistrationEvent extends BaseEvent {
    id: number;
    userId: string;
    email: string;
    name: string | null;
    companyName: string | null;
    registrationTime: string;
    ipAddress: string | null;
    userAgent: string | null;
    registrationSource: string | null;
}

export interface RegistrationStats {
    totalRegistrations: number;
    successfulRegistrations: number;
    failedRegistrations: number;
    successRate: number;
    recentEvents: RegistrationEvent[];
}