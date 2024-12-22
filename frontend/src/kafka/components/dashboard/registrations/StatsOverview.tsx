import React from 'react';
import {RegistrationStats} from "@/kafka/types/registrationEvent";
import {useTranslation} from "react-i18next";

interface StatsOverviewProps {
    stats: RegistrationStats;
}

export const StatsOverview: React.FC<StatsOverviewProps> = ({stats}) => {
    const {t} = useTranslation();

    const statCards = [
        {
            title: t('kafka.dashboard.registrations.stats.total'),
            value: stats.totalRegistrations,
            className: 'text-blue-600'
        },
        {
            title: t('kafka.dashboard.registrations.stats.successful'),
            value: stats.successfulRegistrations,
            className: 'text-green-600'
        },
        {
            title: t('kafka.dashboard.registrations.stats.failed'),
            value: stats.failedRegistrations,
            className: 'text-red-600'
        },
        {
            title: t('kafka.dashboard.registrations.stats.successRate'),
            value: `${Math.round(stats.successRate)}%`,
            className: 'text-purple-600'
        }
    ];

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {statCards.map((stat, index) => (
                <div key={index} className="bg-white rounded-lg shadow p-6">
                    <div className="flex flex-col">
                        <p className="text-sm font-medium text-gray-600">
                            {stat.title}
                        </p>
                        <p className={`text-2xl font-bold ${stat.className}`}>
                            {stat.value}
                        </p>
                    </div>

                </div>
            ))}
        </div>
    );
};