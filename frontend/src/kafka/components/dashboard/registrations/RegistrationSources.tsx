import React, {useMemo} from "react";
import {RegistrationEvent} from "@/kafka/types/registrationEvent";
import _ from 'lodash';
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/Cards";
import {useTranslation} from "react-i18next";
import {Cell, Pie, PieChart, ResponsiveContainer, Tooltip} from "recharts";

interface RegistrationSourcesProps {
    events: RegistrationEvent[];
}

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8'];

export const RegistrationSources: React.FC<RegistrationSourcesProps> = ({events}) => {
    const {t} = useTranslation();
    const sourceStats = useMemo(() => {
        const grouped = _.groupBy(events, 'registrationSource');
        return Object.entries(grouped).map(([source, items]) => ({
            name: source || t('common.unknown'),
            value: items.length,
        }));
    }, [events]);

    const browserStats = useMemo(() => {
        const grouped = _.groupBy(events, (event) => {
            const agent = event.userAgent;
            if (!agent) return t('common.unknown');
            if (agent.includes('Chrome')) return 'Chrome';
            if (agent.includes('Firefox')) return 'Firefox';
            if (agent.includes('Safari')) return 'Safari';
            if (agent.includes('Edge')) return 'Edge';
            return t('common.other');
        });
        return Object.entries(grouped).map(([browser, items]) => ({
            name: browser,
            value: items.length,
        }));
    }, [events])

    return (
        <div className="grid md:grid-cols-2 gap-6">
            <Card>
                <CardHeader>
                    <CardTitle>
                        {t('kafka.dashboard.registrations.registrationSources.registrationSource')}
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="h-64">
                        <ResponsiveContainer width="100%" height="100%">
                            <PieChart>
                                <Pie
                                    data={sourceStats}
                                    cx="50%"
                                    cy="50%"
                                    outerRadius={80}
                                    fill="#8884d8"
                                    dataKey="value"
                                    label={({name, percent}) =>
                                        `${name}: ${(percent * 100).toFixed(0)}%`
                                    }
                                >
                                    {sourceStats.map((entry, index) => (
                                        <Cell
                                            key={entry.name}
                                            fill={COLORS[index % COLORS.length]}
                                        />
                                    ))}
                                </Pie>
                                <Tooltip/>
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                </CardContent>
            </Card>

            <Card>
                <CardHeader>
                    <CardTitle>
                        {t('kafka.dashboard.registrations.registrationSources.browsers')}
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="h-64">
                        <ResponsiveContainer width="100%" height="100%">
                            <PieChart>
                                <Pie
                                    data={browserStats}
                                    cx="50%"
                                    cy="50%"
                                    outerRadius={80}
                                    fill="#8884d8"
                                    dataKey="value"
                                    label={({name, percent}) =>
                                        `${name}: ${(percent * 100).toFixed(0)}%`
                                    }
                                >
                                    {browserStats.map((entry, index) => (
                                        <Cell
                                            key={entry.name}
                                            fill={COLORS[index % COLORS.length]}
                                        />
                                    ))}
                                </Pie>
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                </CardContent>
            </Card>
        </div>
    );
};