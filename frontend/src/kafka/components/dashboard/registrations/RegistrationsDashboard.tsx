import React from "react";
import {useTranslation} from "react-i18next";
import {useRegistrationStats} from "@/kafka/hooks/useRegistrationStats";
import {Loader2} from "lucide-react";
import {ErrorMessage} from "@/components/shared/ErrorMessage";
import {useNavigate} from "react-router-dom";
import {StatsOverview} from "@/kafka/components/dashboard/registrations/StatsOverview";
import {RecentRegistrations} from "@/kafka/components/dashboard/registrations/RecentRegistrations";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/Tabs";
import {RegistrationSources} from "@/kafka/components/dashboard/registrations/RegistrationSources";
import {RegistrationTimeAnalysis} from "@/kafka/components/dashboard/registrations/RegistrationTimeAnalysis";

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

                    <Tabs defaultValue="sources" className="w-full">
                        <TabsList>
                            <TabsTrigger value="sources">
                                {t('kafka.dashboard.registrations.registrationSources.registrationSources')}
                            </TabsTrigger>
                            <TabsTrigger value="time">
                                {t('kafka.dashboard.registrations.timeAnalysis')}
                            </TabsTrigger>
                            <TabsTrigger value="recent">
                                {t('kafka.dashboard.registrations.latestRegistrations')}
                            </TabsTrigger>
                        </TabsList>
                        <TabsContent value="sources">
                            <RegistrationSources events={recentEvents}/>
                        </TabsContent>

                        <TabsContent value="time">
                            <RegistrationTimeAnalysis events={recentEvents}/>
                        </TabsContent>

                        <TabsContent value="recent">
                            <div className="bg-white rounded-lg shadow p-6">
                                <RecentRegistrations events={recentEvents}/>
                            </div>
                        </TabsContent>
                    </Tabs>
                </div>
            )}
        </div>
    );
};