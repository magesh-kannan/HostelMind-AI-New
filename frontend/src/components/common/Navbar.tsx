import React from 'react';
import { AppBar, Toolbar, Typography, IconButton, Box, Avatar, Tooltip } from '@mui/material';
import { Sun, Moon, LogOut, Bot } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const Navbar: React.FC = () => {
  const { user, logout, toggleTheme, mode } = useAuth();

  return (
    <AppBar
      position="sticky"
      elevation={0}
      sx={{
        backgroundColor: mode === 'dark' ? 'rgba(30, 41, 59, 0.85)' : 'rgba(255, 255, 255, 0.85)',
        backdropFilter: 'blur(12px)',
        borderBottom: 1,
        borderColor: 'divider',
        color: 'text.primary',
      }}
    >
      <Toolbar sx={{ justifyContent: 'space-between' }}>
        <Box display="flex" alignItems="center" gap={1.5}>
          <Box
            sx={{
              p: 1,
              borderRadius: 2,
              background: 'linear-gradient(135deg, #4F46E5 0%, #10B981 100%)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#fff',
            }}
          >
            <Bot size={22} />
          </Box>
          <Box>
            <Typography variant="h6" sx={{ fontWeight: 800, letterSpacing: -0.5 }}>
              HostelMind<Typography component="span" variant="h6" color="primary.light" sx={{ fontWeight: 800 }}>-AI</Typography>
            </Typography>
            <Typography variant="caption" color="text.secondary" sx={{ display: 'block', marginTop: -0.5, fontSize: '0.65rem' }}>
              Institutional Operations Platform
            </Typography>
          </Box>
        </Box>

        <Box display="flex" alignItems="center" gap={2}>
          <Tooltip title="Toggle light/dark mode">
            <IconButton onClick={toggleTheme} color="inherit" size="small">
              {mode === 'dark' ? <Sun size={18} /> : <Moon size={18} />}
            </IconButton>
          </Tooltip>

          {user && (
            <Box display="flex" alignItems="center" gap={1.5}>
              <Avatar
                sx={{
                  width: 34,
                  height: 34,
                  bgcolor: 'primary.main',
                  fontSize: '0.9rem',
                  fontWeight: 700,
                }}
              >
                {user.fullName.charAt(0)}
              </Avatar>
              <Box sx={{ display: { xs: 'none', sm: 'block' } }}>
                <Typography variant="subtitle2" sx={{ fontWeight: 600, lineHeight: 1.2 }}>
                  {user.fullName}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {user.roles.map(r => r.replace('ROLE_', '')).join(', ')}
                </Typography>
              </Box>

              <Tooltip title="Logout">
                <IconButton onClick={logout} color="error" size="small">
                  <LogOut size={18} />
                </IconButton>
              </Tooltip>
            </Box>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
};
