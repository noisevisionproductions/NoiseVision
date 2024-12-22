import {StatsOverview} from "@/kafka/components/dashboard/registrations/StatsOverview";
import {render, screen} from "@testing-library/react";
import {vi} from "vitest";

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key: string) => {
            const translations: { [key: string]: string } = {
                'kafka.dashboard.registrations.stats.total': 'Total Registrations',
                'kafka.dashboard.registrations.stats.successful': 'Successful',
                'kafka.dashboard.registrations.stats.failed': 'Failed',
                'kafka.dashboard.registrations.stats.successRate': 'Success Rate'
            };
            return translations[key] || key;
        }
    })
}));

describe('StatsOverview', () => {
    const mockStats = {
        totalRegistrations: 100,
        successfulRegistrations: 80,
        failedRegistrations: 20,
        successRate: 80,
        recentEvents: []
    };

    test('should render all stat cards with correct values', () => {
        render(<StatsOverview stats={mockStats}/>);

        expect(screen.getByText('Total Registrations')).toBeInTheDocument();
        expect(screen.getByText('100')).toBeInTheDocument();

        expect(screen.getByText('Successful')).toBeInTheDocument();
        expect(screen.getByText('80')).toBeInTheDocument();

        expect(screen.getByText('Failed')).toBeInTheDocument();
        expect(screen.getByText('20')).toBeInTheDocument();

        expect(screen.getByText('Success Rate')).toBeInTheDocument();
        expect(screen.getByText('80%')).toBeInTheDocument();
    });

    test('should apply correct color classes to values', () => {
        render(<StatsOverview stats={mockStats}/>);

        expect(screen.getByText('100')).toHaveClass('text-blue-600');
        expect(screen.getByText('80')).toHaveClass('text-green-600');
        expect(screen.getByText('20')).toHaveClass('text-red-600');
        expect(screen.getByText('80%')).toHaveClass('text-purple-600');
    });

    test('should round success rate percentage', () => {
        const statsWithDecimal = {
            ...mockStats,
            successRate: 75.6789
        };

        render(<StatsOverview stats={statsWithDecimal}/>);
        expect(screen.getByText('76%')).toBeInTheDocument();
    });

    test('should render correct number of stat cards', () => {
        render(<StatsOverview stats={mockStats}/>);
        const statCards = screen.getAllByRole('generic').filter(
            element => element.className.includes('bg-white rounded-lg shadow p-6')
        );
        expect(statCards).toHaveLength(4);
    });
});