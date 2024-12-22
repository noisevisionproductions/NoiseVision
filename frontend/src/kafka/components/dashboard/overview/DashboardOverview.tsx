import React from 'react';
import {useTranslation} from "react-i18next";
import {useRegistrationStats} from "@/kafka/hooks/useRegistrationStats";
import {useBaseProject} from "@/projects/hooks/useBaseProject";
import {Activity, FolderGit2, Users} from "lucide-react";
import {LoadingSpinner} from "@/components/shared/LoadingSpinner";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/Cards";

export const DashboardOverview: React.FC = () => {
    const {t} = useTranslation();
    const {stats: registrationStats, loading: registrationLoading} = useRegistrationStats();
    const {projects, loading: projectLoading} = useBaseProject();

    const statsCards = [
        {
            title: 'kafka.dashboard.overview.totalRegistrations',
            value: registrationStats?.totalRegistrations || 0,
            icon: <Users className="h-4 w-4 text-muted-foreground"/>,
            description: 'kafka.dashboard.overview.totalRegistrationsDesc'
        },
        {
            title: 'kafka.dashboard.overview.totalRegistrationsDesc',
            value: `${Math.round(registrationStats?.successRate || 0)}%`,
            icon: <Activity className="h-4 w-4 text-muted-foreground"/>,
            description: 'kafka.dashboard.overview.registrationSuccessDesc'
        },
        {
            title: 'kafka.dashboard.overview.totalRegistrationsDesc',
            value: projects?.length || 0,
            icon: <FolderGit2 className="h-4 w-4 text-muted-foreground"/>,
            description: 'kafka.dashboard.overview.totalProjectsDesc'
        },
    ];

    if (registrationLoading || projectLoading) {
        return <LoadingSpinner/>;
    }

    return (
        <div className="space-y-6">
            <h1 className="text-2xl font-bold">
                {t('kafka.dashboard.overview.title')}
            </h1>

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                {statsCards.map((card, index) => (
                    <Card key={index}>
                        <CardHeader>
                            <div className="flex items-center justify-between">
                                <CardTitle className="text-sm font-medium text-gray-600">
                                    {t(card.title)}
                                </CardTitle>
                                {card.icon}
                            </div>
                        </CardHeader>
                        <CardContent>
                            <div className="text-2xl font-bold">
                                {card.value}
                            </div>
                            <p className="text-sm text-gray-500 mt-1">
                                {t(card.description)}
                            </p>
                        </CardContent>
                    </Card>
                ))}
            </div>

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                <Card className="col-span-2">
                    <CardHeader>
                        <CardTitle>
                            {t('kafka.dashboard.overview.recentActivity')}
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <p className="text-gray-500">
                            {t('kafka.dashboard.overview.noRecentActivity')}
                        </p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle>
                            {t('kafka.dashboard.overview.quickActions')}
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <p className="text-gray-500">
                            {t('kafka.dashboard.overview.noQuickActions')}
                        </p>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
};