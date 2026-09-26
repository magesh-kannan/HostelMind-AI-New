import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, CssBaseline } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider, useAuth } from './context/AuthContext';
import { getAppTheme } from './theme/theme';
import { LoginPage } from './pages/auth/LoginPage';
import { RegisterPage } from './pages/auth/RegisterPage';
import { DashboardLayout } from './layouts/DashboardLayout';
import { DashboardPage } from './pages/dashboard/DashboardPage';
import { RoomManagementPage } from './pages/hostel/RoomManagementPage';
import { AllocationPage } from './pages/hostel/AllocationPage';
import { ComplaintManagementPage } from './pages/complaints/ComplaintManagementPage';
import { AiAssistantPage } from './pages/ai/AiAssistantPage';
import { BillingManagementPage } from './pages/billing/BillingManagementPage';
import { MessManagementPage } from './pages/mess/MessManagementPage';
import { FacilityManagementPage } from './pages/facility/FacilityManagementPage';
import { AuditPage } from './pages/audit/AuditPage';
import { DigitalIdPage } from './pages/digitalid/DigitalIdPage';
import { CommunityPage } from './pages/community/CommunityPage';

const queryClient = new QueryClient();

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, loading } = useAuth();
  if (loading) return null;
  if (!user) return <Navigate to="/login" replace />;
  return <>{children}</>;
};

const AppContent: React.FC = () => {
  const { mode } = useAuth();
  const theme = getAppTheme(mode);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route
            path="/"
            element={
              <ProtectedRoute>
                <DashboardLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<DashboardPage />} />
            <Route path="rooms" element={<RoomManagementPage />} />
            <Route path="allocations" element={<AllocationPage />} />
            <Route path="complaints" element={<ComplaintManagementPage />} />
            <Route path="ai" element={<AiAssistantPage />} />
            <Route path="fees" element={<BillingManagementPage />} />
            <Route path="mess" element={<MessManagementPage />} />
            <Route path="facilities" element={<FacilityManagementPage />} />
            <Route path="audit" element={<AuditPage />} />
            <Route path="digital-id" element={<DigitalIdPage />} />
            <Route path="chat" element={<CommunityPage />} />
            <Route path="*" element={<DashboardPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
};

export const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </QueryClientProvider>
  );
};

export default App;
