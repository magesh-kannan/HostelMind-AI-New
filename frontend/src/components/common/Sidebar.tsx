import React from 'react';
import { Box, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Typography, Divider } from '@mui/material';
import { LayoutDashboard, AlertCircle, Home, CreditCard, Utensils, Zap, MessageSquare, ShieldCheck, UserCheck } from 'lucide-react';
import { useNavigate, useLocation } from 'react-router-dom';

const menuItems = [
  { text: 'Dashboard', icon: <LayoutDashboard size={20} />, path: '/' },
  { text: 'Hostel & Rooms', icon: <Home size={20} />, path: '/rooms' },
  { text: 'Room Allocations', icon: <UserCheck size={20} />, path: '/allocations' },
  { text: 'Complaints AI', icon: <AlertCircle size={20} />, path: '/complaints' },
  { text: 'Fees & Invoices', icon: <CreditCard size={20} />, path: '/fees' },
  { text: 'Mess Planner', icon: <Utensils size={20} />, path: '/mess' },
  { text: 'Facilities & Emergency', icon: <Zap size={20} />, path: '/facilities' },
  { text: 'Community Chat', icon: <MessageSquare size={20} />, path: '/chat' },
  { text: 'Digital ID Card', icon: <UserCheck size={20} />, path: '/digital-id' },
  { text: 'Audit Logs & AI Runs', icon: <ShieldCheck size={20} />, path: '/audit' },
];

export const Sidebar: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <Box
      sx={{
        width: 250,
        flexShrink: 0,
        height: 'calc(100vh - 64px)',
        position: 'sticky',
        top: 64,
        borderRight: 1,
        borderColor: 'divider',
        backgroundColor: 'background.paper',
        p: 2,
      }}
    >
      <Typography variant="overline" color="text.secondary" sx={{ fontWeight: 700, letterSpacing: 1, px: 1 }}>
        OPERATIONS NAVIGATOR
      </Typography>
      <List sx={{ mt: 1 }}>
        {menuItems.map((item) => {
          const isActive = location.pathname === item.path;
          return (
            <ListItem key={item.text} disablePadding sx={{ mb: 0.5 }}>
              <ListItemButton
                selected={isActive}
                onClick={() => navigate(item.path)}
                sx={{
                  borderRadius: 2,
                  py: 1,
                  px: 1.5,
                  '&.Mui-selected': {
                    backgroundColor: 'primary.main',
                    color: '#fff',
                    '& .MuiListItemIcon-root': { color: '#fff' },
                    '&:hover': { backgroundColor: 'primary.dark' },
                  },
                }}
              >
                <ListItemIcon sx={{ minWidth: 36, color: isActive ? '#fff' : 'text.secondary' }}>
                  {item.icon}
                </ListItemIcon>
                <ListItemText
                  primary={item.text}
                  primaryTypographyProps={{ fontSize: '0.875rem', fontWeight: isActive ? 600 : 500 }}
                />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
      <Divider sx={{ my: 2 }} />
      <Box sx={{ p: 1.5, borderRadius: 2, bgcolor: 'action.hover' }}>
        <Typography variant="caption" sx={{ fontWeight: 600, display: 'block', color: 'primary.light' }}>
          Zero-Cost Architecture
        </Typography>
        <Typography variant="caption" color="text.secondary">
          Vercel + Render + Neon PostgreSQL
        </Typography>
      </Box>
    </Box>
  );
};
