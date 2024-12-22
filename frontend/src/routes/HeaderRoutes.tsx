import {Route} from 'react-router-dom';
import ProjectFormPage from "../projects/components/projectCreate/ProjectFormPage";
import {Authority} from "@/auth/types/roles";
import {ProtectedRoute} from "@/auth/components/ProtectedRoute";
import {UnauthorizedPage} from "@/components/UnauthorizedPage";
import {ProjectDetailsPage} from "@/projects/components/projectDetails/ProjectDetailsPage";
import {RegisterPage} from "@/auth/components/register/RegisterPage";
import ProtectedLoginRoute from "../auth/components/login/ProtectedLoginRoute";
import {MainContent} from "@/App";
import React from 'react';
import {dashboardRoutes} from "@/routes/DashboardRoutes";

export const headerRoutes = (
    <React.Fragment>
        <Route path="/" element={<MainContent/>}/>
        <Route path="/login" element={<ProtectedLoginRoute/>}/>
        <Route path="/register" element={<RegisterPage/>}/>
        <Route path="/project/:slug" element={<ProjectDetailsPage/>}/>
        <Route path="/unauthorized" element={<UnauthorizedPage/>}/>

        <Route path="/add-project"
               element={
                   <ProtectedRoute requiredAuthorities={[Authority.CREATE_PROJECTS]}>
                       <ProjectFormPage/>
                   </ProtectedRoute>
               }
        />
        <Route path="/edit-project/:id"
               element={
                   <ProtectedRoute requiredAuthorities={[Authority.EDIT_PROJECTS]}>
                       <ProjectFormPage/>
                   </ProtectedRoute>
               }
        />

        {dashboardRoutes}
    </React.Fragment>
);