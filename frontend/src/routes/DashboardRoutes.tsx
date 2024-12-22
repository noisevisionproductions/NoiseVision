import {DashboardLayout} from "@/kafka/components/DashboardLayout";
import {RegistrationsDashboard} from '@/kafka/components/dashboard/registrations/RegistrationsDashboard';
import {Authority} from "@/auth/types/roles";
import {ProtectedRoute} from "@/auth/components/ProtectedRoute";
import {Route} from 'react-router-dom';
import {DashboardOverview} from "@/kafka/components/dashboard/overview/DashboardOverview";

export const dashboardRoutes = (
    <Route path="/kafka-dashboard"
           element={
               <ProtectedRoute requiredAuthorities={[Authority.ACCESS_KAFKA_DASHBOARD]}>
                   <DashboardLayout/>
               </ProtectedRoute>
           }
    >
        <Route index element={<DashboardOverview/>}/>
        <Route path="registrations" element={<RegistrationsDashboard/>}/>
    </Route>
);