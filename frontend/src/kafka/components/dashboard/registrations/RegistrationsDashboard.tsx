import React from "react";
import {useTranslation} from "react-i18next";
import {useRegistrationStats} from "@/kafka/hooks/useRegistrationStats";
import {Loader2} from "lucide-react";
import {ErrorMessage} from "@/components/shared/ErrorMessage";
import {useNavigate} from "react-router-dom";
import {StatsOverview} from "@/kafka/components/dashboard/registrations/StatsOverview";
import {RegistrationsChart} from "@/kafka/components/dashboard/registrations/RegistrationsChart";
import {RecentRegistrations} from "@/kafka/components/dashboard/registrations/RecentRegistrations";

export const RegistrationsDashboard: React.FC = () => {
    const {t} = useTranslation();
    const navigate = useNavigate();
    const {stats, recentEvents, loading, error} = useRegistrationStats();

    if (loading) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <Loader2 className="h-8 w-8 animate-spin"/>
            </div>
        );
    }

    if (error) {
        return (
            <ErrorMessage
                error={error}
                onBack={() => navigate('/')}
                t={t}
            />
        )
    }

    return (
        <div className="space-y-6">
            <h1 className="text-2xl font-bold">
                {t('kafka.dashboard.registrations.title')}
            </h1>

            {stats && (
                <div className="space-y-6">
                    <StatsOverview stats={stats}/>
                    <div className="grid md:grid-cols-2 gap-6">
                        <div className="bg-white rounded-lg shadow p-6">
                            <h2 className="text-xl font-semibold mb-4">
                                {t('kafka.dashboard.registrations.chart.title')}
                            </h2>
                            <RegistrationsChart events={recentEvents}/>
                        </div>
                        <div className="bg-white rounded-lg shadow p-6">
                            <h2 className="text-xl font-semibold mb-4">
                                {t('kafka.dashboard.registrations.recent.title')}
                            </h2>
                            <RecentRegistrations events={recentEvents}/>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};