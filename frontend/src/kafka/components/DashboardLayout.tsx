import React, {useState} from "react";
import {useTranslation} from "react-i18next";
import {Activity, ChevronLeft, ChevronRight, Users} from "lucide-react";
import {Header} from "@/components/shared/Header";
import {Link, Outlet, useLocation} from "react-router-dom";

export const DashboardLayout: React.FC = () => {
    const [isSidebarOpen, setSidebarOpen] = useState(true);
    const {t} = useTranslation();
    const location = useLocation();

    const menuItems = [
        {
            icon: <Activity className="w-5 h-5 shrink-0"/>,
            label: 'kafka.dashboard.menu.overview',
            path: '/kafka-dashboard'
        },
        {
            icon: <Users className="w-5 h-5 shrink-0"/>,
            label: 'kafka.dashboard.menu.registrations',
            path: '/kafka-dashboard/registrations'
        }
    ];

    return (
        <div className="min-h-screen bg-gray-100">
            <Header
                title={t('header.title')}
                navigation={{
                    login: t('header.navigation.login'),
                    logout: t('header.navigation.logout')
                }}
            />

            <div className="flex pt-16">
                <div className={`fixed h-full bg-white shadow-lg transition-all duration-300 ${
                    isSidebarOpen ? 'w-64' : 'w-20'
                }`}>
                    <button
                        onClick={() => setSidebarOpen(!isSidebarOpen)}
                        className="absolute right-0 top-3 transform translate-x-1/2 rounded-full bg-white p-2 shadow-lg"
                    >
                        {isSidebarOpen ? <ChevronLeft/> : <ChevronRight/>}
                    </button>

                    <nav className="mt-8">
                        {menuItems.map((item, index) => (
                            <Link
                                key={index}
                                to={item.path}
                                className={`flex items-center px-4 py-3 transition-colors ${
                                    location.pathname === item.path
                                        ? 'bg-blue-50 text-blue-600'
                                        : 'hover:bg-gray-100'
                                }`}
                            >
                                {item.icon}
                                {isSidebarOpen && (
                                    <span className="ml-3">
                                        {t(item.label)}
                                    </span>
                                )}
                            </Link>
                        ))}
                    </nav>
                </div>

                <div className={`flex-1 transition-all duration-300 ${
                    isSidebarOpen ? 'ml-64' : 'ml-20'
                }`}>
                    <main className="p-6">
                        <Outlet/>
                    </main>
                </div>
            </div>
        </div>
    );
};