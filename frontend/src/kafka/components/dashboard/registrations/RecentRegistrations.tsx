import {EventStatus} from "@/kafka/types/baseEvent";
import React from "react";
import {useTranslation} from "react-i18next";
import {format} from 'date-fns';
import {pl} from 'date-fns/locale';
import {RegistrationEvent} from "@/kafka/types/registrationEvent";

interface RecentRegistrationsProps {
    events: RegistrationEvent[];
}

export const RecentRegistrations: React.FC<RecentRegistrationsProps> = ({events}) => {
    const {t} = useTranslation();

    const getStatusClass = (status: EventStatus) => {
        return status === EventStatus.SUCCESS ? 'text-green-600' : 'text-red-600';
    };

    return (
        <div className="overflow-x-auto">
            <table className="w-full text-sm">
                <thead>
                <tr className="border-b">
                    <th className="text-left py-2 px-4">{t('kafka.dashboard.registrations.table.status')}</th>
                    <th className="text-left py-2 px-4">{t('kafka.dashboard.registrations.table.email')}</th>
                    <th className="text-left py-2 px-4">{t('kafka.dashboard.registrations.table.date')}</th>
                    <th className="text-left py-2 px-4">{t('kafka.dashboard.registrations.table.source')}</th>
                </tr>
                </thead>
                <tbody>
                {events.map((event) => (
                    <tr key={event.id} className="border-b hover:bg-gray-50">
                        <td className={`py-2 px-4 ${getStatusClass(event.status)}`}>
                            {event.status === EventStatus.SUCCESS ? '✓' : '✗'}
                        </td>
                        <td className="py-2 px-4">{event.email}</td>
                        <td className="py-2 px-4">
                            {format(new Date(event.timestamp), 'PPp', {locale: pl})}
                        </td>
                        <td className="py-2 px-4">{event.registrationSource || '-'}</td>
                    </tr>
                ))}
                </tbody>
            </table>
            {events.length === 0 && (
                <p className="text-center py-4 text-gray-500">
                    {t('kafka.dashboard.registrations.table.noData')}
                </p>
            )}
        </div>
    );
};