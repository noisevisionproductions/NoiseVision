import {RegistrationEvent} from "@/kafka/types/registrationEvent";
import {EventStatus} from "@/kafka/types/baseEvent";
import {Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis} from 'recharts';
import React from "react";
import {pl} from 'date-fns/locale';
import {eachDayOfInterval, endOfDay, format, startOfDay, subDays} from 'date-fns';

interface RegistrationsChartProps {
    events: RegistrationEvent[];
}

export const RegistrationsChart: React.FC<RegistrationsChartProps> = ({events}) => {
    const today = new Date();
    const last7Days = eachDayOfInterval({
        start: subDays(today, 6),
        end: today
    });

    const chartData = last7Days.map(date => {
        const dayStart = startOfDay(date);
        const dayEnd = endOfDay(date);

        const dayEvents = events.filter(event => {
            const eventDate = new Date(event.timestamp);
            return eventDate >= dayStart && eventDate <= dayEnd;
        });

        return {
            date: format(date, 'd MMM', {locale: pl}),
            success: dayEvents.filter(event => event.status === EventStatus.SUCCESS).length,
            failed: dayEvents.filter(event => event.status === EventStatus.FAILED).length
        };
    });

    return (
        <div className="h-[300px] w-full">
            <ResponsiveContainer>
                <BarChart data={chartData}>
                    <CartesianGrid strokeDasharray="3 3"/>
                    <XAxis dataKey="date"/>
                    <YAxis/>
                    <Tooltip/>
                    <Legend/>
                    <Bar dataKey="success" name="success" fill="#059669"/>
                    <Bar dataKey='failed' name="failed" fill="#dc2626"/>
                </BarChart>
            </ResponsiveContainer>
        </div>
    );
};