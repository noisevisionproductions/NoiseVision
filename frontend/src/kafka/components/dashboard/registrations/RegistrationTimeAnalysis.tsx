import React, {useEffect, useMemo, useState} from "react";
import _ from 'lodash';
import {RegistrationEvent} from "@/kafka/types/registrationEvent";
import {useTranslation} from "react-i18next";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/Cards";
import {Bar, BarChart, ResponsiveContainer, Tooltip, XAxis, YAxis} from "recharts";
import {getGeoIpInfo} from "@/kafka/utils/ipMapping";

interface RegistrationTimeAnalysisProps {
    events: RegistrationEvent[];
}

export const RegistrationTimeAnalysis: React.FC<RegistrationTimeAnalysisProps> = ({events}) => {
    const {t} = useTranslation();
    const hourlyData = useMemo(() => {
        const grouped = _.groupBy(events, (event) => {
            const date = new Date(event.registrationTime);
            return date.getHours();
        });

        return _.range(0, 24).map(hour => ({
            hour: `${hour}:00`,
            count: (grouped[hour] || []).length
        }));
    }, [events]);

    interface GeoDataItem {
        region: string;
        count: number;
        percentage: string;
    }

    const [geoData, setGeoData] = useState<GeoDataItem[]>([]);

    useEffect(() => {
        const loadGeoData = async () => {
            const fallbackText = {
                unknown: t('common.unknown'),
                other: t('common.other'),
                invalid: t('common.invalidIp')
            };

            const uniqueIps = _.uniqBy(events, 'ipAddress');

            const locationPromises = uniqueIps.map(async event => {
                const geoInfo = await getGeoIpInfo(event.ipAddress, fallbackText);
                if (!geoInfo) return null;

                const count = events.filter(e => e.ipAddress === event.ipAddress).length;

                return {
                    region: `${geoInfo.country} - ${geoInfo.city}`,
                    count,
                    percentage: ((count / events.length) * 100).toFixed(1)
                } as GeoDataItem;
            });

            const locations = await Promise.all(locationPromises);
            const validLocations = locations.filter((location): location is GeoDataItem => location !== null);

            setGeoData(_.orderBy(validLocations, ['count'], ['desc']));
        };

        loadGeoData().catch(console.error);
    }, [events, t]);

    const formatValue = (value: number): string => `${value}`;

    return (
        <div className="space-y-6">
            <Card>
                <CardHeader>
                    <CardTitle>
                        {t('kafka.dashboard.registrations.registrationSources.schedule')}
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="h-64">
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart data={hourlyData}>
                                <XAxis dataKey="hour"/>
                                <YAxis/>
                                <Tooltip/>
                                <Bar dataKey="count" fill="#8884d8"/>
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </CardContent>
            </Card>

            <Card>
                <CardHeader>
                    <CardTitle>
                        {t('kafka.dashboard.registrations.registrationSources.geoDistribution')}
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="h-96">
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart
                                layout="vertical"
                                data={geoData}
                                margin={{
                                    top: 5,
                                    right: 30,
                                    left: 100,
                                    bottom: 5
                                }}
                            >
                                <YAxis
                                    type="category"
                                    dataKey="region"
                                    width={100}
                                />
                                <XAxis type="number"/>
                                <Tooltip
                                    formatter={(value: number): [string, string] => [
                                        `${value}`,
                                        t('kafka.dashboard.registrations.registrationsCount')
                                    ]}
                                    labelFormatter={() => ''}
                                    separator=": "
                                />
                                <Bar
                                    dataKey="count"
                                    fill="#82ca9d"
                                    label={{
                                        position: 'right',
                                        formatter: formatValue
                                    }}
                                />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </CardContent>
            </Card>
        </div>
    );
};