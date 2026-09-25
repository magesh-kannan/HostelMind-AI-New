import React from 'react';
import { Box } from '@mui/material';
import { Outlet } from 'react-router-dom';
import { Navbar } from '../components/common/Navbar';
import { Sidebar } from '../components/common/Sidebar';

export const DashboardLayout: React.FC = () => {
  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <Navbar />
      <Box sx={{ display: 'flex', flexGrow: 1 }}>
        <Sidebar />
        <Box component="main" sx={{ flexGrow: 1, p: 3, backgroundColor: 'background.default' }}>
          <Outlet />
        </Box>
      </Box>
    </Box>
  );
};
